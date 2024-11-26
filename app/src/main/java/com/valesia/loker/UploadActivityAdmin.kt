package com.valesia.loker

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.valesia.loker.databinding.ActivityUploadAdminBinding

class UploadActivityAdmin : AppCompatActivity() {

    private lateinit var binding: ActivityUploadAdminBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase
        databaseReference = FirebaseDatabase.getInstance("https://infoloker-1c466-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("infoloker")

        // Data untuk Spinner jenis pekerjaan
        val jenisList = listOf("Pilih Jenis Pekerjaan", "Full Time", "Part Time", "Freelance", "Internship")
        val jenisAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, jenisList)
        jenisAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.jenis.adapter = jenisAdapter

        // Data untuk Spinner wilayah pekerjaan
        val wilayahList = listOf("Pilih Wilayah Pekerjaan", "Jakarta", "Bandung", "Surabaya", "Yogyakarta")
        val wilayahAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, wilayahList)
        wilayahAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.wilayah.adapter = wilayahAdapter

        // Handle tombol simpan
        binding.saveButton.setOnClickListener {
            // Ambil data dari inputan
            val namaPekerjaan = binding.nama.text.toString().trim()
            val jenisPekerjaan = binding.jenis.selectedItem.toString()
            val gajiMin = binding.gajimin.text.toString().toIntOrNull() ?: 0
            val gajiMax = binding.gajimax.text.toString().toIntOrNull() ?: 0
            val wilayahPekerjaan = binding.wilayah.selectedItem.toString()
            val syarat = binding.syarat.text.toString().trim()

            // Validasi input
            if (namaPekerjaan.isEmpty() || syarat.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (namaPekerjaan.isEmpty()) {
                Toast.makeText(this, "Nama pekerjaan tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (jenisPekerjaan == "Pilih Jenis Pekerjaan") {
                Toast.makeText(this, "Silakan pilih jenis pekerjaan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (wilayahPekerjaan == "Pilih Wilayah Pekerjaan") {
                Toast.makeText(this, "Silakan pilih wilayah pekerjaan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (syarat.isEmpty()) {
                Toast.makeText(this, "Syarat tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (gajiMin <= 0 || gajiMax <= 0) {
                Toast.makeText(this, "Gaji minimum dan maksimum harus lebih besar dari 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (gajiMin > gajiMax) {
                Toast.makeText(this, "Gaji minimum tidak boleh lebih besar dari gaji maksimum", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Gunakan namaPekerjaan sebagai ID untuk key
            val infoloker = infoloker(
                namaPekerjaan = namaPekerjaan,
                jenisPekerjaan = jenisPekerjaan,
                gajiMin = gajiMin,
                gajiMax = gajiMax,
                wilayahPekerjaan = wilayahPekerjaan,
                syarat = syarat
            )

            // Simpan data ke Firebase dengan menggunakan namaPekerjaan sebagai key
            databaseReference.child(namaPekerjaan).setValue(infoloker)
                .addOnSuccessListener {
                    // Bersihkan form setelah data disimpan
                    binding.nama.text.clear()
                    binding.gajimin.text.clear()
                    binding.gajimax.text.clear()
                    binding.syarat.text.clear()
                    binding.jenis.setSelection(0)
                    binding.wilayah.setSelection(0)

                    Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()

                    // Pindah ke SemuaActivityAdmin setelah berhasil menyimpan data
                    val intent = Intent(this@UploadActivityAdmin, SemuaActivityAdmin::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Data gagal disimpan: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}