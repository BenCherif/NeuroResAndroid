package edu.sdsc.neurores.calendar.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import edu.sdsc.neurores.R;
import edu.sdsc.neurores.calendar.DayClickHandler;
import edu.sdsc.neurores.calendar.DayClickListener;
import edu.sdsc.neurores.calendar.abstraction.CalendarBackedDay;
import edu.sdsc.neurores.calendar.abstraction.CalendarBackedEventCalendar;
import edu.sdsc.neurores.calendar.abstraction.Day;
import edu.sdsc.neurores.calendar.abstraction.Event;
import edu.sdsc.neurores.calendar.abstraction.EventCalendar;
import edu.sdsc.neurores.calendar.abstraction.Week;


/**
 * Created by trevor on 4/21/18.
 */

public class CalendarAdapter extends PagerAdapter{
    private static final String[] days = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private Context context;
    private EventCalendar eventCalendar;
    private DayClickHandler dayClickHandler;
    private RecyclerView weekHolder;

    public CalendarAdapter(Context context, EventCalendar eventCalendar ,DayClickHandler dayClickHandler){
        this.context = context;
        this.eventCalendar = eventCalendar;
        this.dayClickHandler = dayClickHandler;
    }

    @Override
    public int getCount() {
        return eventCalendar.getNumWeeksInCalendar();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Week current = getWeek(position);
        weekHolder = (RecyclerView) LayoutInflater.from(context).inflate(R.layout.calendar_week, container, false);
        WeekAdapter weekAdapter = new WeekAdapter(context, current, eventCalendar.getSelectedDay(), dayClickHandler);
        dayClickHandler.registerDayClickListener(eventCalendar);
        weekHolder.setAdapter(weekAdapter);
        container.addView(weekHolder);
        return weekHolder;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (View) object;
    }

    public Week getWeek(int position){
        return eventCalendar.getWeek(position);
    }
}
