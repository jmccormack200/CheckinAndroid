package com.example.jmack.checkinandroid;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

/**
 * The Slack Service class manages sending data to the Slack Webhook. It is triggered upon the reception of an
 * intent from another class. It also will clear the notification that sent the intent.
 */
public class SlackService extends IntentService {

    private static final String jsonString = "{ " +
            "\"username\":\"Bender\",\n" +
            "\"icon_emoji\":\":bender:\",\n" +
            "\"text\":\"I\'m Here\"}";

    public static final String POST_PARAMETER = "payload";

    private static final String url =
            "https://hooks.slack.com/services/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";

    private final String LOG_TAG = this.getClass().getSimpleName();

    public SlackService() {
        super("Slack");
    }

    /**
     * When the intent is received, a POST request is made to the Slack webhook and the notification is cleared.
     *
     * @param intent The intent received from the previous service.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        //sendSlackPOSTRequest();
        removeNotification(intent);
        Log.v(LOG_TAG, "Slack Intent Received");
    }

    /**
     * A Request Queue is established using the Volley library. The Slack Request is built and then
     * added to the request Queue.
     */
    public void sendSlackPOSTRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = createSlackPOSTRequest();
        queue.add(stringRequest);
    }

    /**
     * The Slack Post Request is built and returned in a format usable by Volley
     *
     * @return The Request that can then be passed into the Volley Queue
     */
    public StringRequest createSlackPOSTRequest() {
        return new StringRequest(Request.Method.POST, url,
                                 new Response.Listener<String>() {
                                     @Override
                                     public void onResponse(String response) {
                                         Log.v("URL", response);
                                     }
                                 }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(LOG_TAG, error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(POST_PARAMETER, jsonString);

                return params;
            }
        };
    }

    /**
     * The Notification that triggered the Slack service is removed to avoid sending duplicate messages.
     *
     * @param intent The intent sent from the previous Service
     */
    public void removeNotification(Intent intent) {
        int Id = Integer.valueOf(intent.getStringExtra(Intent.EXTRA_TEXT));
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Id);

    }
}
