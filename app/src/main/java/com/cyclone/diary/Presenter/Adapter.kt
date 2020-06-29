package com.cyclone.diary.Presenter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyclone.diary.Model.Event
import com.cyclone.diary.R
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Adapter(private var values: MutableList<Event>) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    val timeList: List<LocalTime> = listOf(
        LocalTime.of(0, 0),
        LocalTime.of(1, 0),
        LocalTime.of(2, 0),
        LocalTime.of(3, 0),
        LocalTime.of(4, 0),
        LocalTime.of(5, 0),
        LocalTime.of(6, 0),
        LocalTime.of(7, 0),
        LocalTime.of(8, 0),
        LocalTime.of(9, 0),
        LocalTime.of(10, 0),
        LocalTime.of(11, 0),
        LocalTime.of(12, 0),
        LocalTime.of(13, 0),
        LocalTime.of(14, 0),
        LocalTime.of(15, 0),
        LocalTime.of(16, 0),
        LocalTime.of(17, 0),
        LocalTime.of(18, 0),
        LocalTime.of(19, 0),
        LocalTime.of(20, 0),
        LocalTime.of(21, 0),
        LocalTime.of(22, 0),
        LocalTime.of(23, 0)
    )
    lateinit var parent: ViewGroup
    override fun getItemCount(): Int = timeList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.time_list, parent, false)
        this.parent = parent
        return ViewHolder(itemView)
    }

    fun update(values: MutableList<Event>) {
        this.values = values
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.time.text = timeList[position].format(DateTimeFormatter.ofPattern("HH:mm"))
        val recyclerView = holder.itemsRecycler
        recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        val events = values.filter { event ->
            timeList[position].hour >= event.starttime.toInstant().atZone(ZoneId.systemDefault())
                .toLocalTime().hour &&
                    timeList[position].hour <= event.endtime.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalTime().hour
        }.toMutableList()
        recyclerView.adapter = EventsAdapter(events)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var time: TextView = itemView.findViewById(R.id.item_time)
        var itemsRecycler: RecyclerView = itemView.findViewById(R.id.items_recycler_view)
    }
}