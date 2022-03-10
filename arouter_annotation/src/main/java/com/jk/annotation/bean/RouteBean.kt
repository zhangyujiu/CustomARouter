package com.jk.annotation.bean

import javax.lang.model.element.Element


class RouteBean {

    enum class TypeEnum {
        ACTIVITY,
        FRAGMENT
    }

    var group: String? = null

    var path: String? = null

    var clazz: Class<*>? = null

    var typeEnum: TypeEnum? = null

    var element: Element? = null
}