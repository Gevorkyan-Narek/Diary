package com.cyclone.diary.Presenter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.cyclone.diary.Model.Event
import com.cyclone.diary.R
import com.cyclone.diary.View.EventViewFragment
import io.realm.Realm

class Adapter(private val values: MutableList<Event>) : RecyclerView.Adapter<Adapter.ViewHolder>() {

    lateinit var parent: ViewGroup
    override fun getItemCount(): Int = values.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.time_item_list, parent, false)
        this.parent = parent
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.time.text = values[position].starttime
        holder.title.text = values[position].title
        holder.fulltime.text = "${values[position].starttime} - ${values[position].endtime}"

        holder.itemView.setOnClickListener { v ->
            val detailedview = EventViewFragment.newInstance(
                values[position].id,
                holder.title.text.toString(),
                holder.time.text.toString(),
                values[position].endtime,
                values[position].date,
                values[position].description
            )
            (v.context as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.calendar_fragment, detailedview).addToBackStack("").commit()
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
                notifyDataSetChanged()
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
        var time: TextView = itemView.findViewById(R.id.item_time)
        var title: TextView = itemView.findViewById(R.id.item_event)
        var fulltime: TextView = itemView.findViewById(R.id.fulltime)
    }
}