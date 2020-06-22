package com.cyclone.diary.View

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cyclone.diary.Model.Event
import com.cyclone.diary.Model.EventModel
import com.cyclone.diary.Presenter.RealmUtility
import com.cyclone.diary.R
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.event_view.*
import kotlinx.android.synthetic.main.event_view.view.*
import kotlinx.android.synthetic.main.event_view.view.date
import kotlinx.android.synthetic.main.event_view.view.time
import kotlinx.android.synthetic.main.event_view.view.title
import kotlinx.android.synthetic.main.fragment_calendar.view.*
import kotlinx.android.synthetic.main.fragment_calendar.view.add_event
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.util.*

class EventViewFragment : Fragment() {
    var eventModel = EventModel()

    companion object {
        fun newInstance(
            id: Int,
            title: String,
            time: String,
            endtime: String,
            date: String,
            description: String
        ): EventViewFragment {
            val args = Bundle()
            args.putInt("id", id)
            args.putString("title", title)
            args.putString("time", time)
            args.putString("endtime", endtime)
            args.putString("date", date)
            args.putString("description", description)
            args.putBoolean("update", true)
            val fragment = EventViewFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.event_view, container, false)
        val c = Calendar.getInstance()

        Realm.init(context)
        val realm = Realm.getInstance(RealmUtility.getDefaultConfig())

        if (arguments != null) {
            view.title.setText(arguments?.getString("title"))
            view.time.text = arguments?.getString("time")
            view.endtime.text = arguments?.getString("endtime")
            view.date.setText(arguments?.getString("date"))
            view.description.setText(arguments?.getString("description"))
        }

        datePicker(view, c)
        timePicker(view, c)
        buttonListener(view, realm)

        return view
    }

    private fun datePicker(view: View, c: Calendar) {
        view.date_picker.setOnClickListener {
            val dpd = DatePickerDialog(
                view.context,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    c.set(Calendar.YEAR, year)
                    c.set(Calendar.MONTH, month)
                    c.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    val dateString = SimpleDateFormat("yyyy-MM-dd").format(c.time)
                    date.setText(dateString)
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show()
        }
    }

    private fun timePicker(view: View, c: Calendar) {
        view.time.setOnClickListener {
            val tpd = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                c.set(Calendar.HOUR_OF_DAY, hourOfDay)
                c.set(Calendar.MINUTE, minute)
                time.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(c.time)
            }
            TimePickerDialog(
                context,
                tpd,
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                true
            ).show()
        }

        view.endtime.setOnClickListener {
            val tpd = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                c.set(Calendar.HOUR_OF_DAY, hourOfDay)
                c.set(Calendar.MINUTE, minute)
                endtime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(c.time)
            }
            TimePickerDialog(
                context,
                tpd,
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun buttonListener(view: View, realm: Realm) {
        if (arguments == null) {
            view.add.setOnClickListener {
                val key = eventModel.addEvent(
                    realm,
                    if (eventModel.getEvents(realm).count() <= 0) {
                        Event(
                            0,
                            title.text.toString(),
                            time.text.toString(),
                            endtime.text.toString(),
                            date.text.toString(),
                            description.text.toString()
                        )
                    } else {
                        Event(
                            eventModel.getLastEvent(realm).id + 1,
                            title.text.toString(),
                            time.text.toString(),
                            endtime.text.toString(),
                            date.text.toString(),
                            description.text.toString()
                        )
                    }
                )
                if (key) {
                    Toast.makeText(context, "Added", Toast.LENGTH_LONG).show()
                    fragmentManager?.popBackStack()
                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            view.add.text = "Update"
            Toast.makeText(context, "${arguments?.getInt("id")}", Toast.LENGTH_SHORT).show()
            view.add.setOnClickListener {
                val key = eventModel.editEvent(
                    realm,
                    Event(
                        arguments!!.getInt("id"),
                        title.text.toString(),
                        time.text.toString(),
                        endtime.text.toString(),
                        date.text.toString(),
                        description.text.toString()
                    )
                )
                if (key) {
                    Toast.makeText(context, "Updated", Toast.LENGTH_LONG).show()
                    fragmentManager?.popBackStack()
                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}