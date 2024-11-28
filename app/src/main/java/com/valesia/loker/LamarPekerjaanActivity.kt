package com.valesia.loker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.valesia.loker.databinding.ActivityLamarPekerjaanBinding
import com.google.firebase.database.FirebaseDatabase

class LamarPekerjaanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLamarPekerjaanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLamarPekerjaanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data yang diteruskan dari DetailActivityPelamar
        val namaPekerjaan = intent.getStringExtra("namaPekerjaan") ?: "Nama tidak tersedia"

        // Tampilkan nama pekerjaan pada form
        binding.namaPekerjaan.text = "Lamar Pekerjaan: $namaPekerjaan"

        // Tombol untuk mengirimkan lamaran
        binding.btnKirimLamaran.setOnClickListener {
            val namaPelamar = binding.editNamaPelamar.text.toString()
            val emailPelamar = binding.editEmailPelamar.text.toString()
            val cvLink = binding.editCvLink.text.toString()

            // Validasi input
            if (namaPelamar.isEmpty() || emailPelamar.isEmpty() || cvLink.isEmpty()) {
                Toast.makeText(this, "Harap isi semua field.", Toast.LENGTH_SHORT).show()
            } else if (!android.util.Patterns.WEB_URL.matcher(cvLink).matches()) {
                // Validasi URL
                Toast.makeText(this, "Harap masukkan link CV yang valid.", Toast.LENGTH_SHORT).show()
            } else if (!emailPelamar.endsWith("@gmail.com")) {
                // Validasi email agar hanya dapat menggunakan domain @gmail.com
                Toast.makeText(this, "Email harus menggunakan domain @gmail.com.", Toast.LENGTH_SHORT).show()
            } else {
                // Kirim data pelamar ke Firebase
                kirimLamaran(namaPekerjaan, namaPelamar, emailPelamar, cvLink)
            }
        }
    }

    // Fungsi untuk mengirim lamaran pekerjaan ke Firebase
    private fun kirimLamaran(namaPekerjaan: String, namaPelamar: String, emailPelamar: String, cvLink: String) {
        val databaseRef = FirebaseDatabase.getInstance().getReference("pelamar")

        val pelamarId = databaseRef.push().key
        val pelamarData = hashMapOf(
            "namaPekerjaan" to namaPekerjaan,
            "namaPelamar" to namaPelamar,
            "emailPelamar" to emailPelamar,
            "cvLink" to cvLink,
            "status" to "Pending"
        )

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
