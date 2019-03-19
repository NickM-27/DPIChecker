package com.nick.mowen.dpichecker.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView

import com.nick.mowen.dpichecker.R
import com.nick.mowen.dpichecker.data.MainInfo

class ListAdapter(context: Context, resource: Int, items: List<MainInfo>) : ArrayAdapter<MainInfo>(context, resource, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = convertView
                ?: LayoutInflater.from(context).inflate(R.layout.layout_donation_row, LinearLayout(context), false)
        val current = getItem(position)

        if (current != null) {
            val title = v?.findViewById<TextView>(R.id.title)
            val price = v?.findViewById<TextView>(R.id.price)
            val description = v?.findViewById<TextView>(R.id.description)

            if (title != null)
                title.text = current.title

            if (price != null)
                price.text = current.price

            if (description != null)
                description.text = current.description
        }

        return v
    }
}