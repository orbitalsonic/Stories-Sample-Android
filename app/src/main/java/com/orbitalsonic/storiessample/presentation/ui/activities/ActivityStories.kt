package com.orbitalsonic.storiessample.presentation.ui.activities

import com.orbitalsonic.storiessample.base.BaseActivity
import com.orbitalsonic.storiessample.data.dataSources.local.entities.ItemStoryCategoryLib
import com.orbitalsonic.storiessample.databinding.ActivityStoriesBinding
import com.orbitalsonic.storiessample.presentation.adapters.PagerStories
import com.orbitalsonic.storiessample.presentation.viewModels.ViewModelStories
import com.orbitalsonic.storiessample.utilities.observers.SingleLiveEvent
import com.orbitalsonic.storiessample.utilities.viewPager.ZoomOutPageTransformer
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActivityStories : BaseActivity<ActivityStoriesBinding>(ActivityStoriesBinding::inflate) {

    private val viewModel by viewModel<ViewModelStories>()
    private val startIndex by lazy { intent.getIntExtra(EXTRA_START_INDEX, 0) }

    override fun onCreated() {
        initObserver()
    }

    private fun initObserver() {
        viewModel.listLiveData.observe(this) { initViewPager(it) }
        storySwipedLiveData.observe(this) { onSwipeViewPager(it) }
    }

    private fun initViewPager(stories: List<ItemStoryCategoryLib>) {
        if (stories.isEmpty()) {
            finish()
            return
        }

        binding.viewPager.adapter = PagerStories(supportFragmentManager, lifecycle, stories)
        binding.viewPager.setPageTransformer(ZoomOutPageTransformer())
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.offscreenPageLimit = 1

        // Set initial position
        if (startIndex < stories.size) {
            binding.viewPager.setCurrentItem(startIndex, false)
        }

        // Add page change listener to restart stories when category changes
        binding.viewPager.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Notify the current fragment to restart its stories
                restartCurrentStory()
            }
        })
    }

    private fun restartCurrentStory() {
        val currentFragment = supportFragmentManager.fragments.find { it.isVisible }
        if (currentFragment is com.orbitalsonic.storiessample.presentation.ui.fragments.FragmentStory) {
            currentFragment.restartStories()
        }
    }

    private fun onSwipeViewPager(i: Int) {
        val currentItem = binding.viewPager.currentItem
        val adapter = binding.viewPager.adapter
        val totalItems = adapter?.itemCount ?: 0

        if (totalItems == 0) return

        if (i == 0) {  // left to right
            if (currentItem > 0) {
                binding.viewPager.currentItem = currentItem - 1
            }
        } else {
            if (currentItem < totalItems - 1) {
                binding.viewPager.currentItem = currentItem + 1
            }
        }
    }

    companion object {
        val storySwipedLiveData = SingleLiveEvent<Int>()
        const val EXTRA_START_INDEX = "extra_start_index"
    }
}