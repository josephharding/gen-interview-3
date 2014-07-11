package net.josephharding.routequest;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import net.josephharding.routequest.datamodel.RouteModel;
import net.josephharding.routequest.transactions.IResponse;
import net.josephharding.routequest.transactions.TransactionManager;
import net.josephharding.routequest.transactions.TransactionTask;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment represents a list of routes. Any activity using this fragment
 * must implement {@link net.josephharding.routequest.RouteListFragment.RouteSelectionListener}
 * and handle the selection of elements. In two pane mode this fragment supports the highlighting
 * of the most recently selected list item to make it easier to see which route owns the details
 * being displayed in the detail fragment.
 */
public class RouteListFragment extends ListFragment {

    private final String TAG = "RouteListFragment";

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The routes we previous searched for will be bundled up and saved under this
     * key name
     */
    private static final String STATE_LATEST_ROUTES = "latest_routes";

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private RouteSelectionListener mRouteSelectionListener = sDummyRouteSelectionListener;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * The current route data populating this list fragment
     */
    private ArrayList<RouteModel> mData = new ArrayList<RouteModel>();

    /**
     * The array adapter handles exporting our route data into a list layout
     */
    private ArrayAdapter mArrayAdapter;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface RouteSelectionListener {
        /**
         * Callback for when an item has been selected.
         */
        public void onRouteSelected(int id, String name);
    }

    /**
     * A dummy implementation of the
     * {@link net.josephharding.routequest.RouteListFragment.RouteSelectionListener} interface
     * that does nothing. Used only when this fragment is not attached to an activity.
     */
    private static RouteSelectionListener sDummyRouteSelectionListener = new RouteSelectionListener() {
        @Override
        public void onRouteSelected(int id, String name) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RouteListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int layoutFile;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            layoutFile = android.R.layout.simple_list_item_1;
        } else {
            layoutFile = android.R.layout.simple_list_item_activated_1;
        }
        mArrayAdapter = new ArrayAdapter<RouteModel>(getActivity(), layoutFile, android.R.id.text1, mData);
        setListAdapter(mArrayAdapter);
    }

    @Override
    public void onActivityCreated(Bundle savedData) {
        super.onActivityCreated(savedData);
        setEmptyText(getString(R.string.no_routes_hint));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized state data
        if (savedInstanceState != null) {
            if(savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
                int position = savedInstanceState.getInt(STATE_ACTIVATED_POSITION);

                if (position == ListView.INVALID_POSITION) {
                    getListView().setItemChecked(mActivatedPosition, false);
                } else {
                    getListView().setItemChecked(position, true);
                }

                mActivatedPosition = position;
            }

            if(savedInstanceState.containsKey(STATE_LATEST_ROUTES)) {
                List<RouteModel> savedData = savedInstanceState.getParcelableArrayList(STATE_LATEST_ROUTES);
                refreshDataDisplay(savedData);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof RouteSelectionListener)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mRouteSelectionListener = (RouteSelectionListener) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mRouteSelectionListener = sDummyRouteSelectionListener;
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        mActivatedPosition = position;

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        if(mData != null && mData.get(position) != null) {
            mRouteSelectionListener.onRouteSelected(mData.get(position).getRouteId(), mData.get(position).getRouteName());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
        outState.putParcelableArrayList(STATE_LATEST_ROUTES, mData);
    }

    /**
     * Perform wild card transforms on the search query and make remote call to server
     * for results, on response refresh the data and notify the array adapter
     *
     * @param query the user's query term
     */
    public void handleSearch(String query) {

        // wrap the query in the wildcard character % so we can return results on partial street names
        query = "%" + query + "%";

        TransactionManager manager = new TransactionManager();
        manager.findRoutesByStopName(query, new TransactionTask.GenericCompleteListener() {

            @Override
            public void onComplete(final IResponse response) {
                if (response.success()) {
                    refreshDataDisplay(response.getResults());
                }
            }

        });
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
    }

    /**
     * Updates the source data for our list
     * @param refreshedData
     */
    private void refreshDataDisplay(List<RouteModel> refreshedData) {
        mData.clear();
        mData.addAll(refreshedData);
        mArrayAdapter.notifyDataSetChanged();
    }

}
