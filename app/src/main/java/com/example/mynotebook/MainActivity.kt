package com.example.mynotebook

import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_fragment)




        supportActionBar?.apply {
            val color: Int = getColor(R.color.black)
            val colorDrawable = ColorDrawable(color)
            setBackgroundDrawable(colorDrawable)

        }







    }
}