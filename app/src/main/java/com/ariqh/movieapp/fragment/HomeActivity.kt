package com.ariqh.movieapp.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ariqh.movieapp.adapter.TabAdapter
import com.ariqh.movieapp.databinding.ActivityHomeBinding
import com.google.android.material.tabs.TabLayoutMediator

class HomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tabAdapter = TabAdapter(supportFragmentManager, lifecycle)
        binding.viewPager.adapter = tabAdapter

        val tabTitles = arrayOf("Popular", "Now Playing")
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }
}