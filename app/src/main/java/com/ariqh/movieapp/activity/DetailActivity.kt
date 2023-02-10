package com.ariqh.movieapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ariqh.movieapp.R
import com.ariqh.movieapp.databinding.ActivityDetailBinding
import com.ariqh.movieapp.model.Constant
import com.ariqh.movieapp.model.DetailResponse
import com.ariqh.movieapp.retrofit.ApiService
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {
    private val TAG: String = "DetailActivity"

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupView()
        setupListener()
    }

    override fun onStart() {
        super.onStart()
        getMovieDetail()
    }

    private fun setupView() {
        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupListener() {
        fab_play.setOnClickListener {
            startActivity(Intent(applicationContext, TrailerActivity::class.java))
        }
    }

    private fun getMovieDetail() {
        ApiService().endpoint.getMovieDetail(Constant.MOVIE_ID, Constant.API_KEY)
            .enqueue(object : Callback<DetailResponse> {
                override fun onResponse(
                    call: Call<DetailResponse>,
                    response: Response<DetailResponse>
                ) {
                    if(response.isSuccessful) {
                        showMovie(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                    Log.d(TAG, t.toString())
                }

            })
    }

    fun showMovie(detail: DetailResponse) {
        val backdropPath = Constant.BACKDROP_PATH + detail.backdrop_path

        Picasso.get()
            .load(backdropPath)
            .placeholder(R.drawable.placeholder_landscape)
            .error(R.drawable.placeholder_landscape)
            .fit().centerCrop()
            .into(image_poster);

        text_title.text = detail.title
        text_vote.text = detail.vote_average.toString()
        text_overview.text = detail.overview

        var genres = ""
        for (genre in detail.genres!!) {
            genres += "${genre.name}, "
        }
        text_genre.text = genres
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }
}