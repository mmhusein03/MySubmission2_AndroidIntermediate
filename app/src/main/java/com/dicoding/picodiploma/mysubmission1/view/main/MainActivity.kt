package com.dicoding.picodiploma.mysubmission1.view.main

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.mysubmission1.R
import com.dicoding.picodiploma.mysubmission1.adapter.LoadingStateAdapter
import com.dicoding.picodiploma.mysubmission1.adapter.StoriesAdapter
import com.dicoding.picodiploma.mysubmission1.databinding.ActivityMainBinding
import com.dicoding.picodiploma.mysubmission1.model.UserPreference
import com.dicoding.picodiploma.mysubmission1.view.ViewModelFactory
import com.dicoding.picodiploma.mysubmission1.view.maps.MapsActivity
import com.dicoding.picodiploma.mysubmission1.view.story.AddStoryActivity
import com.dicoding.picodiploma.mysubmission1.view.welcome.WelcomeActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory(UserPreference.getInstance(dataStore), this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.title_list_stories)

        binding.listStories.layoutManager = LinearLayoutManager(this)
        getStory()
        action()
    }

    private fun action() {
        binding.fabButton.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }

        binding.mapButton.setOnClickListener {
            val intent = Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(intent)
        }

        binding.swipeRefresh.setOnRefreshListener {
            getStory()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun getStory() {
        if (application.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.listStories.layoutManager = GridLayoutManager(this, 2)
        } else {
            binding.listStories.layoutManager = LinearLayoutManager(this)
        }

        lifecycleScope.launchWhenCreated {
            mainViewModel.getUser().collect() {
                if (it.isLogin) {
                    val adapter = StoriesAdapter()

                    binding.listStories.adapter = adapter.withLoadStateFooter(
                        footer = LoadingStateAdapter {
                            adapter.retry()
                        }
                    )

                    mainViewModel.getStories(it.token).observe(this@MainActivity) {  result ->
                        adapter.submitData(lifecycle, result)
                    }
                } else {
                    startWelcome()
                }
            }
        }

    }

    private fun startWelcome() {
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                mainViewModel.logout()
                true
            }

            R.id.setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            else -> true
        }
    }
}