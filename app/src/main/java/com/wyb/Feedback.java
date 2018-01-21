package com.wyb;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Feedback extends AppCompatActivity {
    EditText e;
    Button b;
    //String email,feedback;
    TextView t;
    SharedPreferences preferences;
    String url = "http://192.168.43.37/wyb/feedback.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.feedback);
        preferences = getSharedPreferences("MyPref", MODE_PRIVATE);

        t=(TextView)findViewById(R.id.textView);
        e=(EditText)findViewById(R.id.feedback);
        b=(Button)findViewById(R.id.send);



        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name=preferences.getString("LoggedIn","");
                final String email=preferences.getString("Email","");
                final String feedback= e.getText().toString();

                RequestQueue queue = Volley.newRequestQueue(Feedback.this);
                StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Toast.makeText(Feedback.this, response, Toast.LENGTH_SHORT).show();
                        Log.i("My success",""+response);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(Feedback.this, "my error :"+error, Toast.LENGTH_LONG).show();
                        Log.i("My error",""+error);
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        Map<String,String> map = new HashMap<String, String>();
                        map.put("name",name);
                        map.put("email",email);
                        map.put("feedback",feedback);


                        return map;
                    }
                };
                queue.add(request);

            }
        });


//        b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e("email",body);
//                Intent intent = new Intent(Intent.ACTION_SENDTO);
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_SUBJECT, subject);
//                intent.putExtra(Intent.EXTRA_TEXT, body);
//                intent.setData(Uri.parse("mailto:vibhavgoel97@gmail.com"));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
//                startActivity(intent);
//            }
//        });
    }
}
