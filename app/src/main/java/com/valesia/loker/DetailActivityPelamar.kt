package com.valesia.loker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.valesia.loker.databinding.ActivityDetailPelamarBinding
import com.google.firebase.database.FirebaseDatabase

class DetailActivityPelamar : AppCompatActivity() {

    private lateinit var binding: ActivityDetailPelamarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPelamarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari Intent dengan validasi
        val namaPekerjaan = intent.getStringExtra("namaPekerjaan") ?: "Nama tidak tersedia"
        val jenisPekerjaan = intent.getStringExtra("jenisPekerjaan") ?: "Jenis tidak tersedia"
        val gajiMin = intent.getIntExtra("gajiMin", 0)
        val gajiMax = intent.getIntExtra("gajiMax", 0)
        val wilayahPekerjaan = intent.getStringExtra("wilayahPekerjaan") ?: "Wilayah tidak tersedia"
        val syarat = intent.getStringExtra("syarat") ?: "Tidak ada syarat"

        // Log untuk debugging
        Log.d("DetailActivityPelamar", "namaPekerjaan: $namaPekerjaan")
        Log.d("DetailActivityPelamar", "jenisPekerjaan: $jenisPekerjaan")
        Log.d("DetailActivityPelamar", "gajiMin: $gajiMin")
        Log.d("DetailActivityPelamar", "gajiMax: $gajiMax")
        Log.d("DetailActivityPelamar", "wilayahPekerjaan: $wilayahPekerjaan")
        Log.d("DetailActivityPelamar", "syarat: $syarat")

        // Set data ke tampilan
        binding.detailNama.text = namaPekerjaan
        binding.detailJenis.text = "Jenis Pekerjaan : $jenisPekerjaan"
        binding.detailGajiMin.text = "Gaji Min : Rp. $gajiMin"
        binding.detailGajiMax.text = "Gaji Max : Rp. $gajiMax"
        binding.detailWilayah.text = "Wilayah Pekerjaan : $wilayahPekerjaan"
        binding.syarat.text = "Syarat dan Ketentuan :\n$syarat"

        // Tombol Lamar Pekerjaan
        binding.btnLamar.setOnClickListener {
            if (namaPekerjaan != "Nama tidak tersedia") {
                // Arahkan ke LamarPekerjaanActivity dan kirim data pekerjaan
                val intent = Intent(this, LamarPekerjaanActivity::class.java)
                intent.putExtra("namaPekerjaan", namaPekerjaan)
                intent.putExtra("jenisPekerjaan", jenisPekerjaan)
                intent.putExtra("gajiMin", gajiMin)
                intent.putExtra("gajiMax", gajiMax)
                intent.putExtra("wilayahPekerjaan", wilayahPekerjaan)
                intent.putExtra("syarat", syarat)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Nama pekerjaan tidak ditemukan.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk melamar pekerjaan
    private fun lamarkanPekerjaan(namaPekerjaan: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("pelamar")

        // Menggunakan nama pekerjaan untuk menambah data pelamar di Firebase
        val pelamarId = databaseRef.push().key
        val pelamarData = hashMapOf(
            "namaPekerjaan" to namaPekerjaan,
            "status" to "Pending"
        )

        // Menambahkan data pelamar ke Firebase
        pelamarId?.let {
            databaseRef.child(it).setValue(pelamarData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Lamaran pekerjaan berhasil dikirim.", Toast.LENGTH_SHORT).show()
                    finish() // Tutup Activity setelah berhasil melamar
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Gagal mengirim lamaran: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
