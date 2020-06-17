package com.cyclone.diary.View

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cyclone.diary.R
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.model.*
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import kotlinx.android.synthetic.main.day_view_resource.view.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import org.threeten.bp.LocalDate
import org.threeten.bp.Year
import org.threeten.bp.YearMonth
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.WeekFields
import java.util.*

class CalendarViewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)
        AndroidThreeTen.init(context)

        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        val currentMonth = YearMonth.now()
        createHeader(currentMonth, view)

        view.calendarView.setup(
            currentMonth.minusMonths(6),
            currentMonth.plusMonths(6),
            firstDayOfWeek
        )
        view.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.tv.text = day.date.dayOfMonth.toString()
                container.tv.setTextColor(
                    if (day.owner == DayOwner.THIS_MONTH) Color.BLACK
                    else Color.rgb(236, 236, 236)
                )
                if (day.date == LocalDate.now()) {
                    container.tv.setBackgroundResource(R.drawable.oval_gap)
                }
                container.tv.setOnClickListener { v ->

                }
            }
        }
        view.calendarView.monthScrollListener = { calendarMonth ->
            view.name_month.text =
                calendarMonth.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        }
        view.calendarView.scrollToMonth(currentMonth)
        view.calendarView.inDateStyle = InDateStyle.ALL_MONTHS
        view.calendarView.outDateStyle = OutDateStyle.END_OF_ROW
        view.calendarView.scrollMode = ScrollMode.PAGED
        return view
    }

    private fun createHeader(currentMonth: YearMonth, view: View) {
        view.year_header.text = Year.now().toString()
        view.day_header.text = "${LocalDate.now().dayOfMonth} ${currentMonth.month.getDisplayName(
            TextStyle.FULL,
            Locale.ENGLISH
        )}"
        view.day_of_week_header.text =
            LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)

        view.header.setOnClickListener { calendarView.smoothScrollToMonth(currentMonth) }
    }

    private class DayViewContainer(view: View) : ViewContainer(view) {
        val tv = view.day_display
    }
}


