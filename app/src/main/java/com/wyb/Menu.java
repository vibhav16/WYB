package com.wyb;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Menu extends AppCompatActivity {

    private TextView mTextMessage,t1,t2,t3,tc,fourth,t6,t7,t8;
    private ArrayList<UserMenu> menus;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayout;
    private MenuAdapter adapter;
    String f,budget,people;
    OnGetResponse ogr;
    TextView food,cost;
    UserMenu u,um,um1,um2,um3,um4,um5,um6,um7,um8;
    SharedPreferences preferences;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(um.getFood());
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        preferences = getSharedPreferences("MyPref", MODE_PRIVATE);
        budget=preferences.getString("Budget","");
        people=preferences.getString("People","");
        Log.d("BUDGET", preferences.getString("Budget", ""));
        mTextMessage = (TextView) findViewById(R.id.menu);
        t1=(TextView)findViewById(R.id.menu1);
        t2=(TextView)findViewById(R.id.menu2);
        t3=(TextView)findViewById(R.id.menu3);
        tc=(TextView)findViewById(R.id.totalcost);
        fourth=(TextView)findViewById(R.id.fourth);
        t6=(TextView)findViewById(R.id.sixth);
        t7=(TextView)findViewById(R.id.seventh);
        t8=(TextView)findViewById(R.id.eight);

        //recyclerView=(RecyclerView)findViewById(R.id.recyclerview_menu);
                menus=new ArrayList<>();

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                "http://192.168.43.37/wyb/menu.php?budget="+budget+"&people="+people,

                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject Object = null;
                            try {
                                Object = response.getJSONObject(i);

                                Log.d("fooood", "onResponse: " + Object.get("food"));
                                Log.d("RESPONSE",response.toString());
                                menus.add(new UserMenu(Object.getString("res_name"), Object.getString("food"),Object.getInt("cost"),Object.getInt("budget"),Object.getInt("totalcost"),Object.getInt("people")));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        ogr.onSuccess(menus);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("yahahu", "onErrorResponse: " + error.toString());
                        Log.d("yahahu", "onErrorResponse: " + error.getMessage());
                    }
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(Menu.this);
        requestQueue.add(jsonArrayRequest);
        ogr=new OnGetResponse() {
            @Override
            public void onSuccess(ArrayList<UserMenu> menus) {

                StringBuilder builder=new StringBuilder();
                for(int i=0;i<menus.size();i++)
                {
                    um=menus.get(i);
                    builder.append(um.getFood()+"->"+um.getCost()+"\n");
                    tc.setText("Total Cost: "+um.getTotalcost());
                }
                mTextMessage.setText(builder.toString());

            }
        };

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
