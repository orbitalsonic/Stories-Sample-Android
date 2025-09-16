package com.orbitalsonic.storiessample.presentation.ui.activities

import android.util.Log
import android.widget.Toast
import com.orbitalsonic.storiessample.base.BaseActivity
import com.orbitalsonic.storiessample.databinding.ActivityMainBinding
import dev.epegasus.storyview.StoryView
import dev.epegasus.storyview.dataClasses.MyStory
import dev.epegasus.storyview.listeners.OnStoryChangeListener
import dev.epegasus.storyview.listeners.OnStoryClickListener

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    override fun onCreated() {
        binding.mtvTitle.setOnClickListener { onShowClick() }
    }

    private fun onShowClick() {
        val myStories = ArrayList<MyStory>()

        val story1 = MyStory(url = "https://media.istockphoto.com/id/517188688/photo/mountain-landscape.jpg?s=1024x1024&w=0&k=20&c=z8_rWaI8x4zApNEEG9DnWlGXyDIXe-OmsAyQ5fGPVV8=")
        val story2 = MyStory(url = "https://static.boredpanda.com/blog/wp-content/uploads/2017/11/My-most-popular-pic-since-I-started-dog-photography-5a0b38cbd5e1e__880.jpg")
        val story3 = MyStory(url = "https://images.pexels.com/photos/36029/aroni-arsa-children-little.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500")
        myStories.add(story1)
        myStories.add(story2)
        myStories.add(story3)

        StoryView.Builder(supportFragmentManager)
            .setHeaderTitleText("Pegasus")
            .setHeaderSubtitleText("Android is Love")
            .setHeaderTitleLogoUrl("https://images.pexels.com/photos/36029/aroni-arsa-children-little.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500")
            .setStoryDuration(5000)
            .setStoriesList(myStories)
            .setOnStoryClickListener(object : OnStoryClickListener {
                override fun onTitleIconClickListener(position: Int) {

                }

                override fun onDescriptionClickListener(position: Int) {
                    showToast(myStories[position].description.toString())
                }
            })
            .setOnStoryChangeListener(object : OnStoryChangeListener {
                override fun storyChanged(position: Int) {}
                override fun storySwipe(position: Int) {
                    showToast("Story Swiped: $position")
                }
            })
            .setStartingIndex(0)
            .setRtl(false)
            .build()
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
    }
}