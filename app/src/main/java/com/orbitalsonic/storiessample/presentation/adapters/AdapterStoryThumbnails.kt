package com.orbitalsonic.storiessample.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orbitalsonic.storiessample.R
import com.orbitalsonic.storiessample.data.dataSources.local.entities.ItemStoryCategoryLib
import com.orbitalsonic.storiessample.databinding.ItemStoryThumbnailBinding

class AdapterStoryThumbnails(private val onItemClick: (ItemStoryCategoryLib) -> Unit) : ListAdapter<ItemStoryCategoryLib, AdapterStoryThumbnails.CustomViewHolder>(StoryDiffCallback) {

    private var seenStatusMap = mapOf<Int, Boolean>()

    /**
     * Update seen status for stories
     */
    fun updateSeenStatus(newSeenStatus: Map<Int, Boolean>) {
        seenStatusMap = newSeenStatus
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = ItemStoryThumbnailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val story = getItem(position)
        val isSeen = seenStatusMap[story.id] ?: false

        holder.bind(story, isSeen, onItemClick)

        holder.binding.root.setOnClickListener { onItemClick(story) }
    }

    inner class CustomViewHolder(val binding: ItemStoryThumbnailBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(story: ItemStoryCategoryLib, isSeen: Boolean, onItemClick: (ItemStoryCategoryLib) -> Unit) {
            with(binding) {
                Glide.with(root.context).load(story.headerUrl).into(ifvThumbnail)

                mtvStoryTitle.text = story.headerText

                val ringDrawable = if (isSeen) {
                    R.drawable.circle_ring_seen
                } else {
                    R.drawable.circle_ring_unseen
                }
                sivRing.setBackgroundResource(ringDrawable)
            }
        }
    }

    companion object {
        object StoryDiffCallback : DiffUtil.ItemCallback<ItemStoryCategoryLib>() {
            override fun areItemsTheSame(oldItem: ItemStoryCategoryLib, newItem: ItemStoryCategoryLib): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ItemStoryCategoryLib, newItem: ItemStoryCategoryLib): Boolean {
                return oldItem == newItem
            }
        }
    }
}