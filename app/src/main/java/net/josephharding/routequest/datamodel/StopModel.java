package net.josephharding.routequest.datamodel;

/**
 * The StopModel represents what is returned from a request for stops
 */

public class StopModel {

    /**
     * The stop's unique id
     */
    private int mId;

    /**
     * The route this stop belongs to
     */
    private int mRouteId;

    /**
     * The sequence in which this stop is occurs in the route
     */
    private int mSequence;

    /**
     * The name of the stop, usually a street name
     */
    private String mName;

    public StopModel(int id, int routeId, int sequence, String name) {
        mId = id;
        mRouteId = routeId;
        mSequence = sequence;
        mName = name;
    }

    @Override
    public String toString() {
        return mName;
    }

}
