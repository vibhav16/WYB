package com.wyb;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User extends AppCompatActivity {
    GoogleApiClient mGoogleApiClient;
    public EditText e1,e2,e3;
    public Button b1;
    ImageView imageView;
    MaterialSpinner people;
    ProgressDialog pd;
    ScrollView scrollView;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    public SimpleCursorAdapter myAdapter;

    public SearchView searchView = null;
    public String[] strArrData = {"No Suggestions"};

     @Override
    protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         overridePendingTransition(R.anim.fadein,R.anim.fadeout);
         setContentView(R.layout.activity_user);
         preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
         if (!isNetworkAvailable()) {
             pd = new ProgressDialog(User.this);
             pd.setMessage("Please connect to the internet and try again");
             pd.setCanceledOnTouchOutside(false);
             pd.show();
         } else {
             getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

             final String[] from = new String[] {"locationName"};
             final int[] to = new int[] {android.R.id.text1};

             // setup SimpleCursorAdapter
             myAdapter = new SimpleCursorAdapter(User.this, android.R.layout.simple_spinner_dropdown_item, null, from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

             // Fetch data from mysql table using AsyncTask
             new AsyncFetch().execute();



             Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_user);
             setSupportActionBar(toolbar);


             e2 = (EditText) findViewById(R.id.budget);
             e3 = (EditText) findViewById(R.id.cuisine);

             people = (MaterialSpinner) findViewById(R.id.people);
             people.setItems("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Greater than 10");
             people.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

                 @Override
                 public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                     Snackbar.make(view, "No. of people selected: " + item, Snackbar.LENGTH_SHORT).show();
                 }
             });
             b1 = (Button) findViewById(R.id.proceed);
             b1.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     if (e2.getText().toString().isEmpty()) {
                         e2.setError("Please fill the budget");
                         return;
                     }
                     if (searchView.getQuery().toString().isEmpty())
                     {
                         return;
                     }
                      else  {
                         editor = preferences.edit();

                         editor.putString("Place",searchView.getQuery().toString());
                         editor.putString("Budget",e2.getText().toString());
                         editor.putString("People",people.getText().toString());
                         editor.commit();
                         Log.d("BUDGET",e2.getText().toString());
                         Log.d("People",people.getText().toString());

                         final String budget=e2.getText().toString();
                         final String peoples=people.getText().toString();
                         final String res_name=searchView.getQuery().toString();

                         RequestQueue queue = Volley.newRequestQueue(User.this);
                         StringRequest request = new StringRequest(Request.Method.POST, "http://192.168.43.37/wyb/zom.php", new Response.Listener<String>() {
                             @Override
                             public void onResponse(String response) {

                                 //Toast.makeText(User.this, response, Toast.LENGTH_SHORT).show();
                                 Log.i("My success",""+response);

                             }
                         }, new Response.ErrorListener() {
                             @Override
                             public void onErrorResponse(VolleyError error) {

                                 Toast.makeText(User.this, "my error :"+error, Toast.LENGTH_LONG).show();
                                 Log.i("My error",""+error);
                             }
                         }){
                             @Override
                             protected Map<String, String> getParams() throws AuthFailureError {

                                 Map<String,String> map = new HashMap<String, String>();
                                 map.put("budget",budget);
                                 map.put("people",peoples);
                                 map.put("res_name",res_name);


                                 return map;
                             }
                         };
                         queue.add(request);
                         Intent intent = new Intent(User.this, MainActivity.class);
                         intent.putExtra("place",searchView.getQuery().toString());
                         intent.putExtra("cuisine",e3.getText().toString());
                         startActivity(intent);

                         overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                     }
                 }
             });

         }
     }

    public SearchView getSearchView() {
        return searchView;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void onBackPressed(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // adds item to action bar
        getMenuInflater().inflate(R.menu.search_main, menu);

        // Get Search item from action bar and Get Search service
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) User.this.getSystemService(Context.SEARCH_SERVICE);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            //searchView.setSearchableInfo(searchManager.getSearchableInfo(User.this.getComponentName()));
            searchView.setQueryHint("Enter City");
            searchView.setIconified(false);
            searchView.setSuggestionsAdapter(myAdapter);
            // Getting selected (clicked) item suggestion
            searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
                @Override
                public boolean onSuggestionClick(int position) {

                    // Add clicked text to search box
                    CursorAdapter ca = searchView.getSuggestionsAdapter();
                    Cursor cursor = ca.getCursor();
                    cursor.moveToPosition(position);
                    searchView.setQuery(cursor.getString(cursor.getColumnIndex("locationName")),false);
                    return true;
                }

                @Override
                public boolean onSuggestionSelect(int position) {
                    return false;
                }
            });
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

                    if (e2.getText().toString().isEmpty()) {
                        e2.setError("Please fill the budget");

                    }
                    else  {
                        Intent intent = new Intent(User.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {

                    // Filter data
                    final MatrixCursor mc = new MatrixCursor(new String[]{ BaseColumns._ID, "locationName" });
                    for (int i=0; i<strArrData.length; i++) {
                        if (strArrData[i].toLowerCase().startsWith(s.toLowerCase()))
                            mc.addRow(new Object[] {i, strArrData[i]});
                    }
                    myAdapter.changeCursor(mc);
                    return false;
                }
            });
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    // Every time when you press search button on keypad an Activity is recreated which in turn calls this function
    @Override
    protected void onNewIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (searchView != null) {
                searchView.clearFocus();

            }


            // User entered text and pressed search button. Perform task ex: fetching data from database and display

        }
    }

    // Create class AsyncFetch
    private class AsyncFetch extends AsyncTask<String, String, String> {

        ProgressDialog pdLoading = new ProgressDialog(User.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tPlease Wait Loading...");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides or your JSON file address
                url = new URL("http://192.168.43.37/wyb/loc.php");

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");

                // setDoOutput to true as we receive data
                conn.setDoOutput(true);
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {
                    return("Connection error");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread
            ArrayList<String> dataList = new ArrayList<String>();
            pdLoading.dismiss();


            if(result.equals("no rows")) {

                // Do some action if no data from database

            }else{

                try {

                    JSONArray jArray = new JSONArray(result);

                    // Extract data from json and store into ArrayList
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json_data = jArray.getJSONObject(i);
                        dataList.add(json_data.getString("location"));
                    }

                    strArrData = dataList.toArray(new String[dataList.size()]);

                } catch (JSONException e) {
                    // You to understand what actually error is and handle it appropriately
                    Toast.makeText(User.this, e.toString(), Toast.LENGTH_LONG).show();
                    Toast.makeText(User.this, result.toString(), Toast.LENGTH_LONG).show();
                }

            }

        }

    }
}


