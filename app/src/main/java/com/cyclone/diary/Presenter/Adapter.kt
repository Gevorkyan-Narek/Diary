package com.cyclone.diary.Presenter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.cyclone.diary.Model.Event
import com.cyclone.diary.R
import com.cyclone.diary.View.EventViewFragment

class Adapter(private val values: List<Event>) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    lateinit var parent: ViewGroup
    override fun getItemCount(): Int = values.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.time_item_list, parent, false)
        this.parent = parent
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.time.text = values[position].starttime
        holder.title.text = values[position].title
        holder.fulltime.text = "${values[position].starttime} - ${values[position].endtime}"

        holder.itemView.setOnClickListener { v ->
            val detailedview = EventViewFragment.newInstance(
                values[position].id,
                holder.title.text.toString(),
                holder.time.text.toString(),
                values[position].endtime,
                values[position].date,
                values[position].description
            )
            (v.context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.calendar_fragment, detailedview).addToBackStack("").commit()
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var time: TextView = itemView.findViewById(R.id.item_time)
        var title: TextView = itemView.findViewById(R.id.item_event)
        var fulltime: TextView = itemView.findViewById(R.id.fulltime)

    }
}