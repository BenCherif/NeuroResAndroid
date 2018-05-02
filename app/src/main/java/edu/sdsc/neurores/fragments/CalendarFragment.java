package edu.sdsc.neurores.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.sdsc.neurores.R;
import edu.sdsc.neurores.activities.LoginActivity;
import edu.sdsc.neurores.calendar.CalendarController;
import edu.sdsc.neurores.calendar.DayClickListener;
import edu.sdsc.neurores.calendar.abstraction.CalendarBackedWeek;
import edu.sdsc.neurores.calendar.abstraction.Day;
import edu.sdsc.neurores.calendar.abstraction.Event;
import edu.sdsc.neurores.calendar.abstraction.Week;
import edu.sdsc.neurores.calendar.adapter.DetailedEventAdapter;
import edu.sdsc.neurores.helper.JSONConverter;
import edu.sdsc.neurores.network.HTTPRequestCompleteListener;
import edu.sdsc.neurores.network.RequestWrapper;

/**
 * Created by tbpetersen on 4/13/2018.
 */


public class CalendarFragment extends Fragment {
    ViewPager viewPager;
    CalendarController calendarController;
    Week selectedWeek;
    Day selectedDay;
    ListView detailedEventListView;

    public CalendarFragment() {
        // required empty constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_calendar, container, false);
        detailedEventListView = (ListView) v.findViewById(R.id.detailed_event_list_view);
        setHasOptionsMenu(true);

        Calendar start = Calendar.getInstance();
        start.add(Calendar.YEAR, -5);
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 5);

        DayClickListener dayClickListener = new DayClickListener() {
            @Override
            public void onDayClicked(Day day) {
                if(selectedDay == null || !selectedDay.equals(day)){
                    detailedEventListView.setAdapter(new DetailedEventAdapter(v.getContext(),day.getEvents()));
                    selectedDay = day;
                }
            }
        };

        calendarController = new CalendarController(v.getContext(), start, end, dayClickListener);

        viewPager = (ViewPager) v.findViewById(R.id.calendar);
        //viewPager.setAdapter(calendarController.getPagerAdapter());
        viewPager.addOnPageChangeListener(calendarController.getOnPageChangeListener());
        alignLeftToolbarTitle();

        setupDatePicker(v);
        return v;
    }

    private void setupDatePicker(final View root) {
        Calendar myCalendar = Calendar.getInstance();

        final TextView calendarTitle = (TextView) root.findViewById(R.id.calendar_title);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                Calendar selected = Calendar.getInstance();
                selected.set(Calendar.YEAR, year);
                selected.set(Calendar.MONTH, monthOfYear);
                selected.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                Log.v("calendar", "Selected " + selected.get(Calendar.WEEK_OF_YEAR) + " " + selected.get(Calendar.YEAR));
                moveToSelectedWeek(new CalendarBackedWeek(selected, null));
            }

        };

        calendarTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Removed feature for now
                /*
                Week week = calendarController.getWeekAtPosition(viewPager.getCurrentItem());
                Calendar myCalendar = Calendar.getInstance();
                myCalendar.setTimeInMillis(week.getStartOfWeek());

                new DatePickerDialog(root.getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                 */
            }
        });
    }

    private void moveToSelectedWeek(Week week) {
        int pos = calendarController.getPositionOfWeek(week);
        if(pos == -1){
            Toast.makeText(getContext(), "Date out of available range", Toast.LENGTH_SHORT).show();
        }else{
            selectedWeek = week;
            viewPager.setCurrentItem(pos);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        HTTPRequestCompleteListener httpRequestCompleteListener = new HTTPRequestCompleteListener() {
            @Override
            public void onComplete(String s) {
                Log.v("event", s);

                List<Event> events = JSONConverter.toEventList(s);
                calendarController.setEvents(events);
                viewPager.setAdapter(calendarController.getPagerAdapter());
                moveToSelectedWeek(new CalendarBackedWeek(Calendar.getInstance(),null));


                List<Event> eventsForDay = new ArrayList<>();
                Calendar today = Calendar.getInstance();
                for(Event event : events){
                    Calendar calForEvent = event.getStart();

                    boolean sameYear = today.get(Calendar.YEAR) == calForEvent.get(Calendar.YEAR);
                    boolean sameDay = today.get(Calendar.DAY_OF_YEAR) == calForEvent.get(Calendar.DAY_OF_YEAR);

                    if(sameDay && sameYear){
                        eventsForDay.add(event);
                    }
                }
                detailedEventListView.setAdapter(new DetailedEventAdapter(getContext(), eventsForDay));
            }

            @Override
            public void onError(int i) {
                Log.v("event", i + "");

            }
        };

        RequestWrapper.getEvents(getContext(), getToken(),httpRequestCompleteListener);
        moveToSelectedWeek(new CalendarBackedWeek(Calendar.getInstance(),null));
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_wipe_thread);
        item.setEnabled(false);
        item.setVisible(false);
    }

    private void alignLeftToolbarTitle() {
        TextView toolbarTitle = (TextView) getActivity().findViewById(R.id.toolbar_title);
        Toolbar.LayoutParams params =  (Toolbar.LayoutParams)toolbarTitle.getLayoutParams();
        params.gravity = Gravity.START;
        toolbarTitle.setLayoutParams(params);
    }

    public String getToken(){
        SharedPreferences sPref = getContext().getSharedPreferences(LoginActivity.PREFS, Context.MODE_PRIVATE);
        return sPref.getString(LoginActivity.TOKEN, null);
    }
}
