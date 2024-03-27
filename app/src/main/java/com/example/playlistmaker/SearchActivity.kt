package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var currentSearchText = SEARCH_TEXT_EMPTY

    // для работы поиска
    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)
    private val tracks = ArrayList<Track>()
    lateinit var myAdapter : TrackAdapter

    // обработка проблем поиска
    lateinit var recyclerSearch : RecyclerView
    lateinit var searchErrorView : LinearLayout
    lateinit var internetErrorView : LinearLayout

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
        myAdapter = TrackAdapter(tracks, searchHistory)
        recyclerSearch.adapter = myAdapter

        // ---- Работа с вводом ----
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)
        clearButton.setOnClickListener {
            inputEditText.setText(SEARCH_TEXT_EMPTY)
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            inputEditText.clearFocus()

            searchErrorView.isVisible = false
            internetErrorView.isVisible = false
            recyclerSearch.isVisible = false
            tracks.clear()
            myAdapter.notifyDataSetChanged()
            historyAdapter.notifyDataSetChanged()
        }

        val myTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                searchHistoryView.isVisible = (inputEditText.hasFocus() && s?.isEmpty() == true && searchHistory.isNotEmpty())
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
                    search(currentSearchText)
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
            search(currentSearchText)
        }

        val clearHistoryButton = findViewById<Button>(R.id.clear_history_button)
        clearHistoryButton.setOnClickListener{
            searchHistoryView.isVisible = false
            searchHistory.clearHistory()
            historyAdapter.notifyDataSetChanged()
        }
    }

    private fun search(text: String){
        iTunesService.search(text).enqueue(object : Callback<TracksResponse>{
            override fun onResponse(
                call: Call<TracksResponse>,
                response: Response<TracksResponse>
            ) {
                // пустой экран - идёт поиск
                searchErrorView.isVisible = false
                internetErrorView.isVisible = false
                recyclerSearch.isVisible = false

                if (response.code() == 200){
                    // успешный запрос
                    tracks.clear()
                    if(response.body()?.results?.isNotEmpty() == true){
                        // Треки нашлись
                        searchErrorView.isVisible = false
                        internetErrorView.isVisible = false
                        recyclerSearch.isVisible = true

                        tracks.addAll(response.body()?.results!!)
                        myAdapter.notifyDataSetChanged()
                    }
                    if (tracks.isEmpty()){
                        // Не нашлись
                        searchErrorView.isVisible = true
                        internetErrorView.isVisible = false
                        recyclerSearch.isVisible = false
                    }
                } else {
                    // код ответа плохой
                    searchErrorView.isVisible = false
                    internetErrorView.isVisible = true
                    recyclerSearch.isVisible = false
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                // всё плохо
                searchErrorView.isVisible = false
                internetErrorView.isVisible = true
                recyclerSearch.isVisible = false
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
        /*
        val mock_list : ArrayList<Track> = arrayListOf(
            Track("Smells Like Teen Spirit",
                "Nirvana",
                "5:01",
                "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),
            Track("Billie Jean",
                "Michael Jackson",
                "4:35",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"),
            Track("Stayin' Alive",
                "Bee Gees",
                "4:10",
                "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"),
            Track("Whole Lotta Love",
                "Led Zeppelin",
                "5:33",
                "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"),
            Track("Sweet Child O'Mine",
                "Guns N' Roses",
                "5:03",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg"),
            Track("Test_Placeholder Long_Long_Long_Long_Long_Long_Long_Name",
                "Long info long long long long long long long long long long long",
                "69:420",
                "https://https://www.google.com/")
        )
        */
    }
}