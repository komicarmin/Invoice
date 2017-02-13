package com.example.hauw.invoice;

import android.content.Intent;
import android.content.SharedPreferences;
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
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class naslovnik extends ActionBarActivity {

    EditText etImeStranke, etPriimekStranke, etNaslovStranke, etHisnaSt, etPosta, etPostnaSt;
    Button btnUstvari;

    /*String imeStr, priimekStr, naslovStr, hisnaSt, postaStr, postnaStStr;

    HttpClient httpClient;
    HttpPost httpPost;

    ArrayList<NameValuePair> nameValuePairs;

    HttpResponse response;
    HttpEntity entity;*/

    String idUser;
    String idStr;
    String idPredracun;

    public static final String filename = "MyPrefs" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_naslovnik);
        StrictMode.enableDefaults();
        initialize();
        //DA DOBIMO ID
        SharedPreferences sp = getSharedPreferences("MyPrefs", 0);
        idUser = sp.getString("id_user", null);
        //idStr = sp.getString("id_stranka", null);
        Toast.makeText(getBaseContext(), idUser, Toast.LENGTH_SHORT).show();
    }

    private void initialize()
    {
       etImeStranke = (EditText) findViewById(R.id.etImeStranke);
       etPriimekStranke = (EditText) findViewById(R.id.etPriimekStranke);
       etNaslovStranke = (EditText) findViewById(R.id.etNaslovStranke);
       etHisnaSt = (EditText) findViewById(R.id.etHisnaSt);
       etPosta = (EditText) findViewById(R.id.etPosta);
       etPostnaSt = (EditText) findViewById(R.id.etPostnaSt);
       btnUstvari = (Button) findViewById(R.id.btnUstvari);
       btnUstvari.setOnClickListener(clickUstvari);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_naslovnik, menu);
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

    private static String convertStreamToString(InputStream is) throws UnsupportedEncodingException {
    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"), 8);
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

    public View.OnClickListener clickUstvari = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpClient httpClientIdPred = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://r4c.sc-nm.si/armin/android_connect/dodajstranko.php");
        HttpPost httpPostInsert = new HttpPost("http://r4c.sc-nm.si/armin/android_connect/dodajpredracun.php");

        //PRIDOBIVANJE stringov iz Edittextov
        String ime = etImeStranke.getText().toString();
        String priimek = etPriimekStranke.getText().toString();
        String naslov = etNaslovStranke.getText().toString();
        String hisnaSt = etHisnaSt.getText().toString();
        String posta = etPosta.getText().toString();
        String postnaSt= etPostnaSt.getText().toString();

        try
        {

            List<NameValuePair> nameValuePairs= new ArrayList<NameValuePair>(6);

            nameValuePairs.add(new BasicNameValuePair("ime", ime));
            nameValuePairs.add(new BasicNameValuePair("priimek", priimek));
            nameValuePairs.add(new BasicNameValuePair("naslov", naslov));
            nameValuePairs.add(new BasicNameValuePair("hisnaSt", hisnaSt));
            nameValuePairs.add(new BasicNameValuePair("posta", posta));
            nameValuePairs.add(new BasicNameValuePair("postnaSt", postnaSt));


            List<NameValuePair> zaInsert= new ArrayList<NameValuePair>(3);//DA POŠLJEMO PHPJU ID STRANKE IN ID USER ZA INSERT PREDRACUNA
            zaInsert.add((new BasicNameValuePair("id_user", idUser)));


            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse response =  httpClient.execute(httpPost);
            httpPost.setHeader("Content-type", "application/json");

            HttpEntity entity = response.getEntity();

            if(entity != null) {
                InputStream instream = entity.getContent();
                String result = convertStreamToString(instream);
                JSONArray arr = new JSONArray(result);
                JSONObject jObj = arr.getJSONObject(0);

                idStr = jObj.getString("id_stranka");
                zaInsert.add((new BasicNameValuePair("id_stranka", idStr)));

                httpPostInsert.setEntity(new UrlEncodedFormEntity(zaInsert, HTTP.UTF_8));
                HttpResponse resIdPredracun = httpClientIdPred.execute(httpPostInsert);
                httpPostInsert.setHeader("Content-type", "application/json");

                HttpEntity entityIdPredracun = resIdPredracun.getEntity();


                InputStream instreamIdPred = entityIdPredracun.getContent();
                String resIdPred = convertStreamToString(instreamIdPred);
                JSONArray arrIdPred = new JSONArray(resIdPred);
                JSONObject jObjIdPred = arrIdPred.getJSONObject(0);
                idPredracun = jObjIdPred.getString("id_predracun");
                Toast.makeText(getBaseContext(), idPredracun, Toast.LENGTH_SHORT).show();


                //Toast.makeText(getBaseContext(), idPredracun, Toast.LENGTH_SHORT).show();
                SharedPreferences spStranka = getSharedPreferences(filename, 0);
                SharedPreferences.Editor spedit = spStranka.edit();
                spedit.putString("id_stranka", idStr);
                spedit.commit();

                SharedPreferences spPredracun = getSharedPreferences(filename, 0);
                SharedPreferences.Editor speditPredracun = spPredracun.edit();
                speditPredracun.putString("id_predracun", idPredracun);
                speditPredracun.commit();

                Intent pojdiVSestavljanje = new Intent(naslovnik.this, sestavljanjeListView.class); // za prehod v nasledn activity
                startActivity(pojdiVSestavljanje);
            }


        } catch (Exception e) {
            Log.e("log_tag", "Krompir" + e.toString());
        }
        }
    };
}
