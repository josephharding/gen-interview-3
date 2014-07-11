package net.josephharding.routequest.datamodel;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The RouteMode represents the data returned in a request for routes.
 *
 * This data type is parcelable in order to save it in an android bundle,
 * this means, for example, we won't have to re-query for search results
 * just because the screen rotated or refreshed
 */

public class RouteModel implements Parcelable {

    /**
     * The route's unique id
     */
    private int mId;

    /**
     * The short name for the route
     */
    private String mShortName;

    /**
     * The full name of the route
     */
    private String mLongName;

    public RouteModel(int id, String shortName, String longName) {
        mId = id;
        mShortName = shortName;
        mLongName = longName;
    }

    public int getRouteId() {
        return mId;
    }

    public String getRouteName() {
        return mLongName;
    }

    @Override
    public String toString() {
        return mLongName;
    }

    /*
    Implementation of Parcelable
    */

    public int describeContents() {
        return 0;
    }

    // write your object's data to the passed-in Parcel
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mLongName);
        out.writeString(mShortName);
        out.writeInt(mId);
    }

    // read the data in the same order you wrote it
    private RouteModel(Parcel in) {
        mLongName = in.readString();
        mShortName = in.readString();
        mId = in.readInt();
    }

    // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<RouteModel> CREATOR = new Parcelable.Creator<RouteModel>() {

        public RouteModel createFromParcel(Parcel in) {
            return new RouteModel(in);
        }

        public RouteModel[] newArray(int size) {
            return new RouteModel[size];
        }
    };

}
