package com.orbitalsonic.storiessample.presentation.ui.fragments

import android.util.Log
import com.orbitalsonic.storiessample.base.BaseFragment
import com.orbitalsonic.storiessample.databinding.FragmentStoryBinding
import com.orbitalsonic.storiessample.presentation.ui.activities.ActivityStories
import com.orbitalsonic.storiessample.utilities.utils.Constants.TAG
import dev.epegasus.storyview.StoryView
import dev.epegasus.storyview.dataClasses.MyStory
import dev.epegasus.storyview.listeners.OnStoryChangeListener
import dev.epegasus.storyview.listeners.OnStoryClickListener

class FragmentStory : BaseFragment<FragmentStoryBinding>(FragmentStoryBinding::inflate) {

    private var storyView: StoryView.Builder? = null
    private var currentPosition = 0

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
        val myStories = ArrayList<MyStory>()

        val story1 = MyStory(url = "https://media.istockphoto.com/id/517188688/photo/mountain-landscape.jpg?s=1024x1024&w=0&k=20&c=z8_rWaI8x4zApNEEG9DnWlGXyDIXe-OmsAyQ5fGPVV8=")
        val story2 = MyStory(url = "https://static.boredpanda.com/blog/wp-content/uploads/2017/11/My-most-popular-pic-since-I-started-dog-photography-5a0b38cbd5e1e__880.jpg")
        val story3 = MyStory(url = "https://images.pexels.com/photos/36029/aroni-arsa-children-little.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500")
        myStories.add(story1)
        myStories.add(story2)
        myStories.add(story3)

        storyView = StoryView.Builder(childFragmentManager)
            .setHeaderTitleText("Pegasus")
            .setHeaderSubtitleText("Android is Love")
            .setHeaderTitleLogoUrl("https://images.pexels.com/photos/36029/aroni-arsa-children-little.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500")
            .setStartingIndex(currentPosition)
            .setStoryDuration(5000)
            .setStoriesList(myStories)
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
            .setStartingIndex(0)
            .setRtl(false)
            .build()

        storyView?.show(binding.clContainerStory.id)
    }

    private fun exitScreen() {
        activity?.finish()
        activity?.overridePendingTransition(0, 0)
    }
}