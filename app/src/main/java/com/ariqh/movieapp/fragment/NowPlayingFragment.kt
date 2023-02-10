package com.ariqh.movieapp.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import com.ariqh.movieapp.activity.DetailActivity
import com.ariqh.movieapp.adapter.MainAdapter
import com.ariqh.movieapp.databinding.FragmentNowPlayingBinding
import com.ariqh.movieapp.model.Constant
import com.ariqh.movieapp.model.MovieModel
import com.ariqh.movieapp.model.MovieResponse
import com.ariqh.movieapp.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NowPlayingFragment : Fragment() {
    private val TAG: String = "NowPlayingFragment"

    lateinit var binding : FragmentNowPlayingBinding
    lateinit var mainAdapter: MainAdapter

    private var currentPage = 1
    private var totalPages = 0
    private var isScrolling = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNowPlayingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListener()
    }

    override fun onStart() {
        super.onStart()
        getMovieNowPlaying()
        showLoadingNextPage(false)
    }

    private fun setupRecyclerView() {
        mainAdapter = MainAdapter(arrayListOf(), object: MainAdapter.OnAdapterListener {
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
        binding.scrollView.setOnScrollChangeListener(object: NestedScrollView.OnScrollChangeListener {
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
                            getMovieNowPlayingNextPage()
                        }
                    }
                }
            }

        })
    }

    fun getMovieNowPlaying() {
        binding.scrollView.scrollTo(0,0)
        currentPage = 1
        showLoading(true)

        ApiService().endpoint.getMovieNowPlaying(Constant.API_KEY, 1)
            .enqueue(object: Callback<MovieResponse> {
                override fun onResponse(
                    call: Call<MovieResponse>,
                    response: Response<MovieResponse>
                ) {
                    showLoading(false)
                    if(response.isSuccessful) {
                        showMovie(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    showLoading(false)
                }

            })
    }

    fun getMovieNowPlayingNextPage() {
        currentPage += 1
        showLoadingNextPage(true)

        ApiService().endpoint.getMovieNowPlaying(Constant.API_KEY, currentPage)
            .enqueue(object: Callback<MovieResponse> {
                override fun onResponse(
                    call: Call<MovieResponse>,
                    response: Response<MovieResponse>
                ) {
                    showLoadingNextPage(false)
                    if(response.isSuccessful) {
                        showMovieNextPage(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<MovieResponse>, t: Throwable) {
                    showLoadingNextPage(false)
                }

            })
    }

    fun showLoading(loading: Boolean) {
        when(loading) {
            true -> binding.progressMovie.visibility = View.VISIBLE
            false -> binding.progressMovie.visibility = View.GONE
        }
    }

    fun showLoadingNextPage(loading: Boolean) {
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