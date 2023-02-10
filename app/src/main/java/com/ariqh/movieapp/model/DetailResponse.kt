package com.ariqh.movieapp.model

data class DetailResponse (
    val id: Int?,
    val title: String?,
    val backdrop_path: String?,
    val poster_path: String?,
    val overview: String?,
    val release_date: String?,
    val genres: List<GenreModel>?,
    val vote_average: Double?
)