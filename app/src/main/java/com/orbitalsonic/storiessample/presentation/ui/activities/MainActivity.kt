package com.orbitalsonic.storiessample.presentation.ui.activities

import android.content.Intent
import com.orbitalsonic.storiessample.base.BaseActivity
import com.orbitalsonic.storiessample.databinding.ActivityMainBinding
import com.orbitalsonic.storiessample.presentation.adapters.CategoryAdapter
import com.orbitalsonic.storiessample.presentation.viewModels.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity :
    BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {

    private val viewModel: MainViewModel by viewModel()
    private lateinit var adapter: CategoryAdapter

    override fun onCreated() {
        adapter = CategoryAdapter { category ->

            val intent = Intent(this, StoryActivity::class.java)
            intent.putExtra("title", category.title)
            intent.putExtra("id", category.id)

            startActivity(intent)
        }

        binding.recyclerView.adapter = adapter

        viewModel.parentCategories.observe(this) { list ->
            adapter.submitList(list)
        }
    }
}