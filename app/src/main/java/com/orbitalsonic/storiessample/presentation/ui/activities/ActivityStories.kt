package com.orbitalsonic.storiessample.presentation.ui.activities

import com.orbitalsonic.storiessample.base.BaseActivity
import com.orbitalsonic.storiessample.data.entities.ItemStory
import com.orbitalsonic.storiessample.databinding.ActivityStoriesBinding
import com.orbitalsonic.storiessample.presentation.adapters.PagerStories
import com.orbitalsonic.storiessample.presentation.viewModels.ViewModelStories
import com.orbitalsonic.storiessample.utilities.observers.SingleLiveEvent
import com.orbitalsonic.storiessample.utilities.viewPager.ZoomOutPageTransformer
import org.koin.androidx.viewmodel.ext.android.viewModel

class ActivityStories : BaseActivity<ActivityStoriesBinding>(ActivityStoriesBinding::inflate) {

    private val viewModel by viewModel<ViewModelStories>()

    override fun onCreated() {
        initObserver()
    }

    private fun initObserver() {
        viewModel.listLiveData.observe(this) { initViewPager(it) }

        liveData.observe(this) {
            val currentItem = binding.viewPager.currentItem
            val adapter = binding.viewPager.adapter
            val totalItems = adapter?.itemCount ?: 0

            if (totalItems == 0) return@observe

            if (it == 0) {  // left to right
                if (currentItem > 0) {
                    binding.viewPager.currentItem = currentItem - 1
                }
            } else {
                if (currentItem < totalItems - 1) {
                    binding.viewPager.currentItem = currentItem + 1
                }
            }
        }
    }

    private fun initViewPager(stories: List<ItemStory>) {
        if (stories.isEmpty()) {
            finish()
            return
        }

        binding.viewPager.adapter = PagerStories(supportFragmentManager, lifecycle, stories)
        binding.viewPager.setPageTransformer(ZoomOutPageTransformer())
        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.offscreenPageLimit = 1
    }


    companion object {
        val liveData = SingleLiveEvent<Int>()
    }
}