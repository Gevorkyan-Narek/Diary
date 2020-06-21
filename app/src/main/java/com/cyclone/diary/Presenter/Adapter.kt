package com.cyclone.diary.Presenter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cyclone.diary.Model.Event
import com.cyclone.diary.R

class Adapter(private val values: List<Event>) : RecyclerView.Adapter<Adapter.ViewHolder>() {
    override fun getItemCount(): Int = values.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.time_item_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.time.text = values[position].time
        holder.title.text = values[position].title
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var time: TextView = itemView.findViewById(R.id.item_time)
        var title: TextView = itemView.findViewById(R.id.item_event)
    }

}