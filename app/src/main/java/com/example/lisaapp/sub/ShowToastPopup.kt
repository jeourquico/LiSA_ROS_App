package com.example.lisaapp.sub

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.lisaapp.R

class ShowToastPopup (private val context: Context, private val layoutInflater: LayoutInflater) {

    // Toast view
    fun showToast(message: String) {
        val layoutInflater = layoutInflater
        val layout: View = layoutInflater.inflate(
            R.layout.toast_layout_root,
            (context as Activity).findViewById(R.id.customToastLayout)
        )

        val textViewToastMessage = layout.findViewById<TextView>(R.id.textViewToastMessage)
        textViewToastMessage.text = message

        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }

}