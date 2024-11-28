package com.valesia.loker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log // Import Log untuk debugging
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.valesia.loker.databinding.ActivityDetailDataPelamarBinding

class DetailDataPelamarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailDataPelamarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailDataPelamarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari intent
        val namaPelamar = intent.getStringExtra("namaPelamar")
        val namaPekerjaan = intent.getStringExtra("namaPekerjaan")
        val linkCV = intent.getStringExtra("cvLink") // Ambil link CV
        val status = intent.getStringExtra("status")

        // Debugging log untuk memeriksa linkCV
        Log.d("DetailDataPelamar", "Link CV: $linkCV") // Menampilkan linkCV di log

        // Tampilkan data di layout
        binding.tvDetailNamaPelamar.text = "Nama Pelamar: $namaPelamar"
        binding.tvDetailNamaPekerjaan.text = "Nama Pekerjaan: $namaPekerjaan"
        binding.tvDetailStatus.text = "Status: $status"

        // Tampilkan link CV jika tersedia
        if (!linkCV.isNullOrEmpty()) {
            binding.tvDetailLinkCV.text = "Link CV: $linkCV"

            // Membuka link CV jika diklik
            binding.tvDetailLinkCV.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkCV))
                startActivity(intent)
            }
        } else {
            binding.tvDetailLinkCV.text = "Link CV tidak tersedia"
        }

        // Tombol Terima
        binding.btnTerima.setOnClickListener {
            updateStatusLamaran(namaPelamar, "Diterima")
        }

        // Tombol Tolak
        binding.btnTolak.setOnClickListener {
            updateStatusLamaran(namaPelamar, "Ditolak")
        }
    }

    private fun updateStatusLamaran(namaPelamar: String?, newStatus: String) {
        if (namaPelamar == null) return

        val databaseRef = FirebaseDatabase.getInstance().getReference("pelamar")
        databaseRef.orderByChild("namaPelamar").equalTo(namaPelamar)
            .get()
            .addOnSuccessListener { snapshot ->
                for (child in snapshot.children) {
                    child.ref.child("status").setValue(newStatus)
                }
                Toast.makeText(this, "Status diperbarui ke $newStatus", Toast.LENGTH_SHORT).show()
                finish() // Kembali ke daftar pelamar
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal memperbarui status", Toast.LENGTH_SHORT).show()
            }
    }
}
