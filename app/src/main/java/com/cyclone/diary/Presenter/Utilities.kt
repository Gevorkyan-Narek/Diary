package com.cyclone.diary.Presenter

import android.content.Context
import android.text.Editable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.cyclone.diary.Model.Event
import com.kizitonwose.calendarview.model.CalendarDay
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

class Utilities {
    companion object {
        fun eventSetting(recyclerView: RecyclerView?, dayCD: CalendarDay?): MutableList<Event> {
            val events = EventModel.getEvents()
            lateinit var currentEvents: List<Event>
            if (dayCD != null) {
                currentEvents = events.filter { event ->
                    event.starttime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        .toString() == dayCD.date.toString()
                }
            } else {
                currentEvents = events.filter { event ->
                    event.starttime.toInstant().atZone(ZoneId.systemDefault())
                        .toLocalDate() == LocalDate.now()
                }
            }
            if (recyclerView?.adapter != null) (recyclerView.adapter as Adapter).update(
                currentEvents.toMutableList()
            )
            return currentEvents.toMutableList()
        }

        fun comparisonDates(eventDate: Date, date: org.threeten.bp.LocalDate): Boolean {
            return eventDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                .toString() == date.toString()
        }

        fun checkTime(time: LocalTime, leftBorder: Date, rightBorder: Date): Boolean {
            return time.hour >= leftBorder.toInstant().atZone(ZoneId.systemDefault())
                .toLocalTime().hour && time.hour <= rightBorder.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalTime().hour
        }

        fun checkOnNull(
            title: Editable,
            timeStart: LocalDateTime?,
            timeEnd: LocalDateTime?,
            date: Editable,
            context: Context
        ): Boolean {
            return when {
                title.isBlank() -> {
        Toast.makeText(context, "Write a title", Toast.LENGTH_SHORT).show()
                    false
                }
                timeStart == null || timeEnd == null -> {
        Toast.makeText(context, "Choose time", Toast.LENGTH_SHORT).show()
                    false
                }
                timeStart > timeEnd -> {
        Toast.makeText(context, "Time of start can't be more than time of end", Toast.LENGTH_SHORT).show()
                    false
                }
                date.isBlank() -> {
        Toast.makeText(context, "Choose date", Toast.LENGTH_SHORT).show()
                    false
                }
                else -> true
            }
        }
    }
}