package cci.myfridge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cci.myfridge.custom.classes.NavDrawerItemClass;
import cci.myfridge.global.Globals;

/**
 * Created by Fabrice KISTNER on 4/30/2015.
 */
public class DrawerCustomlistAdapter extends BaseAdapter {

    private Context context;
    Globals mGlobal;
    ArrayList<NavDrawerItemClass> ItemsList;
    NavDrawerItemClass navDrawerItemClass_obj;

    public DrawerCustomlistAdapter(Context context, ArrayList<NavDrawerItemClass> itemList) {

        this.context = context;
        this.ItemsList=itemList;
        mGlobal=(Globals)context.getApplicationContext();

    }

    @Override
    public int getCount() {

        return ItemsList.size();
    }

    @Override
    public Object getItem(int position){

        return ItemsList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=null;
        if(convertView==null)
        {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            row=inflater.inflate(R.layout.nav_drawer_list_item, null);
        }
        else
        {
            row=convertView;
        }
        // getting objects one by one on each position
        // Récupère les objets 1 par 1 sur chaque position

        navDrawerItemClass_obj=ItemsList.get(position);

        TextView titletv=(TextView)row.findViewById(R.id.itemtitle);
        TextView notitv=(TextView)row.findViewById(R.id.tv_noti);
        ImageView img=(ImageView)row.findViewById(R.id.draweritemimg);

        img.setImageResource(navDrawerItemClass_obj.getNavDrawer_icon());
        titletv.setText(navDrawerItemClass_obj.getNavDrawer_title());

        // Vérifie si l'item est en 2nde position dans la liste
        if(position==2)
        {
            // Vérifie si il n'y pas d'autres notifications
            if(navDrawerItemClass_obj.getNavDrawer_count()>0)
            {
                // Affiche la notification  sur le text view
                notitv.setVisibility(View.VISIBLE);
                notitv.setText(String.valueOf(navDrawerItemClass_obj.getNavDrawer_count()));
            }
            else
            {
                notitv.setVisibility(View.GONE);
            }
        }
        else
        {
            notitv.setVisibility(View.GONE);
        }
        return row;
    }
}