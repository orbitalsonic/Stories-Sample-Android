package com.orbitalsonic.storiessample.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.orbitalsonic.storiessample.data.dataSources.local.entities.ItemStory
import com.orbitalsonic.storiessample.presentation.ui.fragments.FragmentStory

class PagerStories(fragmentManager: FragmentManager, lifecycle: Lifecycle, private val stories: List<ItemStory>) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun createFragment(position: Int): Fragment {
        return FragmentStory.newInstance(stories[position])
    }

    override fun getItemCount(): Int = stories.size
}