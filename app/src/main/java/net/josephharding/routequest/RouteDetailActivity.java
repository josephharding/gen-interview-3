package net.josephharding.routequest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;


/**
 *
 * This class is only used on small screen devices and is a wrapper
 * around the details fragment, {@link RouteDetailFragment}
 *
 */
public class RouteDetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);

        // set the action bar title to show "Back" instead of the default activity name
        setTitle(getString(R.string.action_back));

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // we only need to add the fragment when the saved state does not exist
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putInt(RouteDetailFragment.ARG_ITEM_ID, getIntent().getIntExtra(RouteDetailFragment.ARG_ITEM_ID, 0));
            arguments.putString(RouteDetailFragment.ARG_ITEM_NAME, getIntent().getStringExtra(RouteDetailFragment.ARG_ITEM_NAME));

            RouteDetailFragment fragment = new RouteDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction().add(R.id.route_detail_container, fragment).commit();
        }
    }

    /**
     *
     * Google is pushing the use of "up" navigation as opposed to
     * "back" navigation - however in the case of this activity the two
     * happen to be the same thing
     *
     * http://developer.android.com/design/patterns/navigation.html#up-vs-back
     *
     * @param item      menu item selected
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true;
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, RouteListActivity.class));
        } else {
            result = super.onOptionsItemSelected(item);
        }
        return result;
    }

}
