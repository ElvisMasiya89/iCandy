package com.intellicco.icandy


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.intellicco.icandy.R.style.Theme_ICandyToonApp
import com.intellicco.icandy.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setTheme(Theme_ICandyToonApp)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)

    }




}