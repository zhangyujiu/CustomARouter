package com.jk.processor


class ProcessorConfig {

    companion object {

        const val OPTIONS = "moduleName"

        const val ROUTER_NAME = "com.jk.annotation.Router"

        const val ACTIVITY_PACKAGE = "android.app.Activity"

        const val FRAGMENT_PACKAGE = "androidx.fragment.app.Fragment"

        // path的方法名
        const val PATH_METHOD_NAME = "getPath"

        // path中pathMap的变量名
        const val PATH_VAR_MAP = "pathMap"

        // 组的方法名
        const val GROUP_METHOD_NAME = "getGroupMap"

        const val GROUP_VAR_MAP = "groupMap"

    }
}