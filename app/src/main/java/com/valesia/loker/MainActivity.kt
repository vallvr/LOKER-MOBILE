package com.valesia.loker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() { // Deklarasi class MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Pastikan nama layout sesuai

        // Tombol untuk Dashboard Admin
        val btnAdmin: Button = findViewById(R.id.btnAdmin)
        btnAdmin.setOnClickListener {
            // Intent menuju MainActivity2
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        // Tombol untuk Jelajahi Pekerjaan
        val btnPelamar: Button = findViewById(R.id.btnPelamar)
        btnPelamar.setOnClickListener {
            // Intent menuju MainActivity3
            val intent = Intent(this@MainActivity, MainActivity3::class.java)
            startActivity(intent)
        }
    }
}
