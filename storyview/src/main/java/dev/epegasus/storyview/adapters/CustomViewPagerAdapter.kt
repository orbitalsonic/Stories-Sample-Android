package dev.epegasus.storyview.adapters

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import dev.epegasus.storyview.dataClasses.MyStory
import dev.epegasus.storyview.databinding.ItemStoryBinding
import dev.epegasus.storyview.listeners.StoryCallback
import dev.epegasus.storyview.utils.PaletteExtraction

class CustomViewPagerAdapter(
    private val imageList: List<MyStory>,
    private val itemClick: StoryCallback,
    private val onDownloadClick: ((Int, String) -> Unit)? = null
) : RecyclerView.Adapter<CustomViewPagerAdapter.ViewHolder>() {

    private var storiesStarted = false

    /**
     * Reset the stories started flag (useful when adapter is recreated)
     */
    fun resetStoriesStarted() {
        storiesStarted = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            val item = imageList[position]

            // Configuring Description
            if (!item.description.isNullOrEmpty()) {
                itemBinding.mtvDescription.visibility = View.VISIBLE
                itemBinding.mtvDescription.text = item.description
                itemBinding.mtvDescription.setOnClickListener { itemClick.onDescriptionClickListener(position) }
            }

            // Configuring Download Button
            itemBinding.ifvDownload.setOnClickListener {
                Log.d("CustomViewPagerAdapter", "Download button clicked at position: $position")
                Toast.makeText(itemBinding.root.context, "Downloading!", Toast.LENGTH_SHORT).show()
                onDownloadClick?.invoke(position, item.url ?: "")
            }

            // Prevent touch events from bubbling up to story view
            itemBinding.ifvDownload.setOnTouchListener { view, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        Log.d("CustomViewPagerAdapter", "Download button touch DOWN")
                        itemClick.setDownloadButtonTouched(true)
                        true
                    }

                    MotionEvent.ACTION_UP -> {
                        Log.d("CustomViewPagerAdapter", "Download button touch UP")
                        view.performClick()
                        // Reset the flag after a short delay
                        view.postDelayed({
                            itemClick.setDownloadButtonTouched(false)
                        }, 100)
                        true
                    }

                    MotionEvent.ACTION_CANCEL -> {
                        Log.d("CustomViewPagerAdapter", "Download button touch CANCELLED")
                        itemClick.setDownloadButtonTouched(false)
                        true
                    }

                    else -> false
                }
            }

            // Configuring Image
            Glide.with(itemBinding.root.context)
                .load(item.url)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                        itemClick.nextStory()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable?>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        if (resource != null) {
                            val paletteExtraction = PaletteExtraction(itemBinding.root, (resource as BitmapDrawable).bitmap)
                            paletteExtraction.execute()
                        }
                        if (!storiesStarted) {
                            storiesStarted = true
                            // Check if the view is still attached before starting stories
                            if (itemBinding.root.isAttachedToWindow) {
                                itemClick.startStories()
                            } else {
                                Log.w("CustomViewPagerAdapter", "View not attached, skipping startStories()")
                            }
                        }
                        return false
                    }

                })
                .into(itemBinding.divImageItemStory)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    inner class ViewHolder(val itemBinding: ItemStoryBinding) : RecyclerView.ViewHolder(itemBinding.root)
}