package com.ariqh.movieapp.retrofit

import com.ariqh.movieapp.model.DetailResponse
import com.ariqh.movieapp.model.MovieResponse
import com.ariqh.movieapp.model.TrailerResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiEndpoint {
    @GET("now_playing")
    fun getMovieNowPlaying(
        @Query("api_key") api_key: String,
        @Query("page") page: Int
    ): Call<MovieResponse>

    @GET("popular")
    fun getMoviePopular(
        @Query("api_key") api_key: String,
        @Query("page") page: Int
    ): Call<MovieResponse>

    @GET("{movie_id}")
    fun getMovieDetail(
        @Path("movie_id") movie_id: Int,
        @Query("api_key") api_key: String,
    ): Call<DetailResponse>

    @GET("{movie_id}/videos")
    fun getMovieTrailer(
        @Path("movie_id") movie_id: Int,
        @Query("api_key") api_key: String,
    ): Call<TrailerResponse>
}