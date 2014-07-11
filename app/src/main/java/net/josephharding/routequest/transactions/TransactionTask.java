package net.josephharding.routequest.transactions;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * The TransactionTask handles executing requests and populating responses.
 */

public class TransactionTask extends AsyncTask<Request, Integer, String> {

    private final String TAG = "TransactionTask";

    /**
     * The response object we've been provided with to populate result data
     */
    private IResponse mResponse;

    /**
     * The listener which will handle response callbacks
     */
    private GenericCompleteListener mListener;

    public interface GenericCompleteListener {

        public void onComplete(IResponse response);

    }

    public TransactionTask(GenericCompleteListener listener, IResponse response) {
        mListener = listener;
        mResponse = response;
    }

    /**
     * Performs the actual work of sending the request
     * @param requests
     * @return
     */
    protected String doInBackground(Request... requests) {
        return makeRequest(requests[0]);
    }

    /**
     * This method left unimplemented, however we could monitor progress on the request here
     * @param progress
     */
    protected void onProgressUpdate(Integer... progress) {}

    /**
     * After the request has been sent and received we handle processing of the response here
     * and then the callbacks with that processed response
     * @param result
     */
    protected void onPostExecute(String result) {
        mResponse.parseResponse(result);
        mListener.onComplete(mResponse);
    }

    /**
     * In this method we create the connection and read the response from the server
     *
     * @param request   the request holds information about the url and the params to use in the connection
     * @return
     */
    private String makeRequest(Request request) {
        String result = "";

        try {
            HttpsURLConnection urlConnection = (HttpsURLConnection) request.getURL().openConnection();
            try {
                // set timeout to 5 seconds
                urlConnection.setConnectTimeout(5000);

                // if we are doing a POST then add the params and configure the connection
                if(request.isPost()) {

                    // get the header keys and values from the request
                    Iterator iterator = request.getHeaders().entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry pairs = (Map.Entry)iterator.next();
                        urlConnection.setRequestProperty(pairs.getKey().toString(), pairs.getValue().toString());
                        iterator.remove();
                    }

                    urlConnection.setDoOutput(true);

                    DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
                    outputStream.writeBytes(request.getParams());
                    outputStream.flush();
                    outputStream.close();
                }

                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                    // construct our response object using a string builder
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((result = bufferedReader.readLine()) != null) {
                        stringBuilder.append(result);
                    }
                    result = stringBuilder.toString();
                } else {
                    Log.e(TAG, "got response from request code " + urlConnection.getResponseCode());
                }
            } finally {
                urlConnection.disconnect();
            }
        } catch (java.net.SocketTimeoutException e) {
            Log.e(TAG, e.toString());
        } catch(IOException e) {
            Log.e(TAG, e.toString());
        }

        return result;
    }

}