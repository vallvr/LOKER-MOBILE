package com.valesia.loker

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.valesia.loker.R
import com.valesia.loker.infoloker

class AdapterPelamar(
    private var dataList: List<infoloker>
) : RecyclerView.Adapter<AdapterPelamar.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = dataList[position]

        holder.namaPekerjaan.text = data.namaPekerjaan
        holder.jenisPekerjaan.text = data.jenisPekerjaan
        holder.wilayahPekerjaan.text = data.wilayahPekerjaan

        holder.recCard.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivityPelamar::class.java)
            intent.putExtra("namaPekerjaan", data.namaPekerjaan)
            intent.putExtra("jenisPekerjaan", data.jenisPekerjaan)
            intent.putExtra("gajiMin", data.gajiMin)
            intent.putExtra("gajiMax", data.gajiMax)
            intent.putExtra("wilayahPekerjaan", data.wilayahPekerjaan)
            intent.putExtra("syarat", data.syarat)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun searchDataList(searchList: List<infoloker>) {
        dataList = searchList
        notifyDataSetChanged()
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val namaPekerjaan: TextView = itemView.findViewById(R.id.recNama)
        val jenisPekerjaan: TextView = itemView.findViewById(R.id.recJenis)
        val wilayahPekerjaan: TextView = itemView.findViewById(R.id.recWilayah)
        val recCard: View = itemView.findViewById(R.id.recCard)
    }
}
