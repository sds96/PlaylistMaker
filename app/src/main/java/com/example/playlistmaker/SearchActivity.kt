package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var currentSearchText = SEARCH_TEXT_EMPTY

    private val handler = Handler(Looper.getMainLooper())
    // для работы поиска
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)
    private val tracks = ArrayList<Track>()
    var myAdapter : TrackAdapter? = null

    // обработка проблем поиска
    var recyclerSearch : RecyclerView? = null
    var searchErrorView : LinearLayout? = null
    var internetErrorView : LinearLayout? = null
    var searchProgressBar : ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // ---- История поиска ----
        val sharedPrefs = getSharedPreferences(PLAYLISTMAKER_SHARED_PREFERENCES, MODE_PRIVATE)
        val searchHistory = SearchHistory(sharedPrefs)
        val searchHistoryView = findViewById<LinearLayout>(R.id.search_history)

        val recyclerSearchHistory = findViewById<RecyclerView>(R.id.recyclerSearchHistory)
        val historyAdapter = TrackAdapter(searchHistory.getHistory(), searchHistory)
        recyclerSearchHistory.adapter = historyAdapter

        // ---- Просто поиск ----
        searchErrorView = findViewById(R.id.searchErrorView)
        internetErrorView = findViewById(R.id.internetErrorView)
        recyclerSearch = findViewById(R.id.recyclerSearch)
        searchProgressBar = findViewById(R.id.searchProgressBar)
        myAdapter = TrackAdapter(tracks, searchHistory)
        recyclerSearch?.adapter = myAdapter

        // ---- Работа с вводом ----
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        clearButton.setOnClickListener {
            inputEditText.setText(SEARCH_TEXT_EMPTY)
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            inputEditText.clearFocus()

            searchErrorView?.isVisible = false
            internetErrorView?.isVisible = false
            recyclerSearch?.isVisible = false
            searchProgressBar?.isVisible = false
            tracks.clear()
            myAdapter?.notifyDataSetChanged()
            historyAdapter.notifyDataSetChanged()
        }

        val myTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchHistoryView.isVisible = (inputEditText.hasFocus() && s?.isEmpty() == true && searchHistory.isNotEmpty())
                searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                currentSearchText = s.toString()
            }
        }

        inputEditText.addTextChangedListener(myTextWatcher)
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            searchHistoryView.isVisible = (hasFocus && inputEditText.text.isEmpty() && searchHistory.isNotEmpty())
        }

        // штука, чтобы при нажатии DONE на клаве, запускать поиск
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if(currentSearchText.isNotEmpty())
                    search()
            }
            false
        }

        // ---- Навигация и кнопки ---
        val backArrow = findViewById<ImageView>(R.id.search_back_arrow)
        backArrow.setOnClickListener{
            finish()
        }

        val refreshButton = findViewById<Button>(R.id.refresh_button)
        refreshButton.setOnClickListener{
            search()
        }

        val clearHistoryButton = findViewById<Button>(R.id.clear_history_button)
        clearHistoryButton.setOnClickListener{
            searchHistoryView.isVisible = false
            searchHistory.clearHistory()
            historyAdapter.notifyDataSetChanged()
        }
    }

    private val searchRunnable = Runnable { search() }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, 2000L)
    }

    private fun search(){
        if(currentSearchText.isEmpty())
            return
        // начинаем поиск - прогрессБар
        searchErrorView?.isVisible = false
        internetErrorView?.isVisible = false
        recyclerSearch?.isVisible = false
        searchProgressBar?.isVisible = true

        iTunesService.search(currentSearchText).enqueue(object : Callback<TracksResponse>{
            override fun onResponse(
                call: Call<TracksResponse>,
                response: Response<TracksResponse>
            ) {
                searchProgressBar?.isVisible = false

                if (response.code() == 200){
                    // успешный запрос
                    tracks.clear()
                    if(response.body()?.results?.isNotEmpty() == true){
                        // Треки нашлись
                        searchErrorView?.isVisible = false
                        internetErrorView?.isVisible = false
                        recyclerSearch?.isVisible = true

                        tracks.addAll(response.body()?.results!!)
                        myAdapter?.notifyDataSetChanged()
                    }
                    if (tracks.isEmpty()){
                        // Не нашлись
                        searchErrorView?.isVisible = true
                        internetErrorView?.isVisible = false
                        recyclerSearch?.isVisible = false
                    }
                } else {
                    // код ответа плохой
                    searchErrorView?.isVisible = false
                    internetErrorView?.isVisible = true
                    recyclerSearch?.isVisible = false
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                // всё плохо
                searchProgressBar?.isVisible = false
                searchErrorView?.isVisible = false
                internetErrorView?.isVisible = true
                recyclerSearch?.isVisible = false
            }
        })
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_STRING, currentSearchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        findViewById<EditText>(R.id.inputEditText).setText(savedInstanceState.getString(SEARCH_STRING))
    }

    companion object{
        const val SEARCH_STRING = "SEARCH_STRING"
        const val SEARCH_TEXT_EMPTY = ""
    }
}