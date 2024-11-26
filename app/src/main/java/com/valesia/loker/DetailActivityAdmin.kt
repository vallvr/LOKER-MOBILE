package com.valesia.loker

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.valesia.loker.databinding.ActivityDetailAdminBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.android.gms.tasks.OnFailureListener

class DetailActivityAdmin : AppCompatActivity() {

    private lateinit var binding: ActivityDetailAdminBinding
    private var isFabMenuOpen = false // Status untuk menu FAB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari Intent dengan validasi
        val namaPekerjaan = intent.getStringExtra("namaPekerjaan") ?: "Nama tidak tersedia"
        val jenisPekerjaan = intent.getStringExtra("jenisPekerjaan") ?: "Jenis tidak tersedia"
        val gajiMin = intent.getIntExtra("gajiMin", 0)
        val gajiMax = intent.getIntExtra("gajiMax", 0)
        val wilayahPekerjaan = intent.getStringExtra("wilayahPekerjaan") ?: "Wilayah tidak tersedia"
        val syarat = intent.getStringExtra("syarat") ?: "Tidak ada syarat"

        // Log untuk debugging
        Log.d("DetailActivityAdmin", "namaPekerjaan: $namaPekerjaan")
        Log.d("DetailActivityAdmin", "jenisPekerjaan: $jenisPekerjaan")
        Log.d("DetailActivityAdmin", "gajiMin: $gajiMin")
        Log.d("DetailActivityAdmin", "gajiMax: $gajiMax")
        Log.d("DetailActivityAdmin", "wilayahPekerjaan: $wilayahPekerjaan")
        Log.d("DetailActivityAdmin", "syarat: $syarat")

        // Set data ke tampilan
        binding.detailNama.text = namaPekerjaan
        binding.detailJenis.text = "Jenis Pekerjaan : $jenisPekerjaan"
        binding.detailGajiMin.text = "Gaji Min : Rp. $gajiMin"
        binding.detailGajiMax.text = "Gaji Max : Rp. $gajiMax"
        binding.detailWilayah.text = "Wilayah Pekerjaan : $wilayahPekerjaan"
        binding.syarat.text = "Syarat dan Ketentuan :\n$syarat"

        // Tombol Kembali (FAB Main)
        binding.fabMain.setOnClickListener {
            if (isFabMenuOpen) {
                closeFabMenu()
            } else {
                openFabMenu()
            }
        }

        // Tombol Edit
        binding.fabEdit.setOnClickListener {
            // Ambil namaPekerjaan dari data yang sedang ditampilkan di DetailActivityAdmin
            val intent = Intent(this@DetailActivityAdmin, EditActivityAdmin::class.java)
            intent.putExtra("namaPekerjaan", namaPekerjaan)  // Kirimkan namaPekerjaan
            startActivity(intent)
        }

        // Tombol Hapus
        binding.fabDelete.setOnClickListener {
            if (namaPekerjaan != "Nama tidak tersedia") {
                deleteLokerFromFirebase(namaPekerjaan)
            } else {
                Toast.makeText(this, "Nama pekerjaan tidak ditemukan.", Toast.LENGTH_SHORT).show()
            }
            closeFabMenu()
        }
    }

    // Fungsi membuka FAB Menu
    private fun openFabMenu() {
        if (!isFabMenuOpen) {
            binding.fabEdit.visibility = View.VISIBLE
            binding.fabDelete.visibility = View.VISIBLE

            binding.fabMain.animate().rotation(45f).setDuration(300).start()
            binding.fabEdit.animate().translationX(-150f).alpha(1f).setDuration(300).start()
            binding.fabDelete.animate().translationX(150f).alpha(1f).setDuration(300).start()

            isFabMenuOpen = true
        }
    }

    // Fungsi menutup FAB Menu
    private fun closeFabMenu() {
        if (isFabMenuOpen) {
            binding.fabEdit.animate().translationX(0f).alpha(0f).setDuration(300).withEndAction {
                binding.fabEdit.visibility = View.GONE
            }.start()

            binding.fabDelete.animate().translationX(0f).alpha(0f).setDuration(300).withEndAction {
                binding.fabDelete.visibility = View.GONE
            }.start()

            binding.fabMain.animate().rotation(0f).setDuration(300).start()

            isFabMenuOpen = false
        }
    }

    // Fungsi menghapus data di Firebase
    private fun deleteLokerFromFirebase(namaPekerjaan: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("infoloker")

        // Pastikan bahwa `namaPekerjaan` ada pada node yang sesuai di Firebase
        databaseRef.orderByChild("namaPekerjaan").equalTo(namaPekerjaan)
            .get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists() && dataSnapshot.hasChildren()) {
                    for (childSnapshot in dataSnapshot.children) {
                        // Hapus setiap node yang sesuai
                        childSnapshot.ref.removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Data berhasil dihapus.", Toast.LENGTH_SHORT).show()
                                finish() // Tutup Activity setelah data berhasil dihapus
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this, "Gagal menghapus data: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Data tidak ditemukan.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Terjadi kesalahan: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
