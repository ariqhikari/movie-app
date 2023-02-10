package com.ariqh.movieapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import com.ariqh.movieapp.activity.DetailActivity
import com.ariqh.movieapp.adapter.MainAdapter
import com.ariqh.movieapp.databinding.FragmentPopularBinding
import com.ariqh.movieapp.model.Constant
import com.ariqh.movieapp.model.MovieModel
import com.ariqh.movieapp.model.MovieResponse
import com.ariqh.movieapp.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PopularFragment : Fragment() {
    private val TAG: String = "PopularFragment"

    private lateinit var binding: FragmentPopularBinding
    private lateinit var mainAdapter: MainAdapter

    private var currentPage = 1
    private var totalPages = 0
    private var isScrolling = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPopularBinding.inflate(inflater, container, false)
        return  binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListener()
    }

    override fun onStart() {
        super.onStart()
        getMoviePopular()
        showLoadingNextPage(false)
    }

    private fun setupRecyclerView() {
        mainAdapter = MainAdapter(arrayListOf(), object : MainAdapter.OnAdapterListener{
            override fun onClick(movie: MovieModel) {
                startActivity(Intent(context, DetailActivity::class.java))
            }
        })

        binding.listMovie.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = mainAdapter
        }
    }

    private fun setupListener() {

        binding.scrollView.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener{
            override fun onScrollChange(
                v: NestedScrollView,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                if (scrollY == v!!.getChildAt(0).measuredHeight - v.measuredHeight) {
                    if (!isScrolling) {
                        if ( currentPage <= totalPages ) {
                            getMoviePopularNextPage()
                        }
                    }
                }
            }

        })
    }

    private fun getMoviePopular(){

        binding.scrollView.scrollTo(0, 0)
        currentPage = 1
        showLoading(true)

        ApiService().endpoint.getMoviePopular( Constant.API_KEY, currentPage )
            .enqueue(object : Callback<MovieResponse> {
                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    Log.d(TAG, t.toString())
                    showLoading(false)
                }

                override fun onResponse(call: Call<MovieResponse>,
                                        response: Response<MovieResponse>
                ) {

                    Log.d(TAG, response.toString())
                    showLoading(false)

                    if (response.isSuccessful) {

                        val results = response.body()!!
                        showMovie( results )

                    }
                }

            })
    }

    private fun getMoviePopularNextPage(){

        currentPage += 1
        showLoadingNextPage(true)

        ApiService().endpoint.getMoviePopular( Constant.API_KEY, currentPage )
            .enqueue(object : Callback<MovieResponse> {
                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    Log.d(TAG, t.toString())
                    showLoadingNextPage(false)
                }

                override fun onResponse(call: Call<MovieResponse>,
                                        response: Response<MovieResponse>
                ) {

                    Log.d(TAG, response.toString())
                    showLoadingNextPage(false)

                    if (response.isSuccessful) {

                        val results = response.body()!!
                        showMovieNextPage( results )

                    }
                }

            })
    }

    private fun showLoading(loading: Boolean) {
        when(loading) {
            true -> {
                binding.progressMovie.visibility = View.VISIBLE
            }
            false -> {
                binding.progressMovie.visibility = View.GONE
            }
        }
    }

    private fun showLoadingNextPage(loading: Boolean) {
        when(loading) {
            true -> {
                isScrolling = true
                binding.progressMovieNextPage.visibility = View.VISIBLE
            }
            false -> {
                isScrolling = false
                binding.progressMovieNextPage.visibility = View.GONE
            }
        }
    }

    private fun showMovie(movie: MovieResponse?){
        movie?.let {
            totalPages = movie.total_pages!!
            mainAdapter.setData( movie.results!! )
        }
    }

    private fun showMovieNextPage(movie: MovieResponse?){
        movie?.let {
            totalPages = movie.total_pages!!
            mainAdapter.setDataNextPage( movie.results!! )
            showMessage("Page $currentPage")
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}