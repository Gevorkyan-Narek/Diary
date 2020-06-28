package com.cyclone.diary.Presenter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.cyclone.diary.Model.Event
import com.kizitonwose.calendarview.model.CalendarDay
import java.time.LocalDate
import java.time.ZoneId

fun eventSetting(recyclerView: RecyclerView?, dayCD: CalendarDay?): MutableList<Event> {
    val events = EventModel.getEvents()
    lateinit var currentEvents: List<Event>
    if (dayCD != null) {
        currentEvents = events.filter { event ->
            event.starttime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString() == dayCD.date.toString()
        }
    } else {
        currentEvents = events.filter { event ->
            event.starttime.toInstant().atZone(ZoneId.systemDefault())
                .toLocalDate() == LocalDate.now()
        }
    }

    if(recyclerView?.adapter != null) (recyclerView.adapter as Adapter).update(currentEvents.toMutableList())
    return currentEvents.toMutableList()
}