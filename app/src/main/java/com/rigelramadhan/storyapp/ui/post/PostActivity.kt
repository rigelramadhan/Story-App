package com.rigelramadhan.storyapp.ui.post

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.rigelramadhan.storyapp.R
import com.rigelramadhan.storyapp.data.Result
import com.rigelramadhan.storyapp.data.local.datastore.LoginPreferences
import com.rigelramadhan.storyapp.databinding.ActivityPostBinding
import com.rigelramadhan.storyapp.ui.login.LoginActivity
import com.rigelramadhan.storyapp.ui.main.MainActivity
import com.rigelramadhan.storyapp.utils.AppExecutors
import com.rigelramadhan.storyapp.utils.reduceFileImage
import com.rigelramadhan.storyapp.utils.rotateBitmap
import java.io.File
import java.io.FileOutputStream

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class PostActivity : AppCompatActivity() {

    private val binding: ActivityPostBinding by lazy {
        ActivityPostBinding.inflate(layoutInflater)
    }

    private val postViewModel: PostViewModel by viewModels {
        PostViewModel.PostViewModelFactory.getInstance(
            this,
            LoginPreferences.getInstance(dataStore)
        )
    }

    private val appExecutor: AppExecutors by lazy {
        AppExecutors()
    }

    private var file: File? = null
    private var isBack: Boolean = true
    private var reducingDone: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.post)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        bindResult()
        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        checkIfSessionValid()
    }

    private fun checkIfSessionValid() {
        postViewModel.checkIfTokenAvailable().observe(this) {
            if (it == "null") {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun setupButtons() {
        binding.btnPost.setOnClickListener {
            postViewModel.checkIfTokenAvailable().observe(this) {
                if (reducingDone) {
                    if (it == "null") {
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                    } else {
                        uploadImage("Bearer $it")
                    }
                } else {
                    Toast.makeText(this, getString(R.string.wait_for_processing), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun bindResult() {
        file = intent.getSerializableExtra(PHOTO_RESULT_EXTRA) as File
        isBack = intent.getBooleanExtra(IS_CAMERA_BACK_EXTRA, true)

        val result = rotateBitmap(BitmapFactory.decodeFile((file as File).path), isBack)
        result.compress(Bitmap.CompressFormat.JPEG, 100, FileOutputStream(file))

        appExecutor.diskIO.execute {
            file = reduceFileImage(file as File)
            reducingDone = true
        }


        binding.imgStory.setImageBitmap(result)
    }

    private fun uploadImage(token: String) {
        if (binding.etDescription.text.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.description_cannot_empty), Toast.LENGTH_SHORT).show()
        } else {
            if (file != null) {
                binding.progressBar.visibility = View.VISIBLE
                val description = binding.etDescription.text.toString()
                val result = postViewModel.postStory(token, file as File, description)
                result.observe(this) {
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
                            Toast.makeText(this, getString(R.string.image_posted), Toast.LENGTH_SHORT)
                                .show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val PHOTO_RESULT_EXTRA = "photo_result_extra"
        const val IS_CAMERA_BACK_EXTRA = "is_camera_back_extra"
    }
}