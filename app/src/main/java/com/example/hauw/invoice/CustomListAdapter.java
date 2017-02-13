package com.example.hauw.invoice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
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

/**
 * Created by MSI on 1.4.2015.
 */
public class CustomListAdapter extends ArrayAdapter < Narocila > {
    private Context appContext = null;
    private ArrayList<Narocila> items = null;

    public CustomListAdapter(Context context, int textViewResourceId, ArrayList <Narocila> items) {
        super(context, textViewResourceId, items);
        this.appContext = context;
        this.items = items;
    }

    @
            Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_item, null);
        }
        Narocila o = items.get(position);
        if (o != null) {
            TextView element= (TextView) v.findViewById(R.id.listEtElement);
            TextView kolicina = (TextView) v.findViewById(R.id.listEtKolicina);
            TextView cena = (TextView) v.findViewById(R.id.listEtCena);

            final ImageButton btnOdstrani = (ImageButton) v.findViewById(R.id.btnOdstraniElement);
            final TextView listId = (TextView)v.findViewById(R.id.listId);

            final String idNarocila = listId.getText().toString();
            btnOdstrani.setTag(position);

            btnOdstrani.setOnClickListener(new View.OnClickListener() {

                @
                        Override
                public void onClick(View view) {
                    //Toast.makeText(appContext, "Hello this is clicked",Toast.LENGTH_LONG).show();
                    String pos = btnOdstrani.getTag().toString();
                    int _position = Integer.parseInt(pos);
                    items.remove(_position);
                    notifyDataSetChanged();
                    brisiNarociloIzBaze((String) listId.getText().toString());
                    Toast.makeText(getContext(), btnOdstrani.getTag().toString(), Toast.LENGTH_SHORT).show();
                }
            });

            if (element != null) {
                element.setText(o.GetIzdelek());
            }
            if (kolicina != null) {
                kolicina.setText(o.GetKolicina());
            }
            if (cena != null) {
                cena.setText(o.GetCena());
            }
            if (idNarocila != null) {
                listId.setText(o.GetIdNarocilo());
            }
        }
        return v;
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

    public void brisiNarociloIzBaze(String idnarocilo)
    {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://r4c.sc-nm.si/armin/android_connect/odstraniNarocilo.php");


        try {
            List<NameValuePair> nameValuePairs= new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("idNarocilo", idnarocilo));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                Toast.makeText(getContext(), "brisan", Toast.LENGTH_SHORT).show();
                InputStream instream = entity.getContent();
                String resultNarocilo = convertStreamToString(instream);
            }

        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }catch(ClientProtocolException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
