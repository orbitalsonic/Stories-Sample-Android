package com.orbitalsonic.storiessample.presentation.ui.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.orbitalsonic.storiessample.base.BaseFragment
import com.orbitalsonic.storiessample.databinding.FragmentStoryBinding
import com.orbitalsonic.storiessample.presentation.models.ItemStoryCategoryLib
import com.orbitalsonic.storiessample.presentation.ui.activities.StoryActivity
import com.orbitalsonic.storiessample.presentation.viewModels.StoryDownloadViewModel
import com.orbitalsonic.storiessample.utilities.extensions.showToast
import com.orbitalsonic.storiessample.utilities.utils.Constants
import dev.epegasus.storyview.StoryView
import dev.epegasus.storyview.listeners.OnStoryChangeListener
import dev.epegasus.storyview.listeners.OnStoryClickListener
import kotlin.getValue

class FragmentStory :
    BaseFragment<FragmentStoryBinding>(FragmentStoryBinding::inflate) {

    private var storyView: StoryView.Builder? = null
    private var currentPosition = 0
    private var isStoriesShowing = false

    private val itemStoryCategoryLib by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_ITEM_STORY, ItemStoryCategoryLib::class.java)
        } else {
            arguments?.getParcelable(ARG_ITEM_STORY)
        }
    }

    private val downloadViewModel: StoryDownloadViewModel by lazy {
        StoryDownloadViewModel(requireContext())
    }

    // Activity Result launcher for requesting storage permission
    private val storagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                pendingDownload?.let { (url, fileName) ->
                    downloadViewModel.downloadStory(url, fileName){}
                    context?.showToast("Download started")
                }
            } else {
                context?.showToast("Storage permission denied")
            }
            pendingDownload = null
        }

    // Keep track of a download request waiting for permission
    private var pendingDownload: Pair<String, String>? = null

    override fun onViewCreated() { }

    private fun startDownload(url: String, fileName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Scoped storage: no permission needed
            downloadViewModel.downloadStory(url, fileName){}
            context?.showToast("Download started")
        } else {
            // Pre-Q: Need WRITE_EXTERNAL_STORAGE permission
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                downloadViewModel.downloadStory(url, fileName){}
                context?.showToast("Download started")
            } else {
                // Save pending download and request permission
                pendingDownload = url to fileName
                storagePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

    }

    private fun showStories() {
        val storyData = itemStoryCategoryLib ?: return
        if (storyData.storyList.isEmpty()) {
            exitScreen()
            return
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
                    val timeStamp = System.currentTimeMillis()
                    val fileName = "${storyData.headerText}_$timeStamp.jpg"
                    startDownload(imageUrl, fileName)
                }
            })
            .setOnStoryChangeListener(object : OnStoryChangeListener {
                override fun storyChanged(position: Int) { currentPosition = position }
                override fun storySwiped(swipeDirection: Int) {
                    StoryActivity.storySwipedLiveData.value = swipeDirection
                }
                override fun storyDismiss() {
                    isStoriesShowing = false
                    exitScreen()
                }
            })
            .setRtl(false)
            .build()

        storyView?.show(binding.clContainerStory.id)
    }

    private fun exitScreen() {
        activity?.finish()
        activity?.overridePendingTransition(0, 0)
    }

    fun restartStories() {
        currentPosition = 0
        storyView?.dismiss()
        storyView = null
        isStoriesShowing = false
        showStories()
    }

    override fun onResume() {
        super.onResume()
        if (!isStoriesShowing) showStories()
    }

    override fun onPause() {
        super.onPause()
        storyView?.dismiss()
        storyView = null
        isStoriesShowing = false
    }


    companion object {
        private const val ARG_ITEM_STORY = "arg_item_story"
        fun newInstance(story: ItemStoryCategoryLib): Fragment {
            return FragmentStory().apply {
                arguments = Bundle().apply { putParcelable(ARG_ITEM_STORY, story) }
            }
        }
    }
}