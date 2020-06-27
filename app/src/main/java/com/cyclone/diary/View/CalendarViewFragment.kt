package com.cyclone.diary.View

import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyclone.diary.Model.Event
import com.cyclone.diary.Presenter.EventModel
import com.cyclone.diary.Presenter.Adapter
import com.cyclone.diary.Presenter.RealmUtility
import com.cyclone.diary.R
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.model.*
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import io.realm.Realm
import kotlinx.android.synthetic.main.day_view_resource.view.*
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import org.threeten.bp.*
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.WeekFields
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
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

        val recyclerView = view.time_recycler_view
        recyclerView.layoutManager = LinearLayoutManager(context)
        val currentEvents = mutableListOf<Event>()
        calendarMonthBinder(view)
        calendarDayBinder(view, currentEvents, recyclerView)
        view.selected_day.setOnClickListener { v -> view.calendarView.smoothScrollToDate(LocalDate.now(), DayOwner.THIS_MONTH) }

        view.add_event.setOnClickListener { v ->
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.calendar_fragment, EventViewFragment()).addToBackStack("Calendar")
                .commit()
        }

        view.collapse.setOnClickListener { v ->
            view.toolbar.visibility = if(view.toolbar.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            view.collapse.rotation = if(view.collapse.rotation == 180f) 0f else 180f
        }
        recyclerView.adapter = Adapter(currentEvents)
        return view
    }

    private class DayViewContainer(view: View) : ViewContainer(view) {
        val tv = view.day_display
    }

    private fun calendarDayBinder(view: View, currentEvents: MutableList<Event>, recyclerView: RecyclerView) {
        val events = EventModel().getEvents(realm)
        var previousView: View? = null
        var previousDay: CalendarDay? = null
        val markedDays = mutableListOf<CalendarDay>()

        view.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                // Day of month
                container.tv.text = day.date.dayOfMonth.toString()
                // Current day
                if (!events.isEmpty()) {
                    events.forEach { t: Event ->
                        if (t.starttime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                .toString() == day.date.toString()
                        ) {
                            container.tv.setBackgroundResource(R.drawable.marked_day)
                            markedDays.add(day)
                        } else container.tv.background = null
                    }
                }

                if (day.date == LocalDate.now()) {
                    currentEvents.clear()
                    view.selected_day.text = "${day.date.dayOfWeek.getDisplayName(
                        TextStyle.FULL,
                        Locale.ENGLISH
                    )} ${day.date.dayOfMonth}"
                    container.tv.setBackgroundResource(R.drawable.oval_gap)
                    currentEvents.addAll(events.filter { event ->
                        event.starttime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                            .toString() == day.date.toString()
                    })

                }
                // Day colors
                container.tv.setTextColor(
                    if (day.owner == DayOwner.THIS_MONTH) Color.BLACK
                    else Color.rgb(236, 236, 236)
                )
                // Click listener
                container.tv.setOnClickListener { v ->
                    currentEvents.clear()
                    currentEvents.addAll(events.filter { event ->
                        event.starttime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                            .toString() == day.date.toString()
                    })
                    recyclerView.adapter?.notifyDataSetChanged()
                    if (previousDay?.date == LocalDate.now()) {
                        previousView?.background?.setTintList(null)
                        previousView?.setBackgroundResource(R.drawable.oval_gap)
                    } else if (markedDays.contains(previousDay)) previousView?.setBackgroundResource(
                        R.drawable.marked_day
                    )
                    else previousView?.setBackgroundResource(0)

                    previousView = v
                    previousDay = day
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
            currentMonth.minusMonths(12),
            currentMonth.plusMonths(12),
            firstDayOfWeek
        )

        view.calendarView.monthScrollListener = { calendarMonth ->
                view.name_month.text =
                    calendarMonth.yearMonth.month.getDisplayName(
                        TextStyle.FULL,
                        Locale.ENGLISH
                    ) + " " + calendarMonth.year
        }
        view.calendarView.scrollToMonth(currentMonth)
        view.calendarView.inDateStyle = InDateStyle.ALL_MONTHS
        view.calendarView.outDateStyle = OutDateStyle.END_OF_ROW
        view.calendarView.scrollMode = ScrollMode.PAGED
    }
}


