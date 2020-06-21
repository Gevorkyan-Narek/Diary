package com.cyclone.diary.View

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyclone.diary.Model.Event
import com.cyclone.diary.Model.EventModel
import com.cyclone.diary.Presenter.Adapter
import com.cyclone.diary.Presenter.RealmUtility
import com.cyclone.diary.R
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.model.*
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.day_view_resource.view.*
import kotlinx.android.synthetic.main.event_view.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import kotlinx.android.synthetic.main.fragment_calendar.view.recycler_view
import org.threeten.bp.*
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.WeekFields
import java.util.*

class CalendarViewFragment : Fragment() {

    companion object {
        fun newInstance(): CalendarViewFragment = CalendarViewFragment()
    }

    lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(context)
        Realm.init(context)
        realm = Realm.getInstance(RealmUtility.getDefaultConfig())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        val recyclerView = view.recycler_view
        recyclerView.layoutManager = LinearLayoutManager(context)
        val currentEvents = mutableListOf<Event>()

        calendarDayBinder(view, currentEvents, recyclerView)
        calendarMonthBinder(view)

        view.add_event.setOnClickListener { v ->
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.calendar_fragment, EventViewFragment()).addToBackStack("").commit()
        }

        recyclerView.adapter = Adapter(currentEvents)

        return view
    }

    private class DayViewContainer(view: View) : ViewContainer(view) {
        val tv = view.day_display
    }

    private fun calendarDayBinder(
        view: View,
        currentEvents: MutableList<Event>,
        recyclerView: RecyclerView
    ) {
        val events = EventModel().getEvents(realm)
        var previousView: View? = null

        view.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                // Day of month
                container.tv.text = day.date.dayOfMonth.toString()
                // Current day
                if (day.date == LocalDate.now()) {
                    currentEvents.clear()
                    container.tv.setBackgroundResource(R.drawable.oval_gap)
                    events.forEach { t: Event? ->
                        if (t?.date == day.date.toString())
                            currentEvents.add(t)
                    }
                }
                // Day colors
                container.tv.setTextColor(
                    if (day.owner == DayOwner.THIS_MONTH) Color.BLACK
                    else Color.rgb(236, 236, 236)
                )
                // Click listener
                container.tv.setOnClickListener { v ->
                    currentEvents.clear()
                    events.forEach { t: Event? ->
                        if (t?.date == day.date.toString()) {
                            currentEvents.add(t)
                            container.tv.setBackgroundResource(R.drawable.marked_day)
                        }
                    }
                    recyclerView.adapter?.notifyDataSetChanged()
                    if (previousView != null) {
                        previousView?.setBackgroundResource(0)
                    }
                    previousView = v
                    v.setBackgroundResource(R.drawable.oval_gap)
                    v.background.setTint(Color.RED)

                    view.selected_day.text = "${day.date.dayOfWeek.getDisplayName(
                        TextStyle.FULL,
                        Locale.ENGLISH
                    )} ${day.date.dayOfMonth}"
                }
            }
        }
    }

    private fun calendarMonthBinder(view: View) {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val currentMonth = YearMonth.now()

        view.calendarView.setup(
            currentMonth.minusMonths(6),
            currentMonth.plusMonths(6),
            firstDayOfWeek
        )

        view.calendarView.monthScrollListener = { calendarMonth ->
            view.name_month.text =
                calendarMonth.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        }
        view.calendarView.scrollToMonth(currentMonth)
        view.calendarView.inDateStyle = InDateStyle.ALL_MONTHS
        view.calendarView.outDateStyle = OutDateStyle.END_OF_ROW
        view.calendarView.scrollMode = ScrollMode.PAGED
    }
}


