package com.valesia.loker

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PelamarAdapter(
    private val pelamarList: List<pelamar>
) : RecyclerView.Adapter<PelamarAdapter.PelamarViewHolder>() {

    class PelamarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val namaPelamar: TextView = view.findViewById(R.id.tvNamaPelamar)
        val namaPekerjaan: TextView = view.findViewById(R.id.tvNamaPekerjaan)
        val linkCV: TextView = view.findViewById(R.id.tvLinkCV) // Pastikan ID sesuai
        val status: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PelamarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pelamar, parent, false)
        return PelamarViewHolder(view)
    }

    override fun onBindViewHolder(holder: PelamarViewHolder, position: Int) {
        val pelamar = pelamarList[position]
        holder.namaPelamar.text = pelamar.namaPelamar ?: "Nama tidak tersedia"
        holder.namaPekerjaan.text = pelamar.namaPekerjaan ?: "Pekerjaan tidak tersedia"
        holder.status.text = pelamar.status ?: "Status tidak tersedia"

        // Tampilkan link CV
        if (!pelamar.cvLink.isNullOrEmpty()) {
            holder.linkCV.text = "Link CV: ${pelamar.cvLink}"
            holder.linkCV.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(pelamar.cvLink))
                holder.itemView.context.startActivity(intent)
            }
        } else {
            holder.linkCV.text = "Link CV tidak tersedia"
        }

        // Klik item untuk membuka DetailDataPelamarActivity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, DetailDataPelamarActivity::class.java)
            intent.putExtra("namaPelamar", pelamar.namaPelamar)
            intent.putExtra("namaPekerjaan", pelamar.namaPekerjaan)
            intent.putExtra("cvLink", pelamar.cvLink)
            intent.putExtra("status", pelamar.status)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return pelamarList.size
    }
}
