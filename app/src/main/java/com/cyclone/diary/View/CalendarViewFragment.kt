package com.cyclone.diary.View

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cyclone.diary.R
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kizitonwose.calendarview.model.*
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import kotlinx.android.synthetic.main.day_view_resource.view.*
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import kotlinx.android.synthetic.main.month_header_resource.view.*
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.WeekFields
import java.util.*
import kotlin.time.days

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
        view.calendarView.setup(
            currentMonth.minusMonths(6),
            currentMonth.plusMonths(6),
            firstDayOfWeek
        )
        view.calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.tv.text = day.date.dayOfMonth.toString()
            }
        }
        view.calendarView.scrollToMonth(currentMonth)
        view.calendarView.inDateStyle = InDateStyle.ALL_MONTHS
        view.calendarView.outDateStyle = OutDateStyle.END_OF_ROW
        view.calendarView.scrollMode = ScrollMode.PAGED
        return view
    }
}

class DayViewContainer(view: View) : ViewContainer(view) {
    val tv = view.day_display
}

class MonthHeaderContainer(view: View) : ViewContainer(view) {
    val tv = view.month_header
}