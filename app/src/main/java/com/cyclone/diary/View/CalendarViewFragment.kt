package com.cyclone.diary.View

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyclone.diary.Model.Event
import com.cyclone.diary.Presenter.Adapter
import com.cyclone.diary.Presenter.EventModel
import com.cyclone.diary.Presenter.eventSetting
import com.cyclone.diary.R
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.model.*
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import io.realm.RealmResults
import kotlinx.android.synthetic.main.day_view_resource.view.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.WeekFields
import java.time.ZoneId
import java.util.*

class CalendarViewFragment : Fragment(), View.OnClickListener {

    companion object {
        val instance = CalendarViewFragment()
        fun newInstance(): CalendarViewFragment = instance
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidThreeTen.init(context)
        EventModel.realmInit(context!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        val recyclerView = view.time_recycler_view
        recyclerView.layoutManager = LinearLayoutManager(context)

        calendarMonthBinder(view)
        calendarDayBinder(view, EventModel.getEvents(), recyclerView)

        view.selected_day.setOnClickListener(this)
        view.add_event.setOnClickListener(this)
        view.collapse.setOnClickListener(this)

        recyclerView.adapter = Adapter(eventSetting(recyclerView, dayCD))
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data == null) return
//        eventSetting(time_recycler_view, dayCD)
        calendarDayBinder(view!!, EventModel.getEvents(), time_recycler_view)
    }

    private class DayViewContainer(view: View) : ViewContainer(view) {
        val tv = view.day_display
    }

    private var dayCD: CalendarDay? = null

    fun calendarDayBinder(
        view: View,
        events: RealmResults<Event>,
        recyclerView: RecyclerView
    ) {
        var previousView: View? = null
        var previousDay: CalendarDay? = null
        val markedDays = mutableListOf<CalendarDay>()

        view.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                // Day of month
                container.tv.text = day.date.dayOfMonth.toString()
                // Search days with events
                val allEventsByDay = events.filter { event ->
                    event.starttime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        .toString() == day.date.toString()
                }
                if (allEventsByDay.isNotEmpty()) {
                    container.tv.setBackgroundResource(R.drawable.marked_day)
                    markedDays.add(day)
                } else container.tv.background = null
                // Current day events
                if (day.date == LocalDate.now()) {
                    view.selected_day.text = "${day.date.dayOfWeek.getDisplayName(
                        TextStyle.FULL,
                        Locale.ENGLISH
                    )} ${day.date.dayOfMonth}"
                    container.tv.setBackgroundResource(R.drawable.oval_gap)
                }
                // Day colors
                container.tv.setTextColor(
                    if (day.owner == DayOwner.THIS_MONTH) Color.BLACK
                    else Color.rgb(236, 236, 236)
                )
                // Click listener
                container.tv.setOnClickListener { v ->
                    dayCD = day
                    eventSetting(recyclerView, dayCD)

                    if (previousDay?.date == LocalDate.now()) {
                        previousView?.background?.setTintList(null)
                        previousView?.setBackgroundResource(R.drawable.oval_gap)
                    } else if (markedDays.contains(previousDay))
                        previousView?.setBackgroundResource(R.drawable.marked_day)
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
                if(dayCD == day) {
                    container.tv.performClick()
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.selected_day -> {
                v.calendarView.smoothScrollToDate(
                    LocalDate.now(),
                    DayOwner.THIS_MONTH
                )
            }
            R.id.add_event ->  {
                val intent = Intent(v.context, EventView::class.java)
                startActivityForResult(intent, 1)
            }
            R.id.collapse -> {
                view?.toolbar?.visibility =
                    if (view?.toolbar?.visibility == View.VISIBLE) View.GONE else View.VISIBLE
                v.collapse.rotation = if (v.collapse.rotation == 180f) 0f else 180f
            }
        }
    }
}


