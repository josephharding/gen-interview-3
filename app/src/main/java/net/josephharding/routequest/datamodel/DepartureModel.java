package net.josephharding.routequest.datamodel;

/**
 * The DepartureModel represents the data returned from a request for departures.
 */

public class DepartureModel {

    /**
     * A unique id for each departure occurrence
     */
    private int mId;

    /**
     * Either WEEKEDAY, SATURDAY, or SUNDAY - represents the different
     * schedules and frequency of departures
     */
    private String mCalendar;

    /**
     * The time of the departure from the first stop in a route, in a 24 hour range
     */
    private String mTime;

    public DepartureModel(int id, String calendar, String time) {
        mId = id;
        mCalendar = calendar;
        mTime = time;
    }

    public int getDepartureId() {
        return mId;
    }

    public String getCalendar() {
        return mCalendar;
    }

    public String getTime() {
        return mTime;
    }

    @Override
    public String toString() {
        return mTime;
    }

}
