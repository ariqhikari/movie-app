package com.ariqh.movieapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ariqh.movieapp.R
import com.ariqh.movieapp.databinding.AdapterMainBinding
import com.ariqh.movieapp.model.Constant
import com.ariqh.movieapp.model.MovieModel
import com.squareup.picasso.Picasso

class MainAdapter(var movies: ArrayList<MovieModel>, var listener: OnAdapterListener):
    RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    private val TAG: String = "MainAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= ViewHolder (
        AdapterMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun getItemCount() = movies.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val movie = movies[position]
        holder.binding.textTitle.text = movie.title
        val posterPath = Constant.POSTER_PATH + movie.poster_path

        Picasso.get()
            .load(posterPath)
            .placeholder(R.drawable.placeholder_portrait)
            .error(R.drawable.placeholder_portrait)
            .into(holder.binding.imagePoster);

        holder.binding.imagePoster.setOnClickListener {
            Constant.MOVIE_ID = movie.id!!
            Constant.MOVIE_TITLE = movie.title!!
            listener.onClick(movie)
        }
    }

    class ViewHolder(val binding: AdapterMainBinding): RecyclerView.ViewHolder(binding.root)

    public fun setData(newMovies: List<MovieModel>) {
        movies.clear()
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }

    public fun setDataNextPage(newMovies: List<MovieModel>) {
        movies.addAll(newMovies)
        notifyDataSetChanged()
    }

    interface OnAdapterListener {
        fun onClick(movie: MovieModel)
    }
}