package com.cyclone.diary

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.cyclone.diary.View.CalendarViewFragment

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calendarViewFragment = CalendarViewFragment.newInstance()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.add(calendarViewFragment, "Calendar").replace(R.id.calendar_fragment, calendarViewFragment).commit()
    }
}