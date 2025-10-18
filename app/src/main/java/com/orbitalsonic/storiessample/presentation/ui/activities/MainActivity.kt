package com.orbitalsonic.storiessample.presentation.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.view.isVisible
import com.orbitalsonic.storiessample.base.BaseActivity
import com.orbitalsonic.storiessample.data.dataSources.entities.ItemStory
import com.orbitalsonic.storiessample.databinding.ActivityMainBinding
import com.orbitalsonic.storiessample.presentation.adapters.AdapterStoryThumbnails
import com.orbitalsonic.storiessample.presentation.viewModels.ViewModelMain
import com.orbitalsonic.storiessample.utilities.utils.Constants.TAG
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Main Activity for displaying story thumbnails
 * Follows clean architecture principles with minimal business logic
 */
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val viewModel by viewModel<ViewModelMain>()
    private lateinit var storyAdapter: AdapterStoryThumbnails

    override fun onCreated() {
        setupUI()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        // Refresh seen status when returning from stories
        viewModel.refreshSeenStatus()
        // Cleanup old database entries
        viewModel.cleanupOldEntries()
    }

    /**
     * Setup UI components
     */
    private fun setupUI() {
        setupRecyclerView()
    }

    /**
     * Setup RecyclerView with adapter
     */
    private fun setupRecyclerView() {
        storyAdapter = AdapterStoryThumbnails { story ->
            viewModel.onItemClick(story)
        }
        
        binding.rvStoryThumbnails.apply {
            adapter = storyAdapter
            layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
                this@MainActivity,
                androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
                false
            )
        }
    }

    /**
     * Setup LiveData observers
     */
    private fun setupObservers() {
        // Loading state
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        // Stories data
        viewModel.storiesLiveData.observe(this) { stories ->
            Log.d(TAG, "MainActivity: Received ${stories.size} stories from ViewModel")
            storyAdapter.submitList(stories)
        }

        // Seen status updates
        viewModel.seenStatusLiveData.observe(this) { seenStatus ->
            storyAdapter.updateSeenStatus(seenStatus)
        }

        // Navigation
        viewModel.navigateLiveData.observe(this) { storyIndex ->
            navigateToStories(storyIndex)
        }
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