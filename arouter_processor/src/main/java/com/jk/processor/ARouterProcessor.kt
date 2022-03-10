package com.jk.processor

import com.google.auto.service.AutoService
import com.jk.annotation.Router
import com.jk.annotation.bean.RouteBean
import com.jk.processor.utils.javaToKotlinType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(ProcessorConfig.OPTIONS)
@SupportedAnnotationTypes(ProcessorConfig.ROUTER_NAME)
class ARouterProcessor : AbstractProcessor() {
    // 日志工具类
    private lateinit var mMessage: Messager

    // 文件操作类
    private lateinit var mFiler: Filer

    // 类型工具类
    private lateinit var mTypeTools: Types

    private lateinit var mElementUtils: Elements

    private var mModuleName: String? = null

    private val mPathMap = mutableMapOf<String, MutableList<RouteBean>>()

    // 生成类的包名
    private val mGeneratePackage = "com.jk.route.generate"

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        if (processingEnv == null) return
        mMessage = processingEnv.messager
        mFiler = processingEnv.filer
        mElementUtils = processingEnv.elementUtils
        mTypeTools = processingEnv.typeUtils
        mModuleName = processingEnv.options[ProcessorConfig.OPTIONS]
    }

    override fun process(annotations: MutableSet<out TypeElement>?, roundEnv: RoundEnvironment?): Boolean {

        if (annotations.isNullOrEmpty() || roundEnv == null) {
            mMessage.printMessage(Diagnostic.Kind.NOTE, "没有地方使用注解")
            return true
        }

        if (mModuleName.isNullOrEmpty()) {
            mMessage.printMessage(
                Diagnostic.Kind.NOTE,
                """
                        |请在gradle中进行配置
                        |kapt {
                        |    arguments {
                        |        arg("moduleName", project.getName())
                        |     }
                        |}
                    """.trimMargin()
            )
        }

        // 获取所有的被注解的节点
        val elements = roundEnv.getElementsAnnotatedWith(Router::class.java)

        if (elements.isEmpty()) {
            return true
        }

        // 获取activity的类型，转换成TypeMirror，用于判断
        val activityType = mElementUtils.getTypeElement(ProcessorConfig.ACTIVITY_PACKAGE).asType()
        // 获取fragment的类型，转换成TypeMirror，用于判断
        val fragmentType = mElementUtils.getTypeElement(ProcessorConfig.FRAGMENT_PACKAGE).asType()

        elements.forEach {
            val className = it.simpleName.toString()
            mMessage.printMessage(Diagnostic.Kind.NOTE, "类名：${className}")

            // 获取注解的path变量
            val iRouter = it.getAnnotation(Router::class.java)
            val path = iRouter.path

            if (path.isEmpty()) {
                mMessage.printMessage(Diagnostic.Kind.NOTE, "${className}中path不能为空")
            }

            val routeBean = RouteBean().apply {
                this.group = mModuleName
                this.path = iRouter.path
                this.element = it
            }

            when {
                mTypeTools.isSubtype(it.asType(), activityType) -> {
                    routeBean.typeEnum = RouteBean.TypeEnum.ACTIVITY
                }
                mTypeTools.isSubtype(it.asType(), fragmentType) -> {
                    routeBean.typeEnum = RouteBean.TypeEnum.FRAGMENT
                }
                else -> {
                    mMessage.printMessage(
                        Diagnostic.Kind.ERROR,
                        "@Router注解目前仅限用于Activity和Fragment类之上"
                    )
                }
            }

            val routeBeanList = mPathMap[routeBean.group]

            if (routeBeanList.isNullOrEmpty()) {
                val list = mutableListOf<RouteBean>()
                list.add(routeBean)
                mPathMap[routeBean.group!!] = list
            } else {
                routeBeanList.add(routeBean)
            }

            mPathMap.forEach {
                mMessage.printMessage(Diagnostic.Kind.NOTE, "map ${it.key}--- > ${it.value}")
            }
        }

        // 生成path文件
        generatePathFile()
        // 生成group文件
        generateGroupFile()
        return false
    }

    private fun generatePathFile() {
//        class OrderRouterPath : IRouterPath {
//            override fun getPath(): Map<String, RouteBean> {
//                val map = mutableMapOf<String, RouteBean>()
//                // 订单详情
//                map["order/detail"] = RouteBean().apply {
//                    group = "order"
//                    path = "order/detail"
//                    clazz = OrderDetailActivity::class.java
//                    typeEnum = RouteBean.TypeEnum.ACTIVITY
//                }
//                return map
//            }
//        }

        // 获取某个模块的List<RouteBean>
        val routeList = mPathMap[mModuleName]
        if (routeList.isNullOrEmpty()) {
            mMessage.printMessage(Diagnostic.Kind.NOTE, "${mModuleName}中没有地方使用注解")
            return
        }

        // 方法返回类型，泛型为MutableMap<String,RouteBean>
        val returnType = Map::class.java.asClassName().parameterizedBy(
            String::class.java.asTypeName().javaToKotlinType(),
            RouteBean::class.asTypeName().javaToKotlinType()
        ).javaToKotlinType()

        // 创建方法，方法名为 getPath
        val funcSpecBuilder = FunSpec.builder(ProcessorConfig.PATH_METHOD_NAME)
            // override关键字
            .addModifiers(KModifier.OVERRIDE)
            // 返回map
            .returns(returnType)
            .addStatement(
                "val %N = mutableMapOf<%T, %T>()",
                ProcessorConfig.PATH_VAR_MAP,
                String::class.java.asTypeName().javaToKotlinType(),
                RouteBean::class.java.asTypeName().javaToKotlinType()
            )

        // 添加语句
        routeList.forEach {
            funcSpecBuilder.addStatement(
                """
                    | %N[%S] = %T().apply { 
                    |   group = %S
                    |   path = %S
                    |   clazz = %T::class.java
                    |   typeEnum = %T.%L
                    |}
                    |
                """.trimMargin(),
                ProcessorConfig.PATH_VAR_MAP,
                it.path ?: "",
                RouteBean::class.java.asTypeName().javaToKotlinType(),
                it.group ?: "",
                it.path ?: "",
                it.element!!.asType().asTypeName(),
                RouteBean.TypeEnum::class.java.asTypeName().javaToKotlinType(),
                it.typeEnum!!
            )
        }
        funcSpecBuilder.addStatement("return %N", ProcessorConfig.PATH_VAR_MAP)

        val superInter = ClassName("com.jk.arouter_api", "IRouterPath")
        val fileName = "RouterPath_${mModuleName}"
        val typeSpec = TypeSpec.classBuilder(fileName)
            .addFunction(funcSpecBuilder.build())
            .addSuperinterface(superInter)
            .build()

        // 创建文件
        FileSpec.builder(mGeneratePackage, fileName)
            .addType(typeSpec)
            .build()
            .writeTo(mFiler)
    }

    private fun generateGroupFile() {
//        class OrderRouterGroup : IRouterGroup {
//
//            override fun getGroupMap(): MutableMap<String, Class<out IRouterPath>> {
//                val map = mutableMapOf<String, Class<out IRouterPath>>()
//                map["component_order"] = OrderRouterPath::class.java
//                return map
//            }
//        }

        val routePathInter = ClassName("com.jk.arouter_api", "IRouterPath")
        // 方法返回类型 MutableMap<String,Class<out IRouterPath>>
        val returnType = MutableMap::class.java.asClassName().parameterizedBy(
            String::class.java.asTypeName().javaToKotlinType(),
            Class::class.java.asClassName().parameterizedBy(
                WildcardTypeName.producerOf(routePathInter)
            )
        ).javaToKotlinType()

        // path生成的类名
        val putClazz = ClassName(mGeneratePackage, "RouterPath_${mModuleName}")

        val funSpec = FunSpec.builder(ProcessorConfig.GROUP_METHOD_NAME)
            .returns(returnType)
            .addModifiers(KModifier.OVERRIDE)
            .addStatement(
                "val %N = mutableMapOf<%T, %T>()",
                ProcessorConfig.GROUP_VAR_MAP,
                String::class.java.asTypeName().javaToKotlinType(),
                Class::class.java.asClassName().parameterizedBy(
                    WildcardTypeName.producerOf(routePathInter)
                )
            )
            .addStatement(
                "%N[%S] = %T::class.java",
                ProcessorConfig.GROUP_VAR_MAP,
                mModuleName ?: "",
                putClazz
            )
            .addStatement("return %N", ProcessorConfig.GROUP_VAR_MAP)
            .build()

        val superInter = ClassName("com.jk.arouter_api", "IRouterGroup")
        val fileName = "RouteGroup_${mModuleName}"

        val typeSpec = TypeSpec.classBuilder(fileName)
            .addSuperinterface(superInter)
            .addFunction(funSpec)
            .build()

        FileSpec.builder(mGeneratePackage, fileName)
            .addType(typeSpec)
            .build()
            .writeTo(mFiler)

    }
}