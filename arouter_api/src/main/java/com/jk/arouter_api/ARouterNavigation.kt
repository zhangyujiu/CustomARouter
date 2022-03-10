package com.jk.arouter_api

import android.content.Context
import android.content.Intent

class ARouterNavigation {

    private var mPath = ""

    companion object {

        private val instance by lazy { ARouterNavigation() }

        fun build(path: String): ARouterNavigation {
            val utils = instance
            utils.mPath = path
            return utils
        }
    }

    fun navigation(context: Context) {
        val finalGroup: String = mPath.split("/")[0] // finalGroup = order

        // 找到组map
        val groupClazz =
            Class.forName("com.jk.route.generate.RouteGroup_${finalGroup}")
        val groupInstance = groupClazz.newInstance() as IRouterGroup
        // 通过组找到路径的map
        val pathInstance = (groupInstance.getGroupMap()[finalGroup]
            ?: error("")).newInstance() as IRouterPath
        // 通过路径的map找到组对应的routeBean
        val routeBean = pathInstance.getPath()[mPath]
        // 找到对应的class进行跳转
        val clazz = routeBean!!.clazz
        context.startActivity(Intent(context, clazz))
    }
}