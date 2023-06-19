package com.dicoding.picodiploma.mysubmission1.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.picodiploma.mysubmission1.R
import com.dicoding.picodiploma.mysubmission1.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.mysubmission1.model.UserPreference
import com.dicoding.picodiploma.mysubmission1.utils.ShowLoading
import com.dicoding.picodiploma.mysubmission1.view.ViewModelFactory
import com.dicoding.picodiploma.mysubmission1.data.Result

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var showLoading: ShowLoading
    private lateinit var progressBar: View
    private val signupViewModel by viewModels<SignupViewModel> {
        ViewModelFactory(UserPreference.getInstance(dataStore), this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showLoading = ShowLoading()
        progressBar = binding.progressBar

        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            signupViewModel.signupAcc(name, email, password).observe(this) {
                if (it != null) {
                    when (it) {
                        is Result.Loading -> {
                            showLoading.showLoading(true, progressBar)
                        }
                        is Result.Success -> {
                            showLoading.showLoading(false, progressBar)
                            dialogAlert()
                        }
                        is Result.Error -> {
                            showLoading.showLoading(false, progressBar)
                            toast(getString(R.string.signup_failed))
                        }
                    }
                }
            }
        }

        binding.settingImageView.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
    }

    private fun dialogAlert() {
        AlertDialog.Builder(this@SignupActivity).apply {
            setTitle(getString(R.string.title_alert_dialog))
            setMessage(getString(R.string.message_alert_dialog))
            setPositiveButton(getString(R.string.message_positive_button)) { _, _ ->
                finish()
            }
            create()
            show()
        }
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val nameTextView =
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(500)
        val nameEditTextLayout =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val emailTextView =
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val passwordTextView =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(
                title,
                nameTextView,
                nameEditTextLayout,
                emailTextView,
                emailEditTextLayout,
                passwordTextView,
                passwordEditTextLayout,
                signup
            )
            startDelay = 500
        }.start()
    }

}