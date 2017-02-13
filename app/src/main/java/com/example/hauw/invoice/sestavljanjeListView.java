package com.example.hauw.invoice;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.StrictMode;
import android.os.Bundle;
import android.renderscript.Element;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;


public class sestavljanjeListView extends ListActivity{

    SimpleAdapter adpt = null;
    ArrayList<Narocila> newNarocila;
    AutoCompleteTextView txtElement;
    EditText txtKolicina;
    EditText txtCena;
    Button btnShrani;
    Button btnPrikazi;
    TextView tvIdNarocila;
    CustomListAdapter newAdakter;
    String idUser;
    String idStr;
    String idPredracun;
    String retIdNarocilo;
    BaseFont bf;
    public static File fontFile = new File("fonts/DroidSans.ttf");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sestavljanje_list_view);
        StrictMode.enableDefaults();

        final List<String> list = new ArrayList<String>();
        newNarocila = new ArrayList<>();
        newAdakter = new CustomListAdapter(this, R.layout.list_item, newNarocila);
        setListAdapter(this.newAdakter);


        txtElement = (AutoCompleteTextView) findViewById(R.id.etElementLV);
        txtKolicina = (EditText) findViewById(R.id.etKolicinaLV);
        txtCena = (EditText) findViewById(R.id.etCenaLV);
        btnShrani = (Button) findViewById(R.id.btnShrani);
        btnPrikazi = (Button) findViewById(R.id.btnPrikazi);
        tvIdNarocila = (TextView) findViewById(R.id.idNarocilaBaza);

        final List<String> tabelaNarocilElement = new ArrayList<String>();
        final List<String> tabelaNarocilKolicina = new ArrayList<String>();
        final List<String> tabelaNarocilCena = new ArrayList<String>();

        SharedPreferences sp = getSharedPreferences("MyPrefs", 0);
        idUser = sp.getString("id_user", null);
        idStr = sp.getString("id_stranka", null);
        idPredracun = sp.getString("id_predracun", null);

        HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost("http://r4c.sc-nm.si/armin/android_connect/izpisIzdelkovVTabelo.php");

        dodajCeno();

        try {
            HttpResponse response = httpClient.execute(httpPost);
            httpPost.setHeader("Content-type", "application/json");
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                String result = convertStreamToString(instream);
                JSONArray arr = new JSONArray(result);
                JSONObject jObj = arr.getJSONObject(0);


                for (int i = 0; i < arr.length(); i++) {
                    list.add(arr.getJSONObject(i).getString("naziv"));
                }
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ArrayAdapter<String> ard = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list);

        txtElement.setAdapter(ard);

        btnShrani.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (txtElement.getText().toString().equals("") || txtElement.getText().toString().equals("Element") || txtKolicina.getText().toString().equals("") || txtKolicina.getText().toString().equals("Kolicina") || txtCena.getText().toString().equals("") || txtCena.getText().toString().equals("Cena"))
                    Toast.makeText(getBaseContext(), "Vnesite vsa polja", Toast.LENGTH_SHORT).show();
                else {
                    dodajElementVBazo();
                    String id = getIdNarocila();
                    tvIdNarocila.setText(id);
                    newNarocila = new ArrayList<>();
                    Narocila narocilo = new Narocila();
                    narocilo.SetIzdelek(txtElement.getText().toString());
                    narocilo.SetKolicina(txtKolicina.getText().toString());
                    narocilo.SetCena(txtCena.getText().toString());
                    narocilo.SetIdNarocilo(tvIdNarocila.getText().toString());

                    //Toast.makeText(getBaseContext(), getIdNarocila(), Toast.LENGTH_SHORT).show();
                    newNarocila.add(narocilo);
                    if (newNarocila.size() > 0) {
                        newAdakter.notifyDataSetChanged();
                        newAdakter.add(newNarocila.get(0));
                    }
                    newAdakter.notifyDataSetChanged();
                }

                txtElement.setText("");
                txtKolicina.setText("");
                txtCena.setText("");
                //tvIdNarocila.setText("");
                txtCena.clearFocus();
            }
        });


        btnPrikazi.setOnClickListener(new View.OnClickListener() {//SESTAVLJANJE PDFJA
            @Override
            public void onClick(View v) {
                Document document = new Document();


                HttpClient httpClientPDF = new DefaultHttpClient();
                HttpClient httpClientPDFGlava = new DefaultHttpClient();
                HttpClient httpClientCena = new DefaultHttpClient();
                HttpPost httpPostPDF = new HttpPost("http://r4c.sc-nm.si/armin/android_connect/narocilaVPDF.php");
                HttpPost httpPostPDFGlava = new HttpPost("http://r4c.sc-nm.si/armin/android_connect/glavaVPDF.php");
                HttpPost httpPostCena = new HttpPost("http://r4c.sc-nm.si/armin/android_connect/cenaSkupaj.php");


                try {
                    Font font = new Font(BaseFont.createFont("/system/fonts/DroidSans.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED));

                    BaseFont baseFontBold = BaseFont.createFont("/system/fonts/DroidSans.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                    Font fontNaslov = new Font(baseFontBold, 19, Font.BOLD);
                    Font fontBold = new Font(baseFontBold, 12, Font.BOLD);

                    //ZA NAROČILA IN CENO
                    List<NameValuePair> nameValuePairsPDF = new ArrayList<NameValuePair>(3);
                    List<NameValuePair> nameValuePairsCena = new ArrayList<NameValuePair>(1);
                    nameValuePairsPDF.add(new BasicNameValuePair("idpredracunPDF", idPredracun));
                    nameValuePairsCena.add(new BasicNameValuePair("idpredracunCena", idPredracun));

                    httpPostPDF.setEntity(new UrlEncodedFormEntity(nameValuePairsPDF, HTTP.UTF_8));
                    httpPostCena.setEntity(new UrlEncodedFormEntity(nameValuePairsCena, HTTP.UTF_8));

                    HttpResponse responsePDF = httpClientPDF.execute(httpPostPDF);
                    HttpResponse responseCena = httpClientCena.execute(httpPostCena);
                    httpPostCena.setHeader("Content-type", "application/json");
                    httpPostPDF.setHeader("Content-type", "application/json");
                    HttpEntity entityPDF = responsePDF.getEntity();
                    HttpEntity entityCena = responseCena.getEntity();

                    //ZA GLAVO PREDRAČUNA
                    List<NameValuePair> nameValuePairsPDFGlava =  new ArrayList<NameValuePair>(2);
                    nameValuePairsPDFGlava.add(new BasicNameValuePair("idpredracunPDF", idPredracun));

                    httpPostPDFGlava.setEntity(new UrlEncodedFormEntity(nameValuePairsPDFGlava, HTTP.UTF_8));

                    HttpResponse responsePDFGlava = httpClientPDFGlava.execute(httpPostPDFGlava);
                    httpPostPDFGlava.setHeader("Content-type", "application/json");
                    HttpEntity entityPDFGlava = responsePDFGlava.getEntity();

                    if (entityPDF != null) {
                        //ZA NAROCILA V JSON ARRAY
                        InputStream instreamPDF = entityPDF.getContent();
                        String resultPDF = convertStreamToString(instreamPDF);
                        JSONArray arrPDF = new JSONArray(resultPDF);
                        //JSONObject jObjPDF = arrPDF.getJSONObject(0);

                        //ZA GLAVO PREDRACUNA V JSON ARRAY
                        InputStream instreamPDFGlava = entityPDFGlava.getContent();
                        String resultPDFGlava = convertStreamToString(instreamPDFGlava);
                        JSONArray arrPDFGlava = new JSONArray(resultPDFGlava);
                        JSONObject objGlava = arrPDFGlava.getJSONObject(0);

                        String imedokumenta = objGlava.getString("p_koda");

                        PdfWriter.getInstance(document, new FileOutputStream(getFilesDir().getAbsolutePath() +"/" + imedokumenta));

                        document.open();
                        float[] columnWidths = {1.5f, 0.5f, 0.5f, 0.5f, 0.5f};
                        PdfPTable table = new PdfPTable(columnWidths);

                        Paragraph paragraph = new Paragraph();
                        paragraph.setFont(font);
                        Paragraph glava = new Paragraph();
                        glava.setFont(font);
                        Paragraph glavaNaslovnik = new Paragraph();
                        glavaNaslovnik.setFont(font);
                        Paragraph naslov = new Paragraph();
                        naslov.setFont(fontNaslov);
                        Paragraph odgovornaOseba = new Paragraph();
                        odgovornaOseba.setFont(font);
                        Paragraph skupajCena = new Paragraph();
                        skupajCena.setFont(font);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                        String datum = sdf.format(new Date());


                        //GLAVA DOKUMENTA

                        glava.add(objGlava.getString("p_ime").toString().toUpperCase());
                        glava.add(Chunk.NEWLINE);
                        glava.add(objGlava.getString("p_sedez").toString());
                        glava.add(Chunk.NEWLINE);
                        glava.add(objGlava.getString("p_postnaSt").toString() + " ");
                        glava.add(objGlava.getString("p_posta").toString());
                        glava.add(Chunk.NEWLINE);
                        glava.add("Davčna številka: " + objGlava.getString("p_davcna").toString());
                        glava.add(Chunk.NEWLINE);
                        glava.add(Chunk.NEWLINE);
                        glava.add("Datum izdelave: " + datum);
                        glava.add(Chunk.NEWLINE);
                        glava.add(Chunk.NEWLINE);
                        glava.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                        document.add(glava);

                        glavaNaslovnik.add(objGlava.getString("s_ime").toString().toUpperCase());
                        glavaNaslovnik.add(" "+ objGlava.getString("s_priimek").toString().toUpperCase());
                        glavaNaslovnik.add(Chunk.NEWLINE);
                        glavaNaslovnik.add(objGlava.getString("s_naslov").toString());
                        glavaNaslovnik.add(" "+objGlava.getString("s_hisnaSt").toString());
                        glavaNaslovnik.add(Chunk.NEWLINE);
                        glavaNaslovnik.add(objGlava.getString("s_postnaSt").toString());
                        glavaNaslovnik.add(" " + objGlava.getString("s_posta").toString());
                        glavaNaslovnik.add(Chunk.NEWLINE);
                        glavaNaslovnik.add(Chunk.NEWLINE);
                        document.add(glavaNaslovnik);

                        naslov.add(Chunk.NEWLINE);

                        naslov.add("PREDRAČUN");
                        naslov.add(Chunk.NEWLINE);
                        naslov.add(Chunk.NEWLINE);
                        naslov.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                        document.add(naslov);


                        //GLAVA TABELE
                        insertCell(table, "Naziv", com.itextpdf.text.Element.ALIGN_LEFT, 1, fontBold);
                        insertCell(table, "EM", com.itextpdf.text.Element.ALIGN_LEFT, 1, fontBold);
                        insertCell(table, "Kolicina", com.itextpdf.text.Element.ALIGN_LEFT, 1, fontBold);
                        insertCell(table, "Cena brez DDV", com.itextpdf.text.Element.ALIGN_LEFT, 1, fontBold);
                        insertCell(table, "Cena brez DDV", com.itextpdf.text.Element.ALIGN_LEFT, 1, fontBold);

                        for (int i = 0; i < arrPDF.length(); i++) {
                            /*tabelaNarocilElement.add(arrPDF.getJSONObject(i).getString("naziv"));
                            tabelaNarocilKolicina.add(arrPDF.getJSONObject(i).getString("kolicina"));
                            tabelaNarocilCena.add(arrPDF.getJSONObject(i).getString("cena"));*/
                            double skupnaCena, prva, druga;
                            prva = Double.parseDouble(arrPDF.getJSONObject(i).get("kolicina").toString());
                            druga = Double.parseDouble(arrPDF.getJSONObject(i).get("cena").toString());
                            skupnaCena= prva*druga;

                            insertCell(table, arrPDF.getJSONObject(i).getString("naziv").toString(), com.itextpdf.text.Element.ALIGN_LEFT, 1, font);
                            insertCell(table, arrPDF.getJSONObject(i).getString("enota").toString(), com.itextpdf.text.Element.ALIGN_LEFT, 1, font);
                            insertCell(table, arrPDF.getJSONObject(i).getString("kolicina").toString(), com.itextpdf.text.Element.ALIGN_LEFT, 1, font);
                            insertCell(table, Double.toString(druga), com.itextpdf.text.Element.ALIGN_LEFT, 1, font);
                            insertCell(table, Double.toString(skupnaCena), com.itextpdf.text.Element.ALIGN_LEFT, 1, font);

                            //Paragraph pls = new Paragraph(arrPDF.getJSONObject(i).getString("kolicina") + " " + arrPDF.getJSONObject(i).getString("cena"));

                        }
                        paragraph.add(table);
                        document.add(paragraph);

                        InputStream instreamCena = entityCena.getContent();
                        String resultCena = convertStreamToString(instreamCena);
                        JSONArray arrCena = new JSONArray(resultCena);
                        JSONObject objCena = arrCena.getJSONObject(0);

                        skupajCena.add("SKUPAJ BREZ DDV = " + Double.parseDouble(arrCena.getJSONObject(0).getString("skupaj_narocilo").toString()));
                        skupajCena.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                        skupajCena.setIndentationRight(55);
                        skupajCena.add(Chunk.NEWLINE);
                        skupajCena.add(Chunk.NEWLINE);
                        skupajCena.add(Chunk.NEWLINE);
                        skupajCena.add(Chunk.NEWLINE);
                        document.add(skupajCena);

                        odgovornaOseba.add("Odgovorna oseba: " + objGlava.getString("u_ime").toString() + " " +  objGlava.getString("u_priimek").toString());
                        odgovornaOseba.add(Chunk.NEWLINE);
                        odgovornaOseba.add(Chunk.NEWLINE);
                        odgovornaOseba.add(Chunk.NEWLINE);
                        odgovornaOseba.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                        odgovornaOseba.setIndentationRight(55);
                        document.add(odgovornaOseba);
                        document.close();
                        nalozi(imedokumenta);

                        //http://docs.google.com/gview?embedded=true&url=<url of a supported doc>
                        String url1 = "http://r4c.sc-nm.si/armin/Invoicapp/datoteke/" + imedokumenta + ".pdf";
                        String url = "http://docs.google.com/gview?embedded=true&url=<"+ url1 + ">";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url1));
                        startActivity(i);
                        //Toast.makeText(getBaseContext(), retIdNarocilo, Toast.LENGTH_SHORT).show();
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                    /*KREIRANJE PDFJA
                    PdfWriter.getInstance(document, new FileOutputStream(getFilesDir().getAbsolutePath() + "/ime.pdf"));
                    Toast.makeText(getBaseContext(), getFilesDir().getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    document.open();
                    Paragraph pls = new Paragraph("ASKODMASDML");
                    document.add(pls);
                    document.close();*/
            }
        });
    }


    public void nalozi(String imedokumenta){


        FTPClient con = null;

        try
        {
            con = new FTPClient();
            con.connect("92.244.93.228");

            if (con.login("armin", "potatojuggler"))
            {
                con.enterLocalPassiveMode(); // important!
                con.setFileType(FTP.BINARY_FILE_TYPE);
                String data = getFilesDir().getAbsolutePath() +"/" + imedokumenta;

                FileInputStream in = new FileInputStream(new File(data));
                boolean result = con.storeFile("/Invoicapp/datoteke/" + imedokumenta + ".pdf", in);
                in.close();
                if (result) Log.v("upload result", "succeeded");
                con.logout();
                con.disconnect();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
    private void insertCell(PdfPTable table, String text, int align, int colspan, Font font){

        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        //set the cell alignment
        cell.setHorizontalAlignment(align);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        //in case there is no text and you wan to create an empty row
        if(text.trim().equalsIgnoreCase("")){
            cell.setMinimumHeight(10f);
        }
        //add the call to the table
        table.addCell(cell);

    }

    public String getIdNarocila()
    {
        String kolicina = txtKolicina.getText().toString();
        String naziv = txtElement.getText().toString();

        HttpClient httpClientNarocilo = new DefaultHttpClient();
        HttpPost httpPostNarocilo = new HttpPost("http://r4c.sc-nm.si/armin/android_connect/vrniIdNarocila.php");


        try {
            List<NameValuePair> nameValuePairsNarocilo= new ArrayList<NameValuePair>(3);
            nameValuePairsNarocilo.add(new BasicNameValuePair("idpredracunNarocilo",idPredracun));
            nameValuePairsNarocilo.add(new BasicNameValuePair("kolicinaNarocilo", kolicina));
            nameValuePairsNarocilo.add(new BasicNameValuePair("nazivNarocilo", naziv));

            httpPostNarocilo.setEntity(new UrlEncodedFormEntity(nameValuePairsNarocilo, HTTP.UTF_8));

            HttpResponse responseNarocilo = httpClientNarocilo.execute(httpPostNarocilo);
            httpPostNarocilo.setHeader("Content-type", "application/json");
            HttpEntity entityNarocilo = responseNarocilo.getEntity();
            if (entityNarocilo != null) {
                InputStream instreamNarocilo = entityNarocilo.getContent();
                String resultNarocilo = convertStreamToString(instreamNarocilo);
                JSONArray arrNarocilo = new JSONArray(resultNarocilo);
                JSONObject jObjNarocilo = arrNarocilo.getJSONObject(0);
                retIdNarocilo = jObjNarocilo.getString("id_narocilo");
                //Toast.makeText(getBaseContext(), retIdNarocilo, Toast.LENGTH_SHORT).show();
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
        return retIdNarocilo;
    }

    public void dodajCeno()
    {
        txtElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String element = txtElement.getText().toString();
                //Charset.forName("UTF-8").encode(element);
                HttpClient httpClientCena = new DefaultHttpClient();
                HttpPost httpPostCena = new HttpPost("http://r4c.sc-nm.si/armin/android_connect/vrniCeno.php");


                try {
                    List<NameValuePair> nameValuePairs= new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("element",element));

                    httpPostCena.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));

                    HttpResponse responseCena = httpClientCena.execute(httpPostCena);
                    httpPostCena.setHeader("Content-type", "application/json");
                    HttpEntity entity = responseCena.getEntity();

                    if (entity != null) {
                        InputStream instream = entity.getContent();
                        String result = convertStreamToString(instream);
                        JSONArray arr = new JSONArray(result);
                        JSONObject jObj = arr.getJSONObject(0);

                        String retCena = jObj.getString("cena");
                        txtCena.setText(retCena);
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
            }
        });
    }

    public void dodajElementVBazo(){
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://r4c.sc-nm.si/armin/android_connect/dodajElement.php");

        String element = txtElement.getText().toString();
        String kolicina = txtKolicina.getText().toString();
        String cena = txtCena.getText().toString();

        if(txtElement.getText().toString().equals("") || txtElement.getText().toString().equals("Element") || txtKolicina.getText().toString().equals("") || txtKolicina.getText().toString().equals("Kolicina") || txtCena.getText().toString().equals("") || txtCena.getText().toString().equals("Cena"))
        {
            //Toast.makeText(getBaseContext(), "Vnesite vsa polja", Toast.LENGTH_SHORT).show();
        }
        else{
            try {
                List<NameValuePair> nameValuePairs= new ArrayList<NameValuePair>(4);

                nameValuePairs.add(new BasicNameValuePair("id_predracun", idPredracun));
                nameValuePairs.add(new BasicNameValuePair("element", element));
                nameValuePairs.add(new BasicNameValuePair("kolicina", kolicina));
                nameValuePairs.add(new BasicNameValuePair("cena", cena));

                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
                HttpResponse response =  httpClient.execute(httpPost);


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sestavljanje_list_view, menu);
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

}
