package net.josephharding.routequest;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;


/**
 * A fragment activity that makes use of either a single fragment showing a list of routes or
 * two fragments (on larger devices) a list and details fragment.
 *
 * This activity handles selection of routes by implementing the route selection listener
 * {@link net.josephharding.routequest.RouteListFragment.RouteSelectionListener}
 *
 * This activity is also registered in android manifest as a searchable activity and will open a
 * search dialog over the action bar and then handle the query.
 */
public class RouteListActivity extends ActionBarActivity implements RouteListFragment.RouteSelectionListener {

    private final String TAG = "RouteListActivity";

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet or a device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);

        // if this contain is present in the layout then we must be using the large layouts
        // res/values-large or res/values-sw600dp and we should use "two pane mode"
        if (findViewById(R.id.route_detail_container) != null) {
            mTwoPane = true;

            // configure the fragment to highlight list selections
            ((RouteListFragment) getSupportFragmentManager().findFragmentById(R.id.route_list)).setActivateOnItemClick(true);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // handle an incoming search intent, send the query to our fragment to handle the implementation of search
            String query = intent.getStringExtra(SearchManager.QUERY);
            ((RouteListFragment) getSupportFragmentManager().findFragmentById(R.id.route_list)).handleSearch(query);

            show(getString(R.string.message_searching));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                return onSearchRequested();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Callback method from the fragment indicating that the item with the given ID was selected.
     */
    @Override
    public void onRouteSelected(int id, String name) {
        if (mTwoPane) {
            // in two pane mode replace the fragment in the detail container with new details
            Bundle arguments = new Bundle();

            arguments.putInt(RouteDetailFragment.ARG_ITEM_ID, id);
            arguments.putString(RouteDetailFragment.ARG_ITEM_NAME, name);

            RouteDetailFragment fragment = new RouteDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().replace(R.id.route_detail_container, fragment).commit();
        } else {
            // on a small device just push a new activity on the stack
            Intent detailIntent = new Intent(this, RouteDetailActivity.class);
            detailIntent.putExtra(RouteDetailFragment.ARG_ITEM_ID, id);
            detailIntent.putExtra(RouteDetailFragment.ARG_ITEM_NAME, name);
            startActivity(detailIntent);
        }
    }

    /**
     * Helper method for popping a toast message
     * @param message
     */
    public void show(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(RouteListActivity.this, message, 5);
                toast.show();
            }
        });
    }
}
