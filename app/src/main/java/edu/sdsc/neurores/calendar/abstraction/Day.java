package edu.sdsc.neurores.calendar.abstraction;

import android.view.View;

import java.util.List;

/**
 * Created by trevor on 4/25/18.
 */

public interface Day {
    static final String[] days = new String[]{"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

    List<Event> getEvents();
    void addEvent(Event event);
    String getDayOfWeek();
    int getDayInMonth();
    void deselect();
    void select();
    void setView(View view);
}
