package com.cyclone.diary.Presenter

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.cyclone.diary.Model.Event
import com.cyclone.diary.R
import com.cyclone.diary.View.EventView
import io.realm.Realm
import java.time.ZoneId

class EventsAdapter(private val values: MutableList<Event>) :
    RecyclerView.Adapter<EventsAdapter.ViewHolder>() {
    lateinit var parent: ViewGroup
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_list, parent, false)
        this.parent = parent
        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int = values.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = values[position].title
        holder.fulltime.text =
            "${values[position].starttime.toInstant().atZone(ZoneId.systemDefault())
                .toLocalTime()} - ${values[position].endtime.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalTime()}"
        holder.itemView.setOnClickListener { v ->
            val intent = Intent(v.context, EventView::class.java)
            intent.putExtra("id", values[position].id)
            intent.putExtra("title", values[position].title)
            intent.putExtra("starttime", values[position].starttime)
            intent.putExtra("endtime", values[position].endtime)
            intent.putExtra("description", values[position].description)
            holder.itemView.context.startActivity(intent)
        }
        holder.itemView.setOnLongClickListener { v ->
            val builder = AlertDialog.Builder(v.context)
            builder.setTitle("Confirmation")
            builder.setMessage("Do you want to delete this event?")
            builder.setPositiveButton("Delete") { dialog, which ->
                val eventModel = EventModel()
                Realm.init(v.context)
                val realm = Realm.getInstance(RealmUtility.getDefaultConfig())
                eventModel.deleteEvent(realm, values[position].id)
                values.removeAt(position)
                val fragment =
                    (v.context as FragmentActivity).supportFragmentManager.findFragmentByTag("Calendar")
                val ft =
                    (v.context as FragmentActivity).supportFragmentManager.beginTransaction()
                ft.setReorderingAllowed(false).detach(fragment!!).attach(fragment)
                    .commitAllowingStateLoss()
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            builder.create().show()
            true
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.item_title)
        var fulltime: TextView = itemView.findViewById(R.id.fulltime)
    }
}