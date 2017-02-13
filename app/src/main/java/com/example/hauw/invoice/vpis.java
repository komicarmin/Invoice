package com.example.hauw.invoice;

import android.annotation.TargetApi;
import android.content.Entity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class vpis extends ActionBarActivity implements View.OnClickListener {

    EditText etUser, etPass;
    Button button;

    String username, password;

    HttpClient httpClient;
    HttpPost httpPost;

    ArrayList<NameValuePair> nameValuePairs;

    HttpResponse response;
    HttpEntity entity;

    public static final String filename = "MyPrefs" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vpis);
        StrictMode.enableDefaults(); //net!
        initialize();
    }

    private void initialize()
    {
        etUser = (EditText) findViewById(R.id.etUser);
        etPass = (EditText) findViewById(R.id.etPass);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
        //sp =

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vpis, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)

    private static String convertStreamToString(InputStream is) {
    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();

    }

    public void onClick(View v) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://r4c.sc-nm.si/armin/android_connect/loginuser.php");


        String username = etUser.getText().toString();
        String password = etPass.getText().toString();


        try
        {

            List<NameValuePair> nameValuePairs= new ArrayList<NameValuePair>(2);

            nameValuePairs.add(new BasicNameValuePair("username", username));
            nameValuePairs.add(new BasicNameValuePair("password", password));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response =  httpClient.execute(httpPost);
            httpPost.setHeader("Content-type", "application/json");

                HttpEntity entity = response.getEntity();
                if(entity != null)
                {

                    InputStream instream = entity.getContent();
                    //ZAJEMANJE PODATKOV IZ JSON ZAPISA

                    String result = convertStreamToString(instream);
                    JSONArray arr = new JSONArray(result);
                    JSONObject jObj = arr.getJSONObject(0);

                    String retUser = jObj.getString("upime");
                    String retPass = jObj.getString("geslo");
                    String retID = jObj.getString("id_user");

                    if(username.equals(retUser) && password.equals(retPass))
                    {
                      SharedPreferences spUser = getSharedPreferences(filename, 0);
                       SharedPreferences.Editor spedit = spUser.edit();

                       spedit.putString("id_user", retID);

                        spedit.commit();

                        //---------------ZA PRENOS IDJA V NASLEDN ACTIVITY-----------------------

                        Toast.makeText(getBaseContext(), "Uspesno", Toast.LENGTH_SHORT).show();
                        Intent pojdiVmeni = new Intent(vpis.this, meni.class); // za prehod v nasledn activity
                        startActivity(pojdiVmeni);

                    }
                    else
                    {
                        Toast.makeText(getBaseContext(), "Napaka pri prijavi", Toast.LENGTH_SHORT).show();
                    }
                }


        } catch (Exception e) {
            Log.e("log_tag", "Krompir" + e.toString());
        }
    }

}
