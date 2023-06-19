package com.dicoding.picodiploma.mysubmission1.view.detail

import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.mysubmission1.databinding.ActivityDetailStoryBinding
import com.dicoding.picodiploma.mysubmission1.networking.ListStories
import com.dicoding.picodiploma.mysubmission1.utils.DateFormat
import java.util.*

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setDetailStory()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setDetailStory() {
        val detailStory = intent.getParcelableExtra<ListStories>(DETAIL_STORY) as ListStories

        binding.apply {
            tvNamaDetail.text = detailStory.name
            tvDescription.text = detailStory.description
            tvTime.text = detailStory.createAt?.let { DateFormat.formatDate(it, TimeZone.getDefault().id) }
            Glide.with(this@DetailStoryActivity)
                .load(detailStory.photoUrl)
                .into(previewImageView)

        }
    }

    companion object {
        const val DETAIL_STORY = "detail_story"
    }
}