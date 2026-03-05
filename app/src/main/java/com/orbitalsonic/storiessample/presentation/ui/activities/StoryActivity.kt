package com.orbitalsonic.storiessample.presentation.ui.activities

import com.orbitalsonic.storiessample.base.BaseActivity
import com.orbitalsonic.storiessample.databinding.ActivityStoryBinding
import com.orbitalsonic.storiessample.presentation.viewModels.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class StoryActivity :
    BaseActivity<ActivityStoryBinding>(ActivityStoryBinding::inflate) {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreated() {

        val title = intent.getStringExtra("title")
        val id = intent.getIntExtra("id", -1)

        binding.titleText.text = title

        binding.btnSeen.setOnClickListener {

            viewModel.markSeen(id)

            finish()
        }
    }
}