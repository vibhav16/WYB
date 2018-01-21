package com.wyb;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

import fragment.HomeFragment;
import fragment.MenuFragment;
import fragment.ProfileFragment;
import other.CircleTransform;

public class MainActivity extends AppCompatActivity{

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private static final String urlNavHeaderBg = "http://api.androidhive.info/images/nav-menu-header-bg.jpg";
    // index to identify current nav menu item
    public static int navItemIndex = 0;

    private static final String TAG_HOME = "home";
    private static final String TAG_MENU = "menu";
    private static final String TAG_PROFILE = "profile";
    public static String CURRENT_TAG = TAG_HOME;
    GoogleApiClient mGoogleApiClient;
    ProgressDialog pd;
    DrawerLayout drawerLayout;
String place,cuisine;
    Bundle bundle;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    private static final int RC_SIGN_IN = 9001;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;
    SharedPreferences preferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_main);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
         bundle=getIntent().getExtras();
        place=bundle.getString("place");
        cuisine=bundle.getString("cuisine");

        if (!isNetworkAvailable()) {
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Please connect to the internet and try again");
            pd.setCanceledOnTouchOutside(false);
            pd.show();

        } else {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            mHandler = new Handler();
            preferences = getSharedPreferences("MyPref", MODE_PRIVATE);


            Log.e("GOOGLE", preferences.getString("LoggedIn", ""));

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            navigationView = (NavigationView) findViewById(R.id.nav_view);
            fab = (FloatingActionButton) findViewById(R.id.fab);

            // Navigation view header
            navHeader = navigationView.getHeaderView(0);
            txtName = (TextView) navHeader.findViewById(R.id.name);
            txtWebsite = (TextView) navHeader.findViewById(R.id.website);
            imgNavHeaderBg = (ImageView) navHeader.findViewById(R.id.img_header_bg);
            imgProfile = (ImageView) navHeader.findViewById(R.id.img_profile);

            // load toolbar titles from string resources
            activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                 Intent intent=new Intent(MainActivity.this,MapsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);


                }
            });

            // load nav menu header data
            loadNavHeader();

            // initializing navigation menu
            setUpNavigationView();

            if (savedInstanceState == null) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
            }
        }
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader() {
        // name, website
        txtName.setText(preferences.getString("LoggedIn",""));
        txtWebsite.setText(preferences.getString("Email",""));

        // loading header background image
        Glide.with(this).load(urlNavHeaderBg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(preferences.getString("Photo",""))
                .crossFade()
                .thumbnail(0.3f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        // showing dot next to notifications label
        navigationView.getMenu().getItem(3).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();

            // show or hide the fab button
            toggleFab();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // show or hide the fab button
        toggleFab();

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);
                return homeFragment;
            case 1:
                // menu
                MenuFragment menuFragment = new MenuFragment();
                return menuFragment;

            case 2:
                // profile

            ProfileFragment profileFragment = new ProfileFragment();
            return profileFragment;

            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle("WYB");
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.menu:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_MENU;
                        break;
                    case R.id.profile:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_PROFILE;
                        break;
                    case R.id.logout:
                        {
                            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestEmail()
                                    .build();
                            mGoogleApiClient=new GoogleApiClient.Builder(MainActivity.this)
                                    .enableAutoManage(MainActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                                        @Override
                                        public void onConnectionFailed(ConnectionResult connectionResult) {
                                            Log.d("Main Activity", "onConnectionFailed:" + connectionResult);
                                        }
                                    })
                                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                                    .addApi(Plus.API)
                                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                                        @Override
                                        public void onConnected(Bundle bundle) {
                                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                                    new ResultCallback<Status>() {
                                                        @Override
                                                        public void onResult(Status status) {
                                                            // [START_EXCLUDE]
//                        updateUI(false);
                                                            if(status.isSuccess())
                                                            {
                                                                Intent intent=new Intent(MainActivity.this,Logingoogle.class);
                                                                startActivity(intent);
                                                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                                                finish();
                                                                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                                            }
                                                            // [END_EXCLUDE]
                                                        }
                                                    });
                                        }

                                        @Override
                                        public void onConnectionSuspended(int i) {

                                        }
                                    })
                                    .build();
                            mGoogleApiClient.connect();
                            preferences.edit().clear().commit();

                        }
                    drawer.closeDrawers();
                    return true;

                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        return true;
                    case R.id.feedback:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, Feedback.class));

                        drawer.closeDrawers();
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                        return true;
                    case R.id.rate:
                        // launch new intent instead of loading fragment
                        drawer.closeDrawers();
                        Snackbar snackbar = Snackbar
                                .make(drawerLayout, "Coming soon on Play Store, Stay tuned...", Snackbar.LENGTH_LONG);

                        snackbar.show();

                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }


        if (shouldLoadHomeFragOnBackPress) {

            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (navItemIndex == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        }

        if (navItemIndex == 3) {
            getMenuInflater().inflate(R.menu.notifications, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.maps) {
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=restaurants");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        if(id==R.id.search)
        {
            Intent intent=new Intent(MainActivity.this,User.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        if (id==R.id.settings) {
            Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
        if (id==R.id.help) {
//            Intent intent=new Intent(MainActivity.this,Help.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            Snackbar snackbar = Snackbar
                    .make(drawerLayout, "We will contact you soon...", Snackbar.LENGTH_LONG);

            snackbar.show();

        }



        return super.onOptionsItemSelected(item);
    }


    private void toggleFab() {
        if (navItemIndex == 0)
            fab.show();
        else
            fab.hide();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
