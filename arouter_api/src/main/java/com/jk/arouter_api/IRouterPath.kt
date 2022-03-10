package com.jk.arouter_api

import com.jk.annotation.bean.RouteBean

interface IRouterPath {

    fun getPath(): Map<String, RouteBean>
}