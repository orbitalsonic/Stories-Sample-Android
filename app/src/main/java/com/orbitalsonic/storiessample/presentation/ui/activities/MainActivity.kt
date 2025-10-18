package com.orbitalsonic.storiessample.presentation.ui.activities

import android.content.Intent
import androidx.core.view.isVisible
import com.orbitalsonic.storiessample.base.BaseActivity
import com.orbitalsonic.storiessample.databinding.ActivityMainBinding
import com.orbitalsonic.storiessample.presentation.adapters.AdapterStoryThumbnails
import com.orbitalsonic.storiessample.presentation.viewModels.ViewModelMain
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Main Activity for displaying story thumbnails
 * Follows clean architecture principles with minimal business logic
 */
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val viewModel by viewModel<ViewModelMain>()
    private val adapter by lazy { AdapterStoryThumbnails { viewModel.onItemClick(it) } }

    override fun onCreated() {
        initRecyclerView()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshSeenStatus()
        viewModel.cleanupOldEntries()
    }

    private fun initRecyclerView() {
        binding.rvStoryThumbnails.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { binding.progressBar.isVisible = it }
        viewModel.storiesLiveData.observe(this) { adapter.submitList(it) }
        viewModel.seenStatusLiveData.observe(this) { adapter.updateSeenStatus(it) }
        viewModel.navigateLiveData.observe(this) { navigateToStories(it) }
    }

    /**
     * Navigate to stories activity
     * @param startIndex The index to start the stories from
     */
    private fun navigateToStories(startIndex: Int) {
        val intent = Intent(this, ActivityStories::class.java).apply {
            putExtra(ActivityStories.EXTRA_START_INDEX, startIndex)
        }
        startActivity(intent)
    }
}