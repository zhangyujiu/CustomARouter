package com.jk.customarouter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.jk.annotation.Router
import com.jk.arouter_api.ARouterNavigation

@Router(path = "app/MainActivity")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.gotoOrder).setOnClickListener {
            ARouterNavigation.build("order/OrderActivity").navigation(this)
        }
    }
}