package com.orbitalsonic.storiessample.presentation.ui.activities

import com.orbitalsonic.storiessample.base.BaseActivity
import com.orbitalsonic.storiessample.databinding.ActivityStoryBinding
import com.orbitalsonic.storiessample.presentation.adapters.PagerStories
import com.orbitalsonic.storiessample.presentation.models.ItemStoryCategoryLib
import com.orbitalsonic.storiessample.presentation.viewModels.MainViewModel
import com.orbitalsonic.storiessample.utilities.extensions.toItemStoryCategory
import com.orbitalsonic.storiessample.utilities.observers.SingleLiveEvent
import com.orbitalsonic.storiessample.utilities.viewPager.ZoomOutPageTransformer
import org.koin.androidx.viewmodel.ext.android.viewModel

class StoryActivity :
    BaseActivity<ActivityStoryBinding>(ActivityStoryBinding::inflate) {

    private val viewModel: MainViewModel by viewModel()

    private val categoryTitle by lazy {
        intent.getStringExtra("title") ?: ""
    }

    private val categoryId by lazy {
        intent.getIntExtra("id", -1)
    }

    override fun onCreated() {

        initObserver()
        loadStories()
    }

    private fun loadStories() {

        viewModel.getStoryList(categoryTitle).observe(this) { categories ->

            if (categories.isEmpty()) {
                finish()
                return@observe
            }

            val storyList: List<ItemStoryCategoryLib> =
                categories.map { it.toItemStoryCategory() }

            initViewPager(storyList)

            // mark clicked category as seen
            viewModel.markSeen(categoryId)
        }
    }

    private fun initObserver() {

        storySwipedLiveData.observe(this) {
            onSwipeViewPager(it)
        }
    }

    private fun initViewPager(stories: List<ItemStoryCategoryLib>) {

        binding.viewPager.adapter =
            PagerStories(supportFragmentManager, lifecycle, stories)

        binding.viewPager.setPageTransformer(ZoomOutPageTransformer())

        binding.viewPager.isUserInputEnabled = false
        binding.viewPager.offscreenPageLimit = 1
    }

    private fun onSwipeViewPager(direction: Int) {

        val currentItem = binding.viewPager.currentItem
        val adapter = binding.viewPager.adapter
        val totalItems = adapter?.itemCount ?: 0

        if (totalItems == 0) return

        if (direction == 0) {

            // swipe previous
            if (currentItem > 0) {
                binding.viewPager.currentItem = currentItem - 1
            }

        } else {

            // swipe next
            if (currentItem < totalItems - 1) {
                binding.viewPager.currentItem = currentItem + 1
            }
        }
    }

    companion object {

        val storySwipedLiveData = SingleLiveEvent<Int>()
    }
}