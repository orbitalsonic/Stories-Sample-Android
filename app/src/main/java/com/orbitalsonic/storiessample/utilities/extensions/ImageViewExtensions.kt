package com.orbitalsonic.storiessample.utilities.extensions

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.progressindicator.CircularProgressIndicator

fun ImageView.setImageFromUrl(imageUrl: String, progressBar: CircularProgressIndicator? = null) {
    Glide
        .with(context)
        .load(imageUrl)
        .addListener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                progressBar?.visibility = View.GONE
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                progressBar?.visibility = View.GONE
                return false
            }
        })
        .into(this)
}