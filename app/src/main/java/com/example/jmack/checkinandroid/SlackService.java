package com.example.jmack.checkinandroid;

import android.app.IntentService;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
        try{
            URL url = new URL(
                    "https://hooks.slack.com/services/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"
            );
            HttpURLConnection client = (HttpURLConnection) url.openConnection();
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("Content-type", "application/json");
            client.setDoOutput(true);

            DataOutputStream dStream = new DataOutputStream(client.getOutputStream());
            dStream.writeBytes(jsonString);
            dStream.flush();
            dStream.close();


        } catch (java.net.MalformedURLException e ){
            e.printStackTrace();
        } catch (java.io.IOException e ){
            e.printStackTrace();
        }
    }
}
