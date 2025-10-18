package com.orbitalsonic.storiessample.presentation.ui.fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.orbitalsonic.storiessample.base.BaseFragment
import com.orbitalsonic.storiessample.data.dataSources.local.entities.ItemStory
import com.orbitalsonic.storiessample.databinding.FragmentStoryBinding
import com.orbitalsonic.storiessample.domain.useCases.UseCaseStorySeen
import com.orbitalsonic.storiessample.presentation.ui.activities.ActivityStories
import com.orbitalsonic.storiessample.presentation.viewModels.DownloadState
import com.orbitalsonic.storiessample.presentation.viewModels.ViewModelDownload
import com.orbitalsonic.storiessample.utilities.extensions.showToast
import com.orbitalsonic.storiessample.utilities.utils.Constants
import com.orbitalsonic.storiessample.utilities.utils.Constants.TAG
import dev.epegasus.storyview.StoryView
import dev.epegasus.storyview.listeners.OnStoryChangeListener
import dev.epegasus.storyview.listeners.OnStoryClickListener
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentStory : BaseFragment<FragmentStoryBinding>(FragmentStoryBinding::inflate) {

    private val useCaseStorySeen: UseCaseStorySeen by inject()
    private val downloadViewModel: ViewModelDownload by viewModel()

    private var storyView: StoryView.Builder? = null
    private var currentPosition = 0
    private var isStoriesShowing = false

    private val itemStory by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_ITEM_STORY, ItemStory::class.java)
        } else {
            arguments?.getParcelable(ARG_ITEM_STORY)
        }
    }

    override fun onViewCreated() {
        setupDownloadObservers()
    }

    override fun onResume() {
        super.onResume()
        // Only show stories if not already showing (prevents duplicate calls)
        if (!isStoriesShowing) {
            showStories()
        }
    }

    override fun onPause() {
        super.onPause()
        storyView?.dismiss()
        storyView = null
        isStoriesShowing = false
    }

    /**
     * Restart stories from the beginning when category changes
     */
    fun restartStories() {
        Log.d(TAG, "Restarting stories for: ${itemStory?.headerText}")
        currentPosition = 0 // Reset to first story

        // Always dismiss current stories and show fresh ones
        storyView?.dismiss()
        storyView = null
        isStoriesShowing = false
        showStories()
    }

    private fun showStories() {
        val storyData = itemStory ?: return

        // Prevent duplicate story showing
        if (isStoriesShowing) {
            Log.d(TAG, "Stories already showing, skipping duplicate call")
            return
        }

        // Validate story data
        if (storyData.storyList.isEmpty()) {
            Log.w(TAG, "Story list is empty for story: ${storyData.headerText}")
            exitScreen()
            return
        }

        // Validate current position
        if (currentPosition < 0 || currentPosition >= storyData.storyList.size) {
            Log.w(TAG, "Invalid current position: $currentPosition, resetting to 0")
            currentPosition = 0
        }

        isStoriesShowing = true

        storyView = StoryView.Builder(childFragmentManager)
            .setHeaderTitleText(storyData.headerText)
            .setHeaderSubtitleText(storyData.subHeaderText)
            .setHeaderTitleLogoUrl(storyData.headerUrl)
            .setStartingIndex(currentPosition)
            .setStoryDuration(Constants.DEFAULT_STORY_DURATION)
            .setStoriesList(ArrayList(storyData.storyList))
            .setOnStoryClickListener(object : OnStoryClickListener {
                override fun onTitleIconClickListener(position: Int) {}
                override fun onDescriptionClickListener(position: Int) {}
                override fun onDownloadClickListener(position: Int, imageUrl: String) {
                    Log.d(TAG, "FragmentStory: onDownloadClickListener: Position: $position, URL: $imageUrl")
                    handleDownloadClick(imageUrl)
                }
            })
            .setOnStoryChangeListener(object : OnStoryChangeListener {
                override fun storyChanged(position: Int) {
                    if (position >= 0 && position < storyData.storyList.size) {
                        this@FragmentStory.currentPosition = position
                        // Mark story as seen
                        markStoryAsSeen(position)
                    }
                }

                override fun storySwiped(swipeDirection: Int) {
                    Log.d(TAG, "storySwiped: $swipeDirection")
                    ActivityStories.storySwipedLiveData.value = swipeDirection
                }

                override fun storyDismiss() {
                    isStoriesShowing = false
                    exitScreen()
                }
            })
            .setRtl(false)
            .build()

        // pass the container id where the StoryView fragment should appear
        storyView?.show(binding.clContainerStory.id)
    }

    private fun exitScreen() {
        activity?.finish()
        activity?.overridePendingTransition(0, 0)
    }

    private fun markStoryAsSeen(storyPosition: Int) {
        val storyData = itemStory ?: return
        if (storyPosition >= 0 && storyPosition < storyData.storyList.size) {
            // Mark individual story as seen
            val storyId = storyData.id * 1000 + storyPosition // Create unique ID
            viewLifecycleOwner.lifecycleScope.launch {
                useCaseStorySeen.markStoryAsSeen(storyId, storyData.id)
            }
        }
    }

    private fun setupDownloadObservers() {
        downloadViewModel.downloadState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DownloadState.Loading -> {
                    Log.d(TAG, "FragmentStory: Download started")
                }

                is DownloadState.Downloading -> {
                    Log.d(TAG, "FragmentStory: Download in progress: ${state.downloadId}")
                }

                is DownloadState.Success -> {
                    Log.d(TAG, "FragmentStory: Download completed: ${state.message}")
                    // Show success message to user
                    showDownloadMessage("Download completed successfully!")
                }

                is DownloadState.Error -> {
                    Log.e(TAG, "FragmentStory: Download failed: ${state.message}")
                    // Show error message to user
                    showDownloadMessage("Download failed: ${state.message}")
                }

                is DownloadState.Cancelled -> {
                    Log.d(TAG, "FragmentStory: Download cancelled")
                    showDownloadMessage("Download cancelled")
                }
            }
        }
    }

    /**
     * Handle download button click
     */
    private fun handleDownloadClick(imageUrl: String) {
        val storyData = itemStory ?: return
        Log.d(TAG, "FragmentStory: handleDownloadClick: Downloading image from: $imageUrl")

        // Start download
        downloadViewModel.downloadStoryImage(imageUrl, storyData.headerText)
    }

    /**
     * Show download message to user
     */
    private fun showDownloadMessage(message: String) {
        context.showToast(message)
        // You can implement a toast, snackbar, or any other UI feedback here
        Log.d(TAG, "FragmentStory: showDownloadMessage: $message")
        // For now, just log the message. You can add UI feedback later.
    }

    companion object {
        private const val ARG_ITEM_STORY = "arg_item_story"

        fun newInstance(story: ItemStory): Fragment {
            return FragmentStory().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_ITEM_STORY, story)
                }
            }
        }
    }
}