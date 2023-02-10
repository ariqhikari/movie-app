package com.ariqh.movieapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ariqh.movieapp.R
import com.ariqh.movieapp.model.Constant
import com.ariqh.movieapp.model.TrailerModel
import kotlinx.android.synthetic.main.adapter_trailer.view.*

class TrailerAdapter(var videos: ArrayList<TrailerModel>, var listener: OnAdapterListener):
    RecyclerView.Adapter<TrailerAdapter.ViewHolder>() {
    private val TAG: String = "TrailerAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= ViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.adapter_trailer, parent, false)
    )

    override fun getItemCount() = videos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = videos[position]
        holder.bind(video)

        holder.view.text_video.setOnClickListener {
            listener.onPlay(video.key!!)
        }
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val view = view

        fun bind(videos: TrailerModel) {
            view.text_video.text = videos.name
        }
    }
    
    public fun setData(newVideos: List<TrailerModel>) {
        videos.clear()
        videos.addAll(newVideos)
        notifyDataSetChanged()
        listener.onLoad(newVideos[0].key!!)
    }

    interface OnAdapterListener {
        fun onLoad(key: String)
        fun onPlay(key: String)
    }
}