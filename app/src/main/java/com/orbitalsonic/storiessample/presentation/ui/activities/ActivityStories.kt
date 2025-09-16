package com.orbitalsonic.storiessample.presentation.ui.activities

import com.orbitalsonic.storiessample.base.BaseActivity
import com.orbitalsonic.storiessample.databinding.ActivityStoriesBinding
import com.orbitalsonic.storiessample.presentation.adapters.PagerStories
import com.orbitalsonic.storiessample.utilities.observers.SingleLiveEvent
import com.orbitalsonic.storiessample.utilities.viewPager.ZoomOutPageTransformer

class ActivityStories : BaseActivity<ActivityStoriesBinding>(ActivityStoriesBinding::inflate) {

    override fun onCreated() {
        initViewPager()
        initObserver()
    }

    private fun initViewPager() {
        binding.viewPager.adapter = PagerStories(supportFragmentManager, lifecycle)
        binding.viewPager.setPageTransformer(ZoomOutPageTransformer())
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.offscreenPageLimit = 1
    }

    private fun initObserver() {
        liveData.observe(this) {
            val currentItem = binding.viewPager.currentItem

            if (it == 0) {  // left to right
                if (currentItem > 0) {
                    binding.viewPager.currentItem = currentItem - 1
                }
            } else {
                if (currentItem < 2) {
                    binding.viewPager.currentItem = currentItem + 1
                }
            }
        }
    }

    companion object {
        val liveData = SingleLiveEvent<Int>()
    }
}