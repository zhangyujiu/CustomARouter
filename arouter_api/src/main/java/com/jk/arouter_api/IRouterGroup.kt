package com.jk.arouter_api


interface IRouterGroup {

    fun getGroupMap(): Map<String, Class<out IRouterPath>>
}