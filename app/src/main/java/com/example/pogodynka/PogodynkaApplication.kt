package com.example.pogodynka

import android.app.Application
import com.example.pogodynka.data.database.PogodynkaDatabase

class PogodynkaApplication :Application() {
    val database : PogodynkaDatabase by lazy {PogodynkaDatabase.getDatabase(this)}
}