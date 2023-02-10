package com.ariqh.movieapp.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import com.ariqh.movieapp.R
import com.ariqh.movieapp.adapter.MainAdapter
import com.ariqh.movieapp.databinding.ActivityMainBinding
import com.ariqh.movieapp.databinding.ContentMainBinding
import com.ariqh.movieapp.model.Constant
import com.ariqh.movieapp.model.MovieModel
import com.ariqh.movieapp.model.MovieResponse
import com.ariqh.movieapp.retrofit.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val moviePopular = 0
const val movieNowPlaying = 1

class MainActivity : AppCompatActivity() {
    private val TAG: String = "MainActivity"
    
    lateinit var mainAdapter: MainAdapter
    private var movieCategory = 0
    private val api = ApiService().endpoint
    private var isScrolling = false
    private var currentPage = 1
    private var totalPages = 0

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var bindingContent: ContentMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        bindingContent = binding.containerMain
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupRecyclerView()
        setupListener()
    }

    override fun onStart() {
        super.onStart()
        getMovie()
        showLoadingNextPage(false)
    }

    private fun setupRecyclerView() {
        mainAdapter = MainAdapter(arrayListOf(), object: MainAdapter.OnAdapterListener {
            override fun onClick(movie: MovieModel) {
                startActivity(Intent(applicationContext, DetailActivity::class.java))
            }

        })
        bindingContent.listMovie.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = mainAdapter
        }
    }

    private fun setupListener() {
        bindingContent.scrollView.setOnScrollChangeListener(object: NestedScrollView.OnScrollChangeListener{
            override fun onScrollChange(
                v: NestedScrollView,
                scrollX: Int,
                scrollY: Int,
                oldScrollX: Int,
                oldScrollY: Int
            ) {
                if (scrollY == v!!.getChildAt(0).measuredHeight - v.measuredHeight) {
                    if(!isScrolling) {
                        if(currentPage <= totalPages) {
                            getMovieNextPage()
                        }
                    }
                }
            }

        })
    }

    private fun getMovie() {
        bindingContent.scrollView.scrollTo(0,0)
        currentPage = 1
        showLoading(true)

        var apiCall: Call<MovieResponse>? = null
        when(movieCategory) {
            moviePopular -> {
                apiCall = api.getMoviePopular(Constant.API_KEY, 1)
            }
            movieNowPlaying -> {
                apiCall = api.getMovieNowPlaying(Constant.API_KEY, 1)
            }
        }

        apiCall!!
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
                    Log.d(TAG, "errorResponse : $t")
                    showLoading(false)
                }

            })
    }

    private  fun getMovieNextPage() {
        currentPage += 1
        showLoadingNextPage(true)

        var apiCall: Call<MovieResponse>? = null
        when(movieCategory) {
            moviePopular -> {
                apiCall = api.getMoviePopular(Constant.API_KEY, currentPage)
            }
            movieNowPlaying -> {
                apiCall = api.getMovieNowPlaying(Constant.API_KEY, currentPage)
            }
        }

        apiCall!!
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
                    Log.d(TAG, "errorResponse : $t")
                    showLoadingNextPage(false)
                }

            })
    }

    fun showLoading(loading: Boolean) {
        when(loading) {
            true -> bindingContent.progressMovie.visibility = View.VISIBLE
            false -> bindingContent.progressMovie.visibility = View.GONE
        }
    }

    fun showLoadingNextPage(loading: Boolean) {
        when(loading) {
            true -> {
                isScrolling = true
                bindingContent.progressMovieNextPage.visibility = View.VISIBLE
            }
            false -> {
                isScrolling = false
                bindingContent.progressMovieNextPage.visibility = View.GONE
            }
        }
    }

    fun showMovie(response: MovieResponse) {
        totalPages = response.total_pages!!.toInt()
        mainAdapter.setData(response.results)
    }

    fun showMovieNextPage(response: MovieResponse) {
        totalPages = response.total_pages!!.toInt()
        mainAdapter.setDataNextPage(response.results)
        showMessage("Page $currentPage")
    }

    fun showMessage(msg: String) {
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_popular -> {
                showMessage("movie popular selected")
                movieCategory = moviePopular
                getMovie()
                true
            }
            R.id.action_now_playing -> {
                showMessage("now playing selected")
                movieCategory = movieNowPlaying
                getMovie()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}