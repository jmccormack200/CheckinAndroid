package com.example.jmack.checkinandroid;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jmack on 5/26/16.
 */
public class SlackService extends IntentService {

    private String jsonString = "{ \"text\":\"I\'m Here\"}";
    private JSONObject mPayload;

    public SlackService() {
        super("Slack");
    }

    @Override
    public void onCreate(){
        super.onCreate();
        try {
            mPayload = new JSONObject(jsonString);
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String url =
                "https://hooks.slack.com/services/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";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        Log.v("URL", response);
                    }
                }, new Response.ErrorListener(){
                    @Override
            public void onErrorResponse(VolleyError error){
                        Log.v("URL", error.getMessage());
                    }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("payload", jsonString);

                return params;
            }
        };
        queue.add(stringRequest);
    }
}
