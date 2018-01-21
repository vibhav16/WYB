package fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wyb.R;
import com.wyb.Restaurant;
import com.wyb.RestaurantsAdapter;
import com.wyb.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeFragment extends Fragment {
    private static final String TAG = HomeFragment.class.getSimpleName();
    private List<Restaurant> restaurants;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayout;
    private RestaurantsAdapter adapter;
    User user;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String place,cuisine;

    private Integer THRESHOLD = 2;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        place=getArguments().getString("place");
        cuisine=getArguments().getString("cuisine");
        //Toast.makeText(getContext(),place,Toast.LENGTH_SHORT).show();
        rootView.setTag(TAG);
        recyclerView=(RecyclerView)rootView.findViewById(R.id.recyclerview);
        restaurants = new ArrayList<>();
        mSwipeRefreshLayout=(SwipeRefreshLayout)rootView.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                restaurants.clear();
                getRestaurantsFromDB(0);


            }
        });
        getRestaurantsFromDB(0);
        gridLayout = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayout);

        adapter = new RestaurantsAdapter(getContext(), restaurants);
        recyclerView.setAdapter(adapter);

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//
//                if (gridLayout.findLastCompletelyVisibleItemPosition() == restaurants.size() - 1) {
//                    getRestaurantsFromDB(restaurants.get(restaurants.size() - 1).getId());
//                }
//
//            }
//        });


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void getRestaurantsFromDB(int id) {


        AsyncTask<Integer, Void, Void> asyncTask = new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... restaurantIds) {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://192.168.43.37/wyb/bash1.php?location="+place+"&cuisine="+cuisine)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    Log.e("RESPONSE",response.toString());
                    JSONArray array = new JSONArray(response.body().string());

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);

                        Restaurant restaurant = new Restaurant(object.getInt("res_id"), object.getString("res_name"),
                                object.getString("res_image"), object.getString("addr"));

                        HomeFragment.this.restaurants.add(restaurant);
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                adapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        };

        asyncTask.execute(id);
    }
}
