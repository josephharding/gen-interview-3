package net.josephharding.routequest.transactions;


import android.util.Log;

import net.josephharding.routequest.datamodel.StopModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StopResponse implements IResponse {

    private final String TAG = "StopResponse";

    /**
     * Data to populate from the response string
     */
    private List<StopModel> mStops;

    /**
     * Parse the string into a list of StopModels
     * @param response
     */
    public void parseResponse(String response) {
        // sample response string:
        // {"rows":[
        // {"id":22,"shortName":"131","longName":"AGRONÔMICA VIA GAMA D'EÇA","lastModifiedDate":"2009-10-26T02:00:00+0000","agencyId":9},
        // {"id":32,"shortName":"133","longName":"AGRONÔMICA VIA MAURO RAMOS","lastModifiedDate":"2012-07-23T03:00:00+0000","agencyId":9}
        // ],"rowsAffected":0}
        if(response != null && !response.equals("")) {

            mStops = new ArrayList<StopModel>();

            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray rows = jsonObject.optJSONArray("rows");
                for(int i = 0; i < rows.length(); i++) {
                    JSONObject row = (JSONObject) rows.get(i);
                    mStops.add(new StopModel((Integer) row.get("id"), (Integer) row.get("route_id"), (Integer) row.get("sequence"), (String) row.get("name")));
                }
            } catch(JSONException exception) {
                Log.e(TAG, exception.toString());
                mStops = null;
            }
        }
    }

    public boolean success() {
        return mStops != null;
    }

    public List<StopModel> getResults() {
        return mStops;
    }

}
