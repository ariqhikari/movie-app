package com.ariqh.movieapp.model

data class MovieResponse (
    val total_pages: Int?,
    val results: List<MovieModel>
)