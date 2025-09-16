package com.orbitalsonic.storiessample.presentation.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.orbitalsonic.storiessample.base.BaseFragment
import com.orbitalsonic.storiessample.data.entities.ItemStory
import com.orbitalsonic.storiessample.databinding.FragmentStoryBinding
import com.orbitalsonic.storiessample.presentation.ui.activities.ActivityStories
import com.orbitalsonic.storiessample.utilities.utils.Constants.TAG
import dev.epegasus.storyview.StoryView
import dev.epegasus.storyview.listeners.OnStoryChangeListener
import dev.epegasus.storyview.listeners.OnStoryClickListener

class FragmentStory : BaseFragment<FragmentStoryBinding>(FragmentStoryBinding::inflate) {

    private var storyView: StoryView.Builder? = null
    private var currentPosition = 0

    private val itemStory by lazy { arguments?.getParcelable(ARG_ITEM_STORY, ItemStory::class.java) }

    override fun onViewCreated() {}

    override fun onResume() {
        super.onResume()
        showStories()
    }

    override fun onPause() {
        super.onPause()
        storyView?.dismiss()
        storyView = null
    }

    private fun showStories() {
        val storyData = itemStory ?: return

        storyView = StoryView.Builder(childFragmentManager)
            .setHeaderTitleText(storyData.headerText)
            .setHeaderSubtitleText(storyData.subHeaderText)
            .setHeaderTitleLogoUrl(storyData.headerUrl)
            .setStartingIndex(currentPosition)
            .setStoryDuration(5000)
            .setStoriesList(ArrayList(storyData.storyList))
            .setOnStoryClickListener(object : OnStoryClickListener {
                override fun onTitleIconClickListener(position: Int) {}
                override fun onDescriptionClickListener(position: Int) {}
            })
            .setOnStoryChangeListener(object : OnStoryChangeListener {
                override fun storyChanged(position: Int) {
                    this@FragmentStory.currentPosition = position
                }

                override fun storySwiped(swipeDirection: Int) {
                    Log.d(TAG, "storySwiped: $swipeDirection")
                    ActivityStories.liveData.value = swipeDirection
                }

                override fun storyDismiss() {
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