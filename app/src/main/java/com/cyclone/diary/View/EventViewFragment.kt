package com.cyclone.diary.View

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cyclone.diary.Model.Event
import com.cyclone.diary.Presenter.EventModel
import com.cyclone.diary.Presenter.RealmUtility
import com.cyclone.diary.R
import io.realm.Realm
import kotlinx.android.synthetic.main.event_view.*
import kotlinx.android.synthetic.main.event_view.view.*
import kotlinx.android.synthetic.main.event_view.view.date
import kotlinx.android.synthetic.main.event_view.view.time
import kotlinx.android.synthetic.main.event_view.view.title
import java.sql.Timestamp
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.min

class EventViewFragment : Fragment() {
    var eventModel = EventModel()

    companion object {
        fun newInstance(
            id: Int,
            title: String,
            starttime: Date,
            endtime: Date,
            description: String
        ): EventViewFragment {
            val args = Bundle()
            args.putInt("id", id)
            args.putString("title", title)
            args.putSerializable("starttime", starttime)
            args.putSerializable("endtime", endtime)
            args.putString("description", description)
            args.putBoolean("update", true)
            val fragment = EventViewFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private val cStart = Calendar.getInstance()
    private val cEnd = Calendar.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.event_view, container, false)

        Realm.init(context)
        val realm = Realm.getInstance(RealmUtility.getDefaultConfig())

        if (arguments != null) {
            view.title.setText(arguments?.getString("title"))
            val time = arguments?.getSerializable("starttime") as Date
            val endtime = arguments?.getSerializable("endtime") as Date
            cStart.time = time
            cEnd.time = endtime
            view.time.text = SimpleDateFormat("HH:mm").format(time)
            view.endtime.text = SimpleDateFormat("HH:mm").format(endtime)
            view.date.setText(SimpleDateFormat("yyyy-MM-dd").format(time))
            view.description.setText(arguments?.getString("description"))
        }

        datePicker(view)
        timePicker(view)
        buttonListener(view, realm)
        return view
    }

    private fun datePicker(view: View) {
        view.date_picker.setOnClickListener {
            val dpd = DatePickerDialog(
                view.context,
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

    private fun timePicker(view: View) {
        view.time.setOnClickListener {
            val tpd = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                cStart.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cStart.set(Calendar.MINUTE, minute)
                time.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cStart.time)
            }
            TimePickerDialog(
                context,
                tpd,
                cStart.get(Calendar.HOUR_OF_DAY),
                cStart.get(Calendar.MINUTE),
                true
            ).show()
        }

        view.endtime.setOnClickListener {
            val tpd = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                cEnd.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cEnd.set(Calendar.MINUTE, minute)
                endtime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cEnd.time)
            }
            TimePickerDialog(
                context,
                tpd,
                cEnd.get(Calendar.HOUR_OF_DAY),
                cEnd.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun buttonListener(view: View, realm: Realm) {
        if (arguments == null) {
            view.add.setOnClickListener {
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
                val key = eventModel.addEvent(
                    realm,
                    if (eventModel.getEvents(realm).count() <= 0) {
                        Event(
                            0,
                            title.text.toString(),
                            Date.from(dateStart.atZone(ZoneId.systemDefault()).toInstant()),
                            Date.from(dateEnd.atZone(ZoneId.systemDefault()).toInstant()),
                            description.text.toString()
                        )
                    } else {
                        Event(
                            eventModel.getLastEvent(realm).id + 1,
                            title.text.toString(),
                            Date.from(dateStart.atZone(ZoneId.systemDefault()).toInstant()),
                            Date.from(dateEnd.atZone(ZoneId.systemDefault()).toInstant()),
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
            view.add.setOnClickListener {
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
                val key = eventModel.editEvent(
                    realm,
                    Event(
                        arguments!!.getInt("id"),
                        title.text.toString(),
                        Date.from(dateStart.atZone(ZoneId.systemDefault()).toInstant()),
                        Date.from(dateEnd.atZone(ZoneId.systemDefault()).toInstant()),
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