package com.valesia.loker

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.valesia.loker.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    // Hardcoded username dan password untuk validasi
    private val validUsername = "admin"
    private val validPassword = "1234"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate layout menggunakan View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tambahkan click listener pada tombol login
        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            // Validasi input
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username dan Password harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                // Lakukan verifikasi login
                verifyLogin(username, password)
            }
        }
    }

    // Fungsi untuk memverifikasi login
    private fun verifyLogin(username: String, password: String) {
        if (username == validUsername && password == validPassword) {
            // Login berhasil, arahkan ke MainActivity2
            val intent = Intent(this, MainActivity2::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Membersihkan task sebelumnya
            startActivity(intent)
            finish() // Tutup LoginActivity
        } else {
            // Login gagal, tampilkan pesan
            Toast.makeText(this, "Username atau Password salah", Toast.LENGTH_SHORT).show()
        }
    }
}
