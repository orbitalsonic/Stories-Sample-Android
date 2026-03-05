package com.orbitalsonic.storiessample.presentation.models

data class Category(
    val id: Int,
    val title: String,
    val subtitle: String,
    val thumbnail: String,
    val stories: List<Story>,
    val isSeen: Boolean = false
)
