package com.orbitalsonic.storiessample.presentation.ui.activities

import android.content.Intent
import com.orbitalsonic.storiessample.base.BaseActivity
import com.orbitalsonic.storiessample.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun onCreated() {
        binding.mbOpen.setOnClickListener { navigateScreen() }
    }

    private fun navigateScreen() {
        startActivity(Intent(this, ActivityStories::class.java))
    }
}