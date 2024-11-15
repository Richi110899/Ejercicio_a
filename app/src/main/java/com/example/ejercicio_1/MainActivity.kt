package com.example.ejercicio_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ejercicio_1.ui.theme.Ejercicio_1Theme
import com.example.ejercicio_1.view.PostsApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ejercicio_1Theme {
                PostsApp()
            }
        }
    }
}

