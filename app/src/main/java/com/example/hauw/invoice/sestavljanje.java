package com.example.hauw.invoice;

import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class sestavljanje extends ActionBarActivity implements View.OnClickListener {


    String idUser;
    String idStr;
    String idPredracun;
    String str[];
    EditText etAutoC;
    EditText etKolicina;
    EditText etCena;
    //TABELA
    TableLayout tl;

            HttpClient httpClient;
    HttpPost httpPost;
    List<String> list = new ArrayList<String>();

    HttpResponse response;
    HttpEntity entity;

    AutoCompleteTextView actv;

    public int a = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sestavljanje);
        StrictMode.enableDefaults();

        napolniTabeloZDodanimiArtikli();
        initialize();

        SharedPreferences sp = getSharedPreferences("MyPrefs", 0);
        idUser = sp.getString("id_user", null);
        idStr = sp.getString("id_stranka", null);
        idPredracun = sp.getString("id_predracun", null);

        Toast.makeText(getBaseContext(), idUser, Toast.LENGTH_SHORT).show();
        Toast.makeText(getBaseContext(), idStr, Toast.LENGTH_SHORT).show();
        Toast.makeText(getBaseContext(), idPredracun, Toast.LENGTH_SHORT).show();

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://r4c.sc-nm.si/armin/android_connect/izpisIzdelkovVTabelo.php");


        try {
                HttpResponse response = httpClient.execute(httpPost);
                httpPost.setHeader("Content-type", "application/json");
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    InputStream instream = entity.getContent();
                    String result = convertStreamToString(instream);
                    JSONArray arr = new JSONArray(result);
                    JSONObject jObj = arr.getJSONObject(0);


                    for(int i = 0; i < arr.length(); i++){
                        list.add(arr.getJSONObject(i).getString("naziv"));
                    }
                }

            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }catch(ClientProtocolException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            } catch (JSONException e) {
            e.printStackTrace();
        }

        initialize();

        actv=(AutoCompleteTextView)findViewById(R.id.autoC);
        ArrayAdapter<String> ard = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list);

        actv.setAdapter(ard);

    }

    public void initialize(){
        Button dodaj = (Button)findViewById(R.id.btnPotrdiElement); //deklaracija knofa za dodajanja
        dodaj.setOnClickListener(this);

        etAutoC = (EditText)findViewById(R.id.autoC);
        etKolicina = (EditText)findViewById(R.id.kolicina);
        etCena = (EditText)findViewById(R.id.etCenaLV);

        //tl = (TableLayout)findViewById(R.id.dodaniArtikli);
    }
    public void napolniTabeloZDodanimiArtikli() { //TU BOMO RABIL PR IZPISU

        //TableLayout tl = (TableLayout)findViewById(R.id.dodaniArtikli);
        TableRow tr1 = new TableRow(this);


        /*
        TableLayout tl = (TableLayout)findViewById(R.id.dodaniArtikli);
        TableRow tr1 = new TableRow(this);
        tr1.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TextView textview = new TextView(this);
        textview.setText("Krompir");
        textview.setTextColor(Color.BLACK);
        tr1.addView(textview);
        tl.addView(tr1);*/
    }

    private void posodobiDodaneElemente(){


    }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sestavljanje, menu);
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


    public void osveziNarocila(){
        HttpClient httpClient1 = new DefaultHttpClient();
        HttpPost httpPost1 = new HttpPost("http://r4c.sc-nm.si/armin/android_connect/izpisNarocilVecTabel.php");

        try{
        List<NameValuePair> nameValuePairs= new ArrayList<NameValuePair>(1);

        nameValuePairs.add(new BasicNameValuePair("id_predracun", idPredracun));

        httpPost1.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        HttpResponse response =  httpClient1.execute(httpPost1);
        httpPost1.setHeader("Content-type", "application/json");

        HttpEntity entity1 = response.getEntity();

        if(entity1 != null)
        {
            InputStream instream1 = entity1.getContent();

            String result = convertStreamToString(instream1);
            JSONArray arr1 = new JSONArray(result);
            String ime;
            String kolicina;

            for(int i = 0, size = arr1.length(); i<size; i++){
                JSONObject object = arr1.getJSONObject(i);

                ime = String.valueOf(object.get("naziv"));
                kolicina = String.valueOf(object.get("kolicina"));
                Toast.makeText(getBaseContext(), ime + " " + kolicina, Toast.LENGTH_SHORT).show();
            }

        }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onClick(View v) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://r4c.sc-nm.si/armin/android_connect/dodajElement.php");

        String element = etAutoC.getText().toString();
        String kolicina = etKolicina.getText().toString();
        String cena = etCena.getText().toString();

        if(etAutoC.getText().toString().equals("") ||etKolicina.getText().toString().equals("") || etCena.getText().toString().equals(""))
        {
            Toast.makeText(getBaseContext(), "Vnesite vsa polja", Toast.LENGTH_SHORT).show();
        }
        else{
            try {
                List<NameValuePair> nameValuePairs= new ArrayList<NameValuePair>(4);

                nameValuePairs.add(new BasicNameValuePair("id_predracun", idPredracun));
                nameValuePairs.add(new BasicNameValuePair("element", element));
                nameValuePairs.add(new BasicNameValuePair("kolicina", kolicina));
                nameValuePairs.add(new BasicNameValuePair("cena", cena));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response =  httpClient.execute(httpPost);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        osveziNarocila();

    }
}
