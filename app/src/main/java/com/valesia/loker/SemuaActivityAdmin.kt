package com.valesia.loker

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.appcompat.widget.SearchView
import com.google.firebase.database.*
import com.valesia.loker.databinding.ActivitySemuaAdminBinding

class SemuaActivityAdmin : AppCompatActivity() {

    private lateinit var binding: ActivitySemuaAdminBinding
    private lateinit var dataList: MutableList<infoloker> // Data dari Firebase
    private lateinit var filteredList: MutableList<infoloker> // Data untuk pencarian
    private lateinit var adapter: MyAdapter
    private var databaseReference: DatabaseReference? = null
    private var eventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySemuaAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        val gridLayoutManager = GridLayoutManager(this@SemuaActivityAdmin, 1)
        binding.recyclerView.layoutManager = gridLayoutManager

        // Loading dialog
        val builder = AlertDialog.Builder(this@SemuaActivityAdmin)
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()

        // Initialize lists and adapter
        dataList = mutableListOf()
        filteredList = mutableListOf()
        adapter = MyAdapter(filteredList) // Gunakan filteredList untuk menampilkan data
        binding.recyclerView.adapter = adapter

        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("infoloker")

        // Fetch data from Firebase
        eventListener = databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(infoloker::class.java)
                    if (item != null) {
                        dataList.add(item)
                    }
                }
                // Salin data awal ke filteredList
                filteredList.clear()
                filteredList.addAll(dataList)
                adapter.notifyDataSetChanged()
                dialog.dismiss()

                // Periksa apakah data kosong
                checkEmptyData()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SemuaActivityAdmin, "Error loading data: ${error.message}", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        })

        // Setup SearchView
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchDataList(newText ?: "") // Panggil fungsi pencarian
                return true
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remove Firebase event listener to avoid memory leaks
        if (eventListener != null) {
            databaseReference!!.removeEventListener(eventListener!!)
        }
    }

    // Function to filter data based on search text
    private fun searchDataList(text: String) {
        filteredList.clear() // Bersihkan daftar hasil pencarian
        for (dataClass in dataList) {
            // Pastikan setiap elemen tidak null sebelum melakukan pencarian
            if ((dataClass.namaPekerjaan?.lowercase()?.contains(text.lowercase()) == true) ||
                (dataClass.jenisPekerjaan?.lowercase()?.contains(text.lowercase()) == true) ||
                (dataClass.wilayahPekerjaan?.lowercase()?.contains(text.lowercase()) == true)) {
                filteredList.add(dataClass)
            }
        }
        adapter.notifyDataSetChanged() // Perbarui RecyclerView dengan hasil pencarian

        // Periksa apakah data kosong setelah pencarian
        checkEmptyData()
    }

    // Function to check if data is empty and show the empty view
    private fun checkEmptyData() {
        if (filteredList.isEmpty()) {
            binding.emptyView.visibility = android.view.View.VISIBLE
            binding.recyclerView.visibility = android.view.View.GONE
        } else {
            binding.emptyView.visibility = android.view.View.GONE
            binding.recyclerView.visibility = android.view.View.VISIBLE
        }
    }
}
