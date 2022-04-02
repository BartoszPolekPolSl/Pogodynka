package com.example.pogodynka.util

import android.widget.Toast
import androidx.fragment.app.Fragment

fun Fragment.toast(message : String){
    Toast.makeText(this.requireContext(), message, Toast.LENGTH_LONG).show()
}