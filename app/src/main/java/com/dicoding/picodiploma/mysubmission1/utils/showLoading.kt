package com.dicoding.picodiploma.mysubmission1.utils

import android.view.View

class ShowLoading {
    fun showLoading(isLoading: Boolean, progressBar: View){
        if (isLoading) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }
}