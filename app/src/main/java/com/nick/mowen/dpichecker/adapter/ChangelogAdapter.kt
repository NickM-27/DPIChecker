package com.nick.mowen.dpichecker.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.nick.mowen.dpichecker.R

import java.util.ArrayList

class ChangelogAdapter(context: Context, private val data: ArrayList<Array<String>>) : RecyclerView.Adapter<ChangelogAdapter.ChangelogHolder>() {

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChangelogHolder {
        val row = layoutInflater.inflate(R.layout.custom_row_changelog, parent, false)
        return ChangelogHolder(row)
    }

    override fun onBindViewHolder(holder: ChangelogHolder, position: Int) {
        val data = this.data[position]
        holder.title.text = data[0]
        holder.text.text = data[1]
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ChangelogHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var title = itemView.findViewById<View>(R.id.title) as TextView
        var text = itemView.findViewById<View>(R.id.text) as TextView
    }
}