package com.orbitalsonic.storiessample.presentation.models

data class Category(
    val id: Int,
    val title: String,
    val subTitle: String,
    val imageUrl: String,
    val stories: List<Story>,
    val isSeen: Boolean = false
)
