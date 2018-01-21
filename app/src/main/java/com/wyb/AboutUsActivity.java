package com.wyb;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class AboutUsActivity extends AppCompatActivity {
    private List<Item> itemList = new ArrayList<>();
    private RecyclerView recyclerview;
    private MyAdapter mAdapter;
    ProgressDialog pd;
    private GridLayoutManager gridLayout;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_about_us);
        if (!isNetworkAvailable()) {
            pd = new ProgressDialog(AboutUsActivity.this);
            pd.setMessage(getText(R.string.progress_dialog));
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        } else {

            toolbar = (Toolbar) findViewById(R.id.toolbar_about);
            setSupportActionBar(toolbar);
            recyclerview=(RecyclerView)findViewById(R.id.recycler_view);


            gridLayout = new GridLayoutManager(this, 2);
            recyclerview.setLayoutManager(gridLayout);


            mAdapter = new MyAdapter(itemList);
            recyclerview.setAdapter(mAdapter);

            prepareItem();

        }
    }
    private void prepareItem() {
        Item item = new Item(R.drawable.wyb,"Vibhav","Facebook");
        itemList.add(item);
        item = new Item(R.drawable.wyb,"Devrishee","Google");
        itemList.add(item);
        mAdapter.notifyDataSetChanged();
        recyclerview.setAdapter(mAdapter);
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

