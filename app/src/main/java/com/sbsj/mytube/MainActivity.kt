package com.sbsj.mytube

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //프래그먼트를 액티비티안에 붙힘.
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer,PlayerFragment())
            .commit()

    }
}