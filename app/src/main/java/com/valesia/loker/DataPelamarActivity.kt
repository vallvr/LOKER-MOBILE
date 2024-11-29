package com.valesia.loker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.valesia.loker.databinding.ActivityDataPelamarBinding

class DataPelamarActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDataPelamarBinding
    private lateinit var pelamarList: MutableList<pelamar>
    private lateinit var filteredPelamarList: MutableList<pelamar>
    private lateinit var adapter: PelamarAdapter
    private var databaseReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataPelamarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        pelamarList = mutableListOf()
        filteredPelamarList = mutableListOf() // List untuk data yang terfilter
        adapter = PelamarAdapter(filteredPelamarList) // Gunakan filtered list pada adapter
        binding.recyclerView.adapter = adapter

        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("pelamar")

        // Fetch data from Firebase
        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                pelamarList.clear() // Kosongkan list sebelum diisi data baru
                for (itemSnapshot in snapshot.children) {
                    val pelamar = itemSnapshot.getValue(pelamar::class.java)
                    pelamar?.let { pelamarList.add(it) }
                }

                // Tampilkan atau sembunyikan emptyView
                if (pelamarList.isEmpty()) {
                    binding.emptyView.visibility = android.view.View.VISIBLE
                } else {
                    binding.emptyView.visibility = android.view.View.GONE
                }

                // Setelah data diterima, filter data dan tampilkan di RecyclerView
                filterPelamarList("") // Filter tanpa teks pencarian (semua data)
            }

            override fun onCancelled(error: DatabaseError) {
                // Tangani error jika gagal mengambil data
                Toast.makeText(this@DataPelamarActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })

        // Setup SearchView untuk mendengarkan perubahan teks pencarian
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // Tidak perlu aksi saat menekan tombol submit
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterPelamarList(newText ?: "") // Panggil fungsi filter dengan teks baru
                return true
            }
        })

        // Setup swipe untuk menghapus data
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val pelamar = filteredPelamarList[viewHolder.adapterPosition]

                // Tampilkan dialog konfirmasi
                showDeleteConfirmationDialog(pelamar, viewHolder.adapterPosition)
            }
        })

        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    // Fungsi untuk menyaring data berdasarkan pencarian
    private fun filterPelamarList(query: String) {
        filteredPelamarList.clear()

        // Jika query kosong, tampilkan semua data
        if (query.isEmpty()) {
            filteredPelamarList.addAll(pelamarList)
        } else {
            // Jika ada query, cari data yang sesuai
            for (pelamar in pelamarList) {
                if (pelamar.namaPelamar?.lowercase()?.contains(query.lowercase()) == true) {
                    filteredPelamarList.add(pelamar)
                }
            }
        }

        // Tampilkan atau sembunyikan emptyView
        if (filteredPelamarList.isEmpty()) {
            binding.emptyView.visibility = android.view.View.VISIBLE
        } else {
            binding.emptyView.visibility = android.view.View.GONE
        }

        // Update RecyclerView setelah data terfilter
        adapter.notifyDataSetChanged()
    }

    // Fungsi untuk menampilkan dialog konfirmasi penghapusan
    private fun showDeleteConfirmationDialog(pelamar: pelamar, position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Apakah Anda yakin ingin menghapus pelamar ${pelamar.namaPelamar}?")
            .setPositiveButton("Ya") { _, _ ->
                // Hapus data pelamar dari Firebase jika yakin
                deletePelamar(pelamar, position)
            }
            .setNegativeButton("Tidak") { _, _ ->
                // Kembalikan item ke dalam RecyclerView jika tidak jadi dihapus
                adapter.notifyItemChanged(position)
            }
        builder.create().show()
    }

    // Fungsi untuk menghapus pelamar dari Firebase dan RecyclerView
    private fun deletePelamar(pelamar: pelamar, position: Int) {
        val namaPelamar = pelamar.namaPelamar // Menggunakan nama pelamar sebagai kunci
        if (namaPelamar != null) {
            val databaseRef = FirebaseDatabase.getInstance().getReference("pelamar")

            // Menemukan pelamar berdasarkan nama (ini mungkin bermasalah jika nama tidak unik)
            databaseRef.orderByChild("namaPelamar").equalTo(namaPelamar).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Hapus pelamar berdasarkan nama
                        snapshot.children.first().ref.removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(this@DataPelamarActivity, "Pelamar berhasil dihapus", Toast.LENGTH_SHORT).show()
                                filteredPelamarList.removeAt(position)
                                adapter.notifyItemRemoved(position)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@DataPelamarActivity, "Gagal menghapus pelamar", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(this@DataPelamarActivity, "Pelamar tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DataPelamarActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
