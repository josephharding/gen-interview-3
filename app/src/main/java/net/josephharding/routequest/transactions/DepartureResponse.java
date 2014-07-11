

package net.josephharding.routequest.transactions;


import android.util.Log;

import net.josephharding.routequest.datamodel.DepartureModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DepartureResponse implements IResponse {

    private final String TAG = "DepartureResponse";

    /**
     * Data to populate from the response string
     */
    private List<DepartureModel> mDepartures;

    public void parseResponse(String response) {
        if(response != null && !response.equals("")) {

            mDepartures = new ArrayList<DepartureModel>();

            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray rows = jsonObject.optJSONArray("rows");
                for(int i = 0; i < rows.length(); i++) {
                    JSONObject row = (JSONObject) rows.get(i);
                    mDepartures.add(new DepartureModel((Integer) row.get("id"), (String) row.get("calendar"), (String) row.get("time")));
                }
            } catch(JSONException exception) {
                Log.e(TAG, exception.toString());
                mDepartures = null;
            }
        }
    }

    public boolean success() {
        return mDepartures != null;
    }

    public List<DepartureModel> getResults() {
        return mDepartures;
    }

}
