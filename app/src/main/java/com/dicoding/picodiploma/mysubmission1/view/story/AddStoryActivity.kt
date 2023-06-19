package com.dicoding.picodiploma.mysubmission1.view.story

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.dicoding.picodiploma.mysubmission1.R
import com.dicoding.picodiploma.mysubmission1.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.mysubmission1.model.UserPreference
import com.dicoding.picodiploma.mysubmission1.utils.ShowLoading
import com.dicoding.picodiploma.mysubmission1.utils.createCustomTempFile
import com.dicoding.picodiploma.mysubmission1.utils.reduceFileImage
import com.dicoding.picodiploma.mysubmission1.utils.uriToFile
import com.dicoding.picodiploma.mysubmission1.view.ViewModelFactory
import com.dicoding.picodiploma.mysubmission1.view.main.MainActivity
import com.dicoding.picodiploma.mysubmission1.view.main.MainViewModel
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import com.dicoding.picodiploma.mysubmission1.data.Result

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null
    private var token: String? = null
    private lateinit var progressBar: View
    private lateinit var showLoading: ShowLoading
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory(UserPreference.getInstance(dataStore), this)
    }
    private val addStoryViewModel by viewModels<AddStoryViewModel> {
        ViewModelFactory(UserPreference.getInstance(dataStore), this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.message_null_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading = ShowLoading()
        progressBar = binding.progressBar


        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this@AddStoryActivity,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        lifecycleScope.launchWhenCreated {
            mainViewModel.getUser().collect {
                token = "Bearer ${it.token}"
            }
        }

        binding.apply {
            galleryButton.setOnClickListener {
                startGallery()
            }

            uploadButton.setOnClickListener {
                uploadImage(token!!)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.camera_launcher, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.camera -> {
                startTakePhoto()
                true
            }
            else -> true
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile

            val result = BitmapFactory.decodeFile(getFile?.path)
            binding.previewImageView.setImageBitmap(result)
        }
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.dicoding.picodiploma.loginwithanimation",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.title_intent_galery))
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)
            getFile = myFile
            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun uploadImage(token: String) {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description =
                binding.description.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )
            addStoryViewModel.upStory(token, imageMultipart, description).observe(this) {
                if (it != null) {
                    when (it) {
                        is Result.Loading -> {
                            showLoading.showLoading(true, progressBar)
                        }
                        is Result.Success -> {
                            showLoading.showLoading(false, progressBar)
                            alertDialog()
                        }
                        is Result.Error -> {
                            showLoading.showLoading(false, progressBar)
                            toast(getString(R.string.failed_upload))
                        }
                    }
                }
            }
        } else {
            toast(getString(R.string.message_null_picture))
        }
    }

    private fun alertDialog() {
        AlertDialog.Builder(this@AddStoryActivity).apply {
            setTitle(getString(R.string.title_alert_dialog))
            setMessage(getString(R.string.message_alert_dialog_addstory))
            setPositiveButton(getString(R.string.message_positive_button)) { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}