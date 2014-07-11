package net.josephharding.routequest;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;


import net.josephharding.routequest.datamodel.DepartureModel;
import net.josephharding.routequest.datamodel.StopModel;
import net.josephharding.routequest.transactions.IResponse;
import net.josephharding.routequest.transactions.TransactionManager;
import net.josephharding.routequest.transactions.TransactionTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A fragment representing the details of a Route. This fragment can be used be either
 * {@link net.josephharding.routequest.RouteListActivity} (if you are in two pane mode) or
 * {@link net.josephharding.routequest.RouteDetailActivity} (if you are on a small device)
 */
public class RouteDetailFragment extends Fragment {

    private final String TAG = "RouteDetailFragment";

    /**
     * parameter from our parent activity, the route id
     */
    public static final String ARG_ITEM_ID = "route_id";

    /**
     * parameter from our parent activity, the route name
     */
    public static final String ARG_ITEM_NAME = "route_name";

    /**
     * key value for slicing our departure data blob returned from the server
     */
    private final String CALENDAR_WEEKDAY = "WEEKDAY";

    /**
     * key value for slicing our departure data blob returned from the server
     */
    private final String CALENDAR_SATURDAY = "SATURDAY";

    /**
     * key value for slicing our departure data blob returned from the server
     */
    private final String CALENDAR_SUNDAY = "SUNDAY";

    /**
     * A list of the name of stops on the given route we are representing in this fragment
     */
    private List<ExpandableListChildData> mStopsByRoute;

    /**
     * A list of the departure times from the first stop for this route on weekdays
     */
    private List<ExpandableListChildData> mWeekdayData;

    /**
     * A list of the departure times from the first stop for this route on saturdays
     */
    private List<ExpandableListChildData> mSaturdayData;

    /**
     * A list of the departure times from the first stop for this route on sundays
     */
    private List<ExpandableListChildData> mSundayData;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RouteDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_route_detail, container, false);

        // make sure the route details were successfully passed to us from the activity
        if (getArguments().containsKey(ARG_ITEM_ID) && getArguments().containsKey(ARG_ITEM_NAME)) {

            // set the name of the route
            TextView routeName = (TextView) view.findViewById(R.id.route_detail_name);
            routeName.setText(getArguments().getString(ARG_ITEM_NAME));

            // send an async request for our stops given the route id
            TransactionManager manager = new TransactionManager();
            manager.findStopsByRouteId(getArguments().getInt(ARG_ITEM_ID), new TransactionTask.GenericCompleteListener() {

                @Override
                public void onComplete(final IResponse response) {
                    if(response.success()) {

                        List<StopModel> responseData = (List<StopModel>) response.getResults();

                        mStopsByRoute = new ArrayList<ExpandableListChildData>();
                        for(int i = 0; i < responseData.size(); i++) {
                            mStopsByRoute.add(new ExpandableListChildData(getAlternatingColor(i), responseData.get(i).toString()));
                        }

                        if(mWeekdayData != null && mSaturdayData != null && mSundayData != null) {
                            populateDetailsList(view);
                        }
                    }
                }

            });

            // send an async request for our time tables given the route id
            manager.findDeparturesByRouteId(getArguments().getInt(ARG_ITEM_ID), new TransactionTask.GenericCompleteListener() {

                @Override
                public void onComplete(final IResponse response) {
                    if (response.success()) {

                        List<DepartureModel> data = response.getResults();

                        // extract the schedule data on a per calendar type basis from the departure model list
                        mWeekdayData = getDataOfCalendarType(data, CALENDAR_WEEKDAY);
                        mSaturdayData = getDataOfCalendarType(data, CALENDAR_SATURDAY);
                        mSundayData = getDataOfCalendarType(data, CALENDAR_SUNDAY);

                        if(mStopsByRoute != null) {
                            populateDetailsList(view);
                        }
                    }
                }

            });
        }

        return view;
    }

    /**
     * Populate our expandable list view with all the data now successfully
     * downloaded from our server
     *
     * @param view  a view object for finding our list layout
     */
    private void populateDetailsList(View view) {
        // populate the expandable list parent categories
        List<String> parents = new ArrayList<String>();
        parents.add(getString(R.string.detail_schedule_weekday));
        parents.add(getString(R.string.detail_schedule_saturday));
        parents.add(getString(R.string.detail_schedule_sunday));
        parents.add(getString(R.string.detail_schedule_streets));

        // populate each parent's children with various string list data
        HashMap<String, List<ExpandableListChildData>> mainList = new LinkedHashMap<String, List<ExpandableListChildData>>();
        mainList.put(getString(R.string.detail_schedule_weekday), mWeekdayData);
        mainList.put(getString(R.string.detail_schedule_saturday), mSaturdayData);
        mainList.put(getString(R.string.detail_schedule_sunday), mSundayData);
        mainList.put(getString(R.string.detail_schedule_streets), mStopsByRoute);

        // get a handle on the actual expandable list
        ExpandableListView exapandListView = (ExpandableListView) view.findViewById(R.id.route_details_list);
        exapandListView.setAdapter(new ExpandableListAdapter(getActivity().getApplicationContext(), parents, mainList));
    }

    /**
     * Helper function to extract lists of strings give a calendar type and departure model list
     *
     * @param input         the source data
     * @param calendarType  the type of data to return out of the source as a list of strings
     * @return              list of strings containing requested data
     */
    private List<ExpandableListChildData> getDataOfCalendarType(List<DepartureModel> input, String calendarType) {
        List<ExpandableListChildData> result = new ArrayList<ExpandableListChildData>();
        int count = input.size();
        for(int i = 0; i < count; i++) {
            if(input.get(i).getCalendar().equals(calendarType)) {
                result.add(new ExpandableListChildData(getAlternatingColor(i), input.get(i).getTime()));
            }
        }

        // if there are no departures data for a calendar type, alert the user with a specially colored label
        if(result.size() == 0) {
            result.add(new ExpandableListChildData(getResources().getColor(R.color.quest_blue), getString(R.string.detail_no_results)));
        }

        return result;
    }

    /**
     * Helper method for assigning alternating colors to list items
     * @param index
     * @return
     */
    private int getAlternatingColor(int index) {
        int result;
        if (index % 2 == 1) {
            result = Color.GRAY;
        } else {
            result = Color.WHITE;
        }
        return result;
    }

    /**
     * Little wrapper class for encapsulation color as a property of the display
     * along with the actual text data
     */
    class ExpandableListChildData {

        private int mColor;

        private String mText;

        public ExpandableListChildData(int backgroundColor, String text) {
            mColor = backgroundColor;
            mText = text;
        }

        public int getColor() {
            return mColor;
        }

        @Override
        public String toString() {
            return mText;
        }

    }

    /*

    The ExpandableListAdapter handles exporting our child hash and parent key
    values to an expandable and collapsible list view

     */
    class ExpandableListAdapter extends BaseExpandableListAdapter {

        private List<String> mParentTitles;

        private HashMap<String, List<ExpandableListChildData>> mChildData;

        private Context mContext;

        public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<ExpandableListChildData>> listChildData) {
            mContext = context;
            mParentTitles = listDataHeader;
            mChildData = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return mChildData.get(this.mParentTitles.get(groupPosition)).get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ExpandableListChildData childData = (ExpandableListChildData) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.route_detail_list_child, null);
            }

            TextView childTextView = (TextView) convertView.findViewById(R.id.route_detail_child);
            childTextView.setText(childData.toString());
            convertView.setBackgroundColor(childData.getColor());

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mChildData.get(mParentTitles.get(groupPosition)).size();
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.route_detail_list_parent, null);
            }

            TextView parentTextView = (TextView) convertView.findViewById(R.id.route_detail_parent);
            parentTextView.setText(getGroup(groupPosition).toString());

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        @Override
        public int getGroupCount() {
            return mParentTitles.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public Object getGroup(int position) {
            return mParentTitles.get(position);
        }

    }



}
