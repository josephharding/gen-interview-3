package net.josephharding.routequest.transactions;


import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * The transaction manager wraps common requests and their appropriate response types as well
 * as handling the proper formatting of the request parameters.
 *
 * For testing and debugging purposes, here is the curl command that makes similar requests to those below:
 * curl
 * -H 'content-type:application/json'
 * -H 'Authorization: Basic V0tENE43WU1BMXVpTThWOkR0ZFR0ek1MUWxBMGhrMkMxWWk1cEx5VklsQVE2OA=='
 * -H 'X-AppGlu-Environment: staging'
 * -d '{"params":{"stopName":"%lauro linhares%"}}'
 * https://dashboard.appglu.com/v1/queries/findRoutesByStopName/run
 */

public class TransactionManager {

    private final String TAG = "TransactionManager";

    /**
     * The base URL our requests will be directed towards
     */
    private String mBaseUrl;

    public TransactionManager() {
        mBaseUrl = "https://dashboard.appglu.com/v1/queries/";
    }

    /**
     * Gets routes that stop at the given partial or whole stop name
     *
     * @param query     the partial or whole stop name
     * @param listener  the listener that will handle reading of the response object
     */
    public void findRoutesByStopName(String query, TransactionTask.GenericCompleteListener listener) {
        JSONObject jsonPackage = new JSONObject();
        try {
            JSONObject childData = new JSONObject();
            childData.put("stopName", query);
            jsonPackage.put("params", childData);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        new TransactionTask(listener, new RouteResponse()).execute(
                new Request(mBaseUrl + "findRoutesByStopName/run", jsonPackage, getHeaderValues()));
    }

    /**
     * Gets all the stops on the given route id
     *
     * @param routeId       the route id
     * @param listener      the listener that will handle reading of the response object
     */
    public void findStopsByRouteId(int routeId, TransactionTask.GenericCompleteListener listener) {
        JSONObject jsonPackage = new JSONObject();
        try {
            JSONObject childData = new JSONObject();
            childData.put("routeId", routeId);
            jsonPackage.put("params", childData);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        new TransactionTask(listener, new StopResponse()).execute(
                new Request(mBaseUrl + "findStopsByRouteId/run", jsonPackage, getHeaderValues()));
    }

    /**
     * Gets the departure data for a given route
     *
     * @param routeId   the route id
     * @param listener  the listener that will handle reading of the response object
     */
    public void findDeparturesByRouteId(int routeId, TransactionTask.GenericCompleteListener listener) {
        JSONObject jsonPackage = new JSONObject();
        try {
            JSONObject childData = new JSONObject();
            childData.put("routeId", routeId);
            jsonPackage.put("params", childData);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        new TransactionTask(listener, new DepartureResponse()).execute(
                new Request(mBaseUrl + "findDeparturesByRouteId/run", jsonPackage, getHeaderValues()));
    }

    /**
     * Return the headers for the requests defined in this manager
     * @return
     */
    private HashMap<String, String> getHeaderValues() {
        HashMap<String, String> result = new HashMap<String, String>();
        result.put("Content-Type", "application/json");
        result.put("Authorization", "Basic V0tENE43WU1BMXVpTThWOkR0ZFR0ek1MUWxBMGhrMkMxWWk1cEx5VklsQVE2OA==");
        result.put("X-AppGlu-Environment", "staging");
        return result;
    }

}

