package com.orbitalsonic.storiessample.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.orbitalsonic.storiessample.presentation.models.ItemStoryCategoryLib
import com.orbitalsonic.storiessample.presentation.ui.fragments.FragmentStory

class PagerStories(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    private val stories: List<ItemStoryCategoryLib>
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {

        return FragmentStory.newInstance(stories[position])
    }

    override fun getItemCount(): Int = stories.size
}