package com.dicoding.picodiploma.mysubmission1.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.mysubmission1.databinding.ItemRowStoriesBinding
import com.dicoding.picodiploma.mysubmission1.networking.ListStories
import com.dicoding.picodiploma.mysubmission1.utils.DateFormat
import com.dicoding.picodiploma.mysubmission1.view.detail.DetailStoryActivity
import java.util.*


class StoriesAdapter :
    PagingDataAdapter<ListStories, StoriesAdapter.ListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemRowStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }


    class ListViewHolder(private var binding: ItemRowStoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(listStories: ListStories) {
            binding.apply {
                tvName.text = listStories.name
                Glide.with(itemView.context)
                    .load(listStories.photoUrl)
                    .into(imgItemPhoto)
                tvCreateAt.text = listStories.createAt?.let {
                    DateFormat.formatDate(it,
                        TimeZone.getDefault().id)
                }

                cardView.setOnClickListener {

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(imgItemPhoto, "photo"),
                            Pair(tvName, "name"),
                            Pair(tvCreateAt, "time_create"),
                        )

                    val moveDetailStories =
                        Intent(itemView.context, DetailStoryActivity::class.java)
                    moveDetailStories.putExtra(DetailStoryActivity.DETAIL_STORY, listStories)

                    itemView.context.startActivity(moveDetailStories, optionsCompat.toBundle())

                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStories>() {
            override fun areItemsTheSame(oldItem: ListStories, newItem: ListStories): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStories, newItem: ListStories): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}