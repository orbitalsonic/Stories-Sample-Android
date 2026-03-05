package com.orbitalsonic.storiessample.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.orbitalsonic.storiessample.R
import com.orbitalsonic.storiessample.databinding.ItemCategoryBinding
import com.orbitalsonic.storiessample.presentation.models.Category

class CategoryAdapter(
    private val onClick: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.VH>(Diff()) {

    inner class VH(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ItemCategoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)

        with(holder.binding) {
            mtvStoryTitle.text = item.title

            val ringDrawable = if (item.isSeen) {
                R.drawable.circle_ring_seen
            } else {
                R.drawable.circle_ring_unseen
            }
            sivRing.setBackgroundResource(ringDrawable)
        }

        Glide.with(holder.itemView)
            .load(item.thumbnail)
            .into(holder.binding.ifvThumbnail)

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    class Diff : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(old: Category, new: Category) =
            old.id == new.id

        override fun areContentsTheSame(old: Category, new: Category) =
            old == new
    }
}