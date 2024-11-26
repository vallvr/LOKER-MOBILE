package com.valesia.loker

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.valesia.loker.databinding.ActivityUploadAdminBinding

class EditActivityAdmin : AppCompatActivity() {

    private lateinit var binding: ActivityUploadAdminBinding
    private lateinit var databaseReference: DatabaseReference
    private var namaPekerjaan: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi Firebase
        databaseReference = FirebaseDatabase.getInstance("https://infoloker-1c466-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("infoloker")

        // Ambil namaPekerjaan dari Intent untuk mengedit data yang sesuai
        namaPekerjaan = intent.getStringExtra("namaPekerjaan")

        // Jika namaPekerjaan ada, ambil data dari Firebase
        if (namaPekerjaan != null) {
            getJobData(namaPekerjaan!!)
        }

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
            val namaPekerjaanInput = binding.nama.text.toString().trim()
            val jenisPekerjaan = binding.jenis.selectedItem.toString()
            val gajiMin = binding.gajimin.text.toString().toIntOrNull() ?: 0
            val gajiMax = binding.gajimax.text.toString().toIntOrNull() ?: 0
            val wilayahPekerjaan = binding.wilayah.selectedItem.toString()
            val syarat = binding.syarat.text.toString().trim()

            // Validasi input
            if (namaPekerjaanInput.isEmpty() || syarat.isEmpty()) {
                Toast.makeText(this, "Mohon lengkapi semua data", Toast.LENGTH_SHORT).show()
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

            // Simpan data yang telah diperbarui ke Firebase
            val infoloker = infoloker(
                namaPekerjaan = namaPekerjaanInput,
                jenisPekerjaan = jenisPekerjaan,
                gajiMin = gajiMin,
                gajiMax = gajiMax,
                wilayahPekerjaan = wilayahPekerjaan,
                syarat = syarat
            )

            // Update data di Firebase berdasarkan namaPekerjaan
            if (namaPekerjaan != null) {
                val query = databaseReference.orderByChild("namaPekerjaan").equalTo(namaPekerjaan)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // Perbarui data pekerjaan yang dipilih berdasarkan namaPekerjaan
                            snapshot.children.first().ref.setValue(infoloker)
                                .addOnSuccessListener {
                                    binding.nama.text.clear()
                                    binding.gajimin.text.clear()
                                    binding.gajimax.text.clear()
                                    binding.syarat.text.clear()
                                    binding.jenis.setSelection(0)
                                    binding.wilayah.setSelection(0)

                                    Toast.makeText(this@EditActivityAdmin, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()

                                    val intent = Intent(this@EditActivityAdmin, SemuaActivityAdmin::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(this@EditActivityAdmin, "Data gagal disimpan: ${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            Toast.makeText(this@EditActivityAdmin, "Pekerjaan tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@EditActivityAdmin, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }
    }

    private fun getJobData(namaPekerjaan: String) {
        val query = databaseReference.orderByChild("namaPekerjaan").equalTo(namaPekerjaan)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Ambil data pekerjaan yang pertama kali ditemukan
                    val job = snapshot.children.first().getValue(infoloker::class.java)

                    job?.let {
                        // Set data yang ada di form berdasarkan data yang diterima dari Firebase
                        binding.nama.setText(it.namaPekerjaan)
                        binding.gajimin.setText(it.gajiMin.toString())
                        binding.gajimax.setText(it.gajiMax.toString())
                        binding.syarat.setText(it.syarat)
                        binding.jenis.setSelection(getSpinnerIndex(binding.jenis, it.jenisPekerjaan))
                        binding.wilayah.setSelection(getSpinnerIndex(binding.wilayah, it.wilayahPekerjaan))
                    }
                } else {
                    Toast.makeText(this@EditActivityAdmin, "Pekerjaan tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditActivityAdmin, "Gagal mengambil data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getSpinnerIndex(spinner: Spinner, value: String): Int {
        val adapter = spinner.adapter
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i) == value) {
                return i
            }
        }
        return 0 // Default ke item pertama jika tidak ditemukan
    }
}