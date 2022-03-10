package com.jk.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.jk.annotation.Router
import com.jk.arouter_api.ARouterNavigation

@Router(path = "order/OrderActivity")
class OrderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        findViewById<View>(R.id.gotoPerson).setOnClickListener {
            ARouterNavigation.build("person/PersonActivity").navigation(this)
        }
    }
}