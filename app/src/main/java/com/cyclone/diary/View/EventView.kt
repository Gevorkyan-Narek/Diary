package com.cyclone.diary.View

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cyclone.diary.Model.Event
import com.cyclone.diary.Presenter.EventModel
import com.cyclone.diary.Presenter.Utilities.Companion.checkOnNull
import com.cyclone.diary.R
import kotlinx.android.synthetic.main.event_view.*
import kotlinx.android.synthetic.main.fragment_calendar.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class EventView : AppCompatActivity(), View.OnClickListener {

    private val cStart = Calendar.getInstance()
    private val cEnd = Calendar.getInstance()
    var arguments: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_view)

        arguments = intent.extras
        if (arguments != null) initFields()

        datePicker()
        timePicker()
        add.setOnClickListener(this)
    }

    private fun initFields() {
        add.text = "Update"
        event_title.setText(arguments?.getString("title"))
        val time = arguments?.getSerializable("starttime") as Date
        val endtime = arguments?.getSerializable("endtime") as Date
        cStart.time = time
        cEnd.time = endtime
        this.time.text = SimpleDateFormat("HH:mm").format(time)
        this.endtime.text = SimpleDateFormat("HH:mm").format(endtime)
        date.setText(SimpleDateFormat("yyyy-MM-dd").format(time))
        description.setText(arguments?.getString("description"))
    }

    private fun datePicker() {
        date_picker.setOnClickListener {
            val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    cStart.set(Calendar.YEAR, year)
                    cStart.set(Calendar.MONTH, month)
                    cStart.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val dateString = SimpleDateFormat("yyyy-MM-dd").format(cStart.time)
                    date.setText(dateString)
                },
                cStart.get(Calendar.YEAR),
                cStart.get(Calendar.MONTH),
                cStart.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show()
        }
    }

    private fun timePicker() {
        time.setOnClickListener {
            val tpd = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                cStart.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cStart.set(Calendar.MINUTE, minute)
                time.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cStart.time)
            }
            TimePickerDialog(
                this,
                tpd,
                cStart.get(Calendar.HOUR_OF_DAY),
                cStart.get(Calendar.MINUTE),
                true
            ).show()
        }

        endtime.setOnClickListener {
            val tpd = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                cEnd.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cEnd.set(Calendar.MINUTE, minute)
                endtime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cEnd.time)
            }
            TimePickerDialog(
                this,
                tpd,
                cEnd.get(Calendar.HOUR_OF_DAY),
                cEnd.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    override fun onClick(v: View?) {
        val dateStart = LocalDateTime.of(
            cStart.get(Calendar.YEAR),
            cStart.get(Calendar.MONTH) + 1,
            cStart.get(Calendar.DAY_OF_MONTH),
            cStart.get(Calendar.HOUR_OF_DAY),
            cStart.get(Calendar.MINUTE)
        )
        val dateEnd = LocalDateTime.of(
            cStart.get(Calendar.YEAR),
            cStart.get(Calendar.MONTH) + 1,
            cStart.get(Calendar.DAY_OF_MONTH),
            cEnd.get(Calendar.HOUR_OF_DAY),
            cEnd.get(Calendar.MINUTE)
        )
        val event = if (EventModel.getEvents().count() <= 0) {
            Event(
                0,
                event_title.text.toString(),
                Date.from(dateStart.atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(dateEnd.atZone(ZoneId.systemDefault()).toInstant()),
                description.text.toString()
            )
        } else {
            Event(
                EventModel.getLastEvent().id + 1,
                event_title.text.toString(),
                Date.from(dateStart.atZone(ZoneId.systemDefault()).toInstant()),
                Date.from(dateEnd.atZone(ZoneId.systemDefault()).toInstant()),
                description.text.toString()
            )
        }
        if (checkOnNull(event_title.text, dateStart, dateEnd, date.text, v?.context!!)) {
            val key = if (arguments == null) {
                setResult(Activity.RESULT_OK, Intent().putExtra("res", "Added"))
                EventModel.addEvent(event)
            } else {
                event.id = arguments!!.getInt("id")
                CalendarViewFragment.instance.time_recycler_view.adapter?.notifyDataSetChanged()
                EventModel.editEvent(event)
            }
            if (key) finish()
            else Toast.makeText(v.context, "Error", Toast.LENGTH_SHORT).show()
        } else Toast.makeText(v.context, "Error", Toast.LENGTH_SHORT).show()
    }
}