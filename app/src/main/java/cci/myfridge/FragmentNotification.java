package cci.myfridge;


import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.Collections;
import java.util.Comparator;

import cci.myfridge.custom.classes.ProductsClass;
import cci.myfridge.global.Globals;

/**
 * Created by Fabrice KISTNER on 4/29/2015.
 */
public class FragmentNotification extends Fragment {

    static View rootview;
    public static ListView noti_lv;
    Context context;
    static Activity mActivity;
    ProgressDialog pd;
    Dialog dialog;
    static Globals mGlobal;
    ProductsClass productsClass_obj;
    static Notification_listAdapter notification_listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        this.context = container.getContext();
        this.mActivity = getActivity();
        rootview = inflater.inflate(R.layout.fragment_notification, null);
        noti_lv = (ListView) rootview.findViewById(R.id.lv_notification);
        setHasOptionsMenu(true);

        return rootview;


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGlobal=(Globals)context.getApplicationContext();
        // Populate la liste de notification avec les produits
        setAdapter();

        dialog = new Dialog(context);
        // Montre le menu de tri
        setHasOptionsMenu(true);



    }

    public static void setAdapter() {

           // Initialise Notification_listAdapter et lui passe la list de notification products

             notification_listAdapter = new Notification_listAdapter(mActivity,mGlobal.mNotiList);
            noti_lv.setAdapter(notification_listAdapter);// set l'adapter sur la notification listview
            NavDrawerActivity.notificationList.clear();// Enlève tous les produits de la liste de notification quand l'utilisateur click le menu avec les notifs
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    //    inflater.inflate(R.menu.custom_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.date_sort:
                // tri les produits par date
                if (mGlobal.mNotiList.size()> 0) {
                    Collections.sort(mGlobal.mNotiList, new Comparator<ProductsClass>() {

                        @Override
                        public int compare(ProductsClass productsClass1, ProductsClass productsClass2) {
                            return productsClass1.getProduct_added_date().compareTo(productsClass2.getProduct_added_date());

                        }

                    });
                    // Set l'adapter après avoir trié les resultats
                    setAdapter();

                }
                return true;
            case R.id.name_sort:
                // tri les produits par nom
                if (mGlobal.mNotiList.size() > 0) {
                    Collections.sort(mGlobal.mNotiList, new Comparator<ProductsClass>() {

                        @Override
                        public int compare(ProductsClass productsClass, ProductsClass productsClass2) {
                            return productsClass.getProduct_name().compareTo(productsClass2.getProduct_name());
                        }
                    });
                    // Set l'adapter après avoir trié les resultats
                    setAdapter();                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}