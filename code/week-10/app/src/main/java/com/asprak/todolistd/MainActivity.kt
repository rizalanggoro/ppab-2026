package com.asprak.todolistd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.asprak.todolistd.core.ComposeApp
import com.asprak.todolistd.feature.auth.data.AuthRepository
import com.asprak.todolistd.feature.setting.data.ThemeRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var themeRepository: ThemeRepository

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeApp(
                themeRepository = themeRepository,
                authRepository = authRepository,
            )
        }
    }
}