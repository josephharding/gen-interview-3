package net.josephharding.routequest.transactions;


import android.util.Log;

import net.josephharding.routequest.datamodel.RouteModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RouteResponse implements IResponse {

    private final String TAG = "RouteResponse";

    /**
     * Data to populate from the response string
     */
    private List<RouteModel> mRoutes;

    public void parseResponse(String response) {
        if(response != null && !response.equals("")) {

            mRoutes = new ArrayList<RouteModel>();

            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray rows = jsonObject.optJSONArray("rows");
                for(int i = 0; i < rows.length(); i++) {
                    JSONObject row = (JSONObject) rows.get(i);
                    mRoutes.add(new RouteModel((Integer) row.get("id"), (String) row.get("shortName"), (String) row.get("longName")));
                }
            } catch(JSONException exception) {
                Log.e(TAG, exception.toString());
                mRoutes = null;
            }
        }
    }

    public boolean success() {
        return mRoutes != null;
    }

    public List<RouteModel> getResults() {
        return mRoutes;
    }

}
