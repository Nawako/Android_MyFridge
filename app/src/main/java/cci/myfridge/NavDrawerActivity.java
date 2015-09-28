package cci.myfridge;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import android.os.Handler;

import cci.myfridge.async.GetNotificationsTask;
import cci.myfridge.custom.classes.Constants;
import cci.myfridge.custom.classes.NavDrawerItemClass;
import cci.myfridge.custom.classes.ProductsClass;
import cci.myfridge.global.Globals;


public class NavDrawerActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private static Context mContext;
    private static Activity mActivity;
    private boolean backPress;
    Globals mGlobal;
    DrawerLayout mDrawerLayout;
    static ListView mDrawerList;
    public static DrawerCustomlistAdapter myadapter;
    static LayoutInflater inflater;
    private ActionBarDrawerToggle drawerlistener;
    Fragment frag;
    View slidingmenu;
    public static ProgressDialog dialog;
    static SharedPreferences sharedPreferences;
    Handler handler;
    public static ArrayList<NavDrawerItemClass> drawerList;
    public static ArrayList<ProductsClass> notificationList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// met l'activity en full screen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);// cache le keyboard

        setContentView(R.layout.nav_drawer_activity);
        Init();

        handler=new Handler();
        // set le temps de répétition à 2min
        handler.postDelayed(runnable, 120000);


        inflater=(LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        dialog=new ProgressDialog(mActivity);
        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.nav_drawer_list_header, mDrawerList, false);
        sharedPreferences=mActivity.getSharedPreferences("MyFridge",MODE_PRIVATE);
        String userFname=sharedPreferences.getString("first_name","");
        String userLname=sharedPreferences.getString("last_name","");
        String userMail=sharedPreferences.getString("email","");
        // get l'utilisateur connecté et set le header du menu
        TextView userNameTv=(TextView)header.findViewById(R.id.tv_header_username);
        TextView userDetailTv=(TextView)header.findViewById(R.id.tv_header_userdetail);
        ImageView userImg=(ImageView)header.findViewById(R.id.dpimg);
        if(!sharedPreferences.getString("photo","").equals(""))
        {
            Picasso.with(mActivity).load(Constants.mWebURL+ sharedPreferences.getString("photo", "") + "").into(userImg);
        }
        userNameTv.setText(""+userFname+" "+userLname+"");
        userDetailTv.setText(userMail);
        //Navigation drawer menu items list
        drawerList=new ArrayList<>();
        //Ajoute NavDrawerItemClass  à la drawerList
        drawerList.add(0,new NavDrawerItemClass(R.drawable.home,"Accueil",0));
        drawerList.add(1,new NavDrawerItemClass(R.drawable.setting,"Paramètres",0));
        drawerList.add(2,new NavDrawerItemClass(R.drawable.notification,"Notifications",0));
        drawerList.add(3,new NavDrawerItemClass(R.drawable.profile,"Profil",0));
        drawerList.add(4,new NavDrawerItemClass(R.drawable.logout,"Déconnexion",0));
        mDrawerList.addHeaderView(header, null, false);// ajoute l'en-tête à la liste
        myadapter = new DrawerCustomlistAdapter(mContext,drawerList); // passe la drawer list à l'adapter

        myadapter.notifyDataSetChanged();

        mDrawerList.setAdapter(myadapter);// set adapter au menu list


        mDrawerList.setOnItemClickListener(this);
        View actionbarView=inflater.inflate(R.layout.custom_action_bar,null);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(actionbarView);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu);
        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM | getSupportActionBar().DISPLAY_SHOW_HOME | getSupportActionBar().DISPLAY_HOME_AS_UP);

        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.header));
        drawerlistener = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.menu, 0, 0);
        mDrawerLayout.setDrawerListener(drawerlistener);
       Bundle data=getIntent().getExtras();// check si l'utilisateur a cliqué sur push notification
        if (data!=null)                     // si l'utilisateur a cliqué sur push notification ouvre le fragment notif
        {
            String frag =data.getString("FRAGMENT");
            if(frag.equals("notification"))
            {
                Fragment fragment=new FragmentNotification();
                FragmentTransaction ft=getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame,fragment);
                ft.commit();

            }
        }


        // Si l'utilisateur est venu normalement, ouvre l'accueil
        else {
            frag = new FragmentHome();
            FragmentManager fragmentManager = getFragmentManager();
            android.app.FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.content_frame, frag).commit();
        }

    }

    private Runnable runnable = new Runnable() { //defining the method to repeat notification service after every 2 minutes
        @Override
        public void run() {
		      new GetNotificationsTask(mActivity,"").execute();
            // toutes les 6 heures
            handler.postDelayed(this, 21600000);
        }
    };
    //Initialise les composants
    public void Init() {
        this.mContext = this;
        this.mActivity = this;
        mGlobal = (Globals) mActivity.getApplicationContext();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        mDrawerList = (ListView) findViewById(R.id.drawerlv);
        slidingmenu=(ViewGroup)findViewById(R.id.layout_slidermenu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.custom_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerlistener.onOptionsItemSelected(item))
        {
            return(true);
        }

        return super.onOptionsItemSelected(item);
    }
    // set les click listener pour les menuitems
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        displayfragment(position);
    }

    public void displayfragment(int pos) {
        switch (pos) {
            case 0:
                frag = new FragmentHome();
                break;
            case 1:
                frag = new FragmentHome();
                if (getFragmentManager().getBackStackEntryCount() > 0 ){
                    getFragmentManager().popBackStack();
                }
                break;
            case 2:
                frag = new FragmentSetting();
                break;
            case 3:
                frag = new FragmentNotification();
                drawerList.get(2).setNavDrawer_count(0);
                myadapter = new DrawerCustomlistAdapter(mContext,drawerList);
                myadapter.notifyDataSetChanged();
                mDrawerList.setAdapter(myadapter);
                break;
            case 4:
                frag=new FragmentProfile();
                break;
            case 5:
                exitApp();
            default:
                break;
        }

        //récupération du frag manager
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        //détection du fragment
        Fragment currentFragment = getFragmentManager().findFragmentById(R.id.content_frame);

        //replace fragment
        if (currentFragment instanceof FragmentHome) {
            Log.v("Fragment", "C'est le frag Home");
            ft.addToBackStack("any");
        }
        else {

        }

        if (frag != null) {
            ft.replace(R.id.content_frame, frag).commit();

            mDrawerLayout.closeDrawer(slidingmenu);// close Navigation drawer menu menu
        }
    }
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            exitApp();
        }
    }

    public void exitApp() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Déconnexion")
                .setMessage("Voulez-vous vraiment partir ?")
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("Non", null).show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        drawerlistener.syncState();
        super.onPostCreate(savedInstanceState);
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerlistener.onConfigurationChanged(newConfig);

    }

    public static void ShowProgressDialog(String msg)
    {
        dialog.setMessage(msg);
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.show();
    }

    // Met à jour la valeur de NavDrawerItemClass dans la drawerList à la position 2 et notifie l'adapter pour qu'il se reset
    public static void updateCount()
    {
        if (notificationList.size()>0)
        {
            drawerList.get(2).setNavDrawer_count(notificationList.size());
            myadapter = new DrawerCustomlistAdapter(mContext,drawerList);
            myadapter.notifyDataSetChanged();
            mDrawerList.setAdapter(myadapter);
        }
    }
    // Montre une alert dialog quand une nouvelle notification apparaît
    public static void ShownotiDialog()
    {
        new AlertDialog.Builder(mContext)
                .setTitle("Notifications")
                .setMessage("Vous avez de nouvelles notifications")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(R.drawable.notification)
                .show();
    }

}
