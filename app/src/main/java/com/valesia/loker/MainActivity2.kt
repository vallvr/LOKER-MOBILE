package com.valesia.loker

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.valesia.loker.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol ke UploadActivityAdmin
        binding.btnKeUpload.setOnClickListener {
            val intent = Intent(this@MainActivity2, UploadActivityAdmin::class.java)
            startActivity(intent)
        }

        // Tombol ke SemuaActivityAdmin
        binding.btnSemua.setOnClickListener {
            val intent = Intent(this@MainActivity2, SemuaActivityAdmin::class.java)
            startActivity(intent)
        }

        // Tombol Keluar
        binding.tvFooter.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
