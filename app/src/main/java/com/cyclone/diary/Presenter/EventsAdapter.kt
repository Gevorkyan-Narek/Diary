package com.cyclone.diary.Presenter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cyclone.diary.Model.Event
import com.cyclone.diary.R
import com.cyclone.diary.View.CalendarViewFragment
import com.cyclone.diary.View.EventView
import java.time.ZoneId

class EventsAdapter(private val values: MutableList<Event>) :
    RecyclerView.Adapter<EventsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = values.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = values[position].title
        holder.fulltime.text =
            "${values[position].starttime.toInstant().atZone(ZoneId.systemDefault())
                .toLocalTime()} - ${values[position].endtime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalTime()}"
        holder.itemView.setOnClickListener { v ->
            val intent = Intent(v.context, EventView::class.java)
            intent.putExtra("id", values[position].id)
            intent.putExtra("title", values[position].title)
            intent.putExtra("starttime", values[position].starttime)
            intent.putExtra("endtime", values[position].endtime)
            intent.putExtra("description", values[position].description)
            holder.itemView.context.startActivity(intent)
        }
        holder.itemView.setOnLongClickListener { v ->
            CalendarViewFragment.instance.deleteDialog(values, position)
            true
        }
    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.item_title)
        var fulltime: TextView = itemView.findViewById(R.id.fulltime)
    }
}