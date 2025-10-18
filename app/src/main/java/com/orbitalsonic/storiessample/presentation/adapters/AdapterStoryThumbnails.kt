package com.orbitalsonic.storiessample.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orbitalsonic.storiessample.R
import com.orbitalsonic.storiessample.data.dataSources.entities.ItemStory
import com.orbitalsonic.storiessample.databinding.ItemStoryThumbnailBinding
import com.orbitalsonic.storiessample.utilities.utils.Constants.TAG

/**
 * Adapter for displaying story thumbnails in a horizontal RecyclerView
 * Handles seen/unseen status with visual indicators
 */
class AdapterStoryThumbnails(
    private val onItemClick: (ItemStory) -> Unit
) : ListAdapter<ItemStory, AdapterStoryThumbnails.StoryThumbnailViewHolder>(StoryDiffCallback) {

    private var seenStatusMap = mapOf<Int, Boolean>()

    /**
     * Update seen status for stories
     */
    fun updateSeenStatus(newSeenStatus: Map<Int, Boolean>) {
        seenStatusMap = newSeenStatus
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryThumbnailViewHolder {
        val binding = ItemStoryThumbnailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StoryThumbnailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryThumbnailViewHolder, position: Int) {
        val story = getItem(position)
        val isSeen = seenStatusMap[story.id] ?: false
        
        Log.d(TAG, "AdapterStoryThumbnails: onBindViewHolder: Binding story ${story.headerText} at position $position")
        holder.bind(story, isSeen, onItemClick)
    }

    /**
     * ViewHolder for story thumbnail items
     */
    inner class StoryThumbnailViewHolder(
        private val binding: ItemStoryThumbnailBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ItemStory, isSeen: Boolean, onItemClick: (ItemStory) -> Unit) {
            with(binding) {
                Log.d(TAG, "AdapterStoryThumbnails: bind: Setting up story ${story.headerText}")
                
                // Load story thumbnail image
                Glide.with(root.context)
                    .load(story.headerUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .centerCrop()
                    .into(ifvThumbnail)

                // Set story title
                mtvStoryTitle.text = story.headerText

                // Set ring drawable based on seen status
                val ringDrawable = if (isSeen) {
                    R.drawable.circle_ring_seen
                } else {
                    R.drawable.circle_ring_unseen
                }
                sivRing.setBackgroundResource(ringDrawable)

                // Handle click
                root.setOnClickListener { 
                    Log.d(TAG, "AdapterStoryThumbnails: Click detected on story ${story.headerText}")
                    onItemClick(story) 
                }
            }
        }
    }

    companion object {
        /**
         * DiffUtil callback for efficient list updates
         */
        object StoryDiffCallback : DiffUtil.ItemCallback<ItemStory>() {
            override fun areItemsTheSame(oldItem: ItemStory, newItem: ItemStory): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ItemStory, newItem: ItemStory): Boolean {
                return oldItem == newItem
            }
        }
    }
}