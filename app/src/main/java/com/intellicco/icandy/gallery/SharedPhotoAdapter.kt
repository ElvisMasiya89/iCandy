package com.intellicco.icandy.gallery

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.util.Size
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.intellicco.icandy.R
import com.intellicco.icandy.databinding.ItemPhotoBinding


class SharedPhotoAdapter(private var context: Context?, private val onClickListener: OnClickListener) : ListAdapter<SharedStoragePhoto, SharedPhotoAdapter.PhotoViewHolder>(Companion) {


    inner class PhotoViewHolder(val binding: ItemPhotoBinding): RecyclerView.ViewHolder(binding.root)

    companion object : DiffUtil.ItemCallback<SharedStoragePhoto>() {
        override fun areItemsTheSame(oldItem: SharedStoragePhoto, newItem: SharedStoragePhoto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SharedStoragePhoto, newItem: SharedStoragePhoto): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(
            ItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {

        val photo = currentList[position]
       // val photo = getItem(position)
//        val thumbImage: Bitmap = ThumbnailUtils.extractThumbnail(
//            BitmapFactory.decodeFile(photo.contentUri.path), 640,480 )

        val thumbnail:Bitmap? = context?.contentResolver?.loadThumbnail(photo.contentUri, Size(144, 144), null)
        holder.binding.apply {

            Glide.with(ivPhoto.context)
                .load(thumbnail)
                .apply(
                        RequestOptions()
                        .placeholder(R.drawable.ic_baseline_image_24).centerCrop())
                .into(ivPhoto)



            holder.itemView.setOnClickListener {
                onClickListener.onClick(photo)
            }



        }
    }

    class OnClickListener(val clickListener: (photo:SharedStoragePhoto) -> Unit) {
        fun onClick(photo:SharedStoragePhoto){
              clickListener(photo)
        }
    }
}


