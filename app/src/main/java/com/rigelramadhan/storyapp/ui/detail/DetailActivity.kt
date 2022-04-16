package com.rigelramadhan.storyapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.rigelramadhan.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private val binding: ActivityDetailBinding by lazy {
        ActivityDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
    }

    private fun setupView() {
        val name = intent.getStringExtra(NAME_EXTRA)
        val description = intent.getStringExtra(DESCRIPTION_EXTRA)
        val imgUrl = intent.getStringExtra(IMAGE_URL_EXTRA)

        supportActionBar?.title = name
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.tvDescription.text = description

        Glide.with(this)
            .load(imgUrl)
            .into(binding.imgStory)
    }

    companion object {
        const val NAME_EXTRA = "name_extra"
        const val DESCRIPTION_EXTRA = "desc_extra"
        const val IMAGE_URL_EXTRA = "img_extra"
    }
}