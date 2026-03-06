package com.orbitalsonic.storiessample.presentation.ui.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.orbitalsonic.storiessample.base.BaseFragment
import com.orbitalsonic.storiessample.databinding.FragmentStoryBinding
import com.orbitalsonic.storiessample.presentation.models.ItemStoryCategoryLib
import com.orbitalsonic.storiessample.presentation.ui.activities.StoryActivity
import com.orbitalsonic.storiessample.utilities.utils.Constants
import dev.epegasus.storyview.StoryView
import dev.epegasus.storyview.listeners.OnStoryChangeListener
import dev.epegasus.storyview.listeners.OnStoryClickListener

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


    override fun onViewCreated() { }

    override fun onResume() {
        super.onResume()

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

    fun restartStories() {

        currentPosition = 0

        storyView?.dismiss()
        storyView = null
        isStoriesShowing = false

        showStories()
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

//                    context.showToast("Download clicked")
                }
            })
            .setOnStoryChangeListener(object : OnStoryChangeListener {

                override fun storyChanged(position: Int) {
                    currentPosition = position
                }

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


    companion object {

        private const val ARG_ITEM_STORY = "arg_item_story"

        fun newInstance(story: ItemStoryCategoryLib): Fragment {

            return FragmentStory().apply {

                arguments = Bundle().apply {

                    putParcelable(ARG_ITEM_STORY, story)
                }
            }
        }
    }
}