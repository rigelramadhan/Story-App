package com.rigelramadhan.storyapp.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.rigelramadhan.storyapp.R
import com.rigelramadhan.storyapp.adapter.StoriesAdapter
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.local.datastore.LoginPreferences
import com.rigelramadhan.storyapp.databinding.ActivityMainBinding
import com.rigelramadhan.storyapp.ui.camera.CameraActivity
import com.rigelramadhan.storyapp.ui.login.LoginActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory.getInstance(
            this,
            LoginPreferences.getInstance(dataStore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        checkIfSessionValid()
    }

    private fun checkIfSessionValid() {
        mainViewModel.checkIfTokenAvailable().observe(this) {
            if (it == "null") {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                setupView("Bearer $it")
            }
        }
    }

    private fun setupView(token: String) {
        supportActionBar?.title = getString(R.string.feed)
        mainViewModel.getStories(token).observe(this) {
            when (it) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Result.Error -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    val error = it.error
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }

                is Result.Success -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    val data = it.data
                    if (data.isEmpty()) {
                        binding.tvNoStory.visibility = View.VISIBLE
                    } else {
                        binding.tvNoStory.visibility = View.INVISIBLE
                        binding.rvStories.apply {
                            adapter = StoriesAdapter(this@MainActivity, data)
                            layoutManager = LinearLayoutManager(this@MainActivity)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            mainViewModel.logout()
        } else if (item.itemId == R.id.settings) {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupButtons() {
        binding.btnAdd.setOnClickListener {
            if (!allPermissionsGranted()) {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            } else {
                val intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(this, getString(R.string.permission_needed), Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}