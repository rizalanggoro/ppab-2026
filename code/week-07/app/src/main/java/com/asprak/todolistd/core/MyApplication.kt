package com.asprak.todolistd.core

import android.app.Application
import com.asprak.todolistd.feature.auth.data.AuthRepository

class MyApplication : Application() {
    val authRepository = AuthRepository()
}