package com.jk.person

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.jk.annotation.Router

@Router(path = "person/PersonActivity")
class PersonActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person)
    }
}