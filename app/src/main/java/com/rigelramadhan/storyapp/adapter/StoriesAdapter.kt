package com.rigelramadhan.storyapp.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rigelramadhan.storyapp.data.local.entity.StoryEntity
import com.rigelramadhan.storyapp.databinding.ItemPostBinding
import com.rigelramadhan.storyapp.ui.detail.DetailActivity

class StoriesAdapter(private val context: Context, private val list: List<StoryEntity>) : RecyclerView.Adapter<StoriesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = list[position]
        holder.binding.tvUserName.text = story.name
        holder.binding.tvCaption.text = story.description

        Glide.with(holder.itemView.context)
            .load(story.photoUrl)
            .into(holder.binding.imgPost)

        holder.binding.cardStory.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra(DetailActivity.NAME_EXTRA, story.name)
            intent.putExtra(DetailActivity.DESCRIPTION_EXTRA, story.description)
            intent.putExtra(DetailActivity.IMAGE_URL_EXTRA, story.photoUrl)

            val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                holder.itemView.context as Activity,
                Pair(holder.binding.imgPost, "picture"),
                Pair(holder.binding.tvCaption, "description")
            )

            holder.itemView.context.startActivity(intent, optionsCompat.toBundle())
        }
    }

    override fun getItemCount() = list.size

    inner class ViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)
}