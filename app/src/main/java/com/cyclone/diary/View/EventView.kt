package com.cyclone.diary.View

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cyclone.diary.MainActivity
import com.cyclone.diary.Model.Event
import com.cyclone.diary.Presenter.Adapter
import com.cyclone.diary.Presenter.EventModel
import com.cyclone.diary.Presenter.RealmUtility
import com.cyclone.diary.R
import io.realm.Realm
import kotlinx.android.synthetic.main.event_view.*
import kotlinx.android.synthetic.main.event_view.view.*
import kotlinx.android.synthetic.main.event_view.view.date
import kotlinx.android.synthetic.main.event_view.view.time
import kotlinx.android.synthetic.main.time_list.*
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.min

class EventView : AppCompatActivity() {

    private val cStart = Calendar.getInstance()
    private val cEnd = Calendar.getInstance()
    var arguments: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.event_view)

        arguments = intent.extras

        if (arguments != null) {
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

        datePicker()
        timePicker()
        buttonListener()
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

    private fun buttonListener() {
        if (arguments == null) {
            add.setOnClickListener {
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
                val key = EventModel.addEvent(
                    if (EventModel.getEvents().count() <= 0) {
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
                )
                if (key) {
                    Toast.makeText(this, "Added", Toast.LENGTH_LONG).show()
                    setResult(Activity.RESULT_OK, Intent().putExtra("res", "Added"))
                    finish()
                } else {
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            add.text = "Update"
            add.setOnClickListener {
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
                val key = EventModel.editEvent(
                    Event(
                        arguments!!.getInt("id"),
                        event_title.text.toString(),
                        Date.from(dateStart.atZone(ZoneId.systemDefault()).toInstant()),
                        Date.from(dateEnd.atZone(ZoneId.systemDefault()).toInstant()),
                        description.text.toString()
                    )
                )
                if (key) {
                    Toast.makeText(this, "Updated", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}