package cci.myfridge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import cci.myfridge.custom.classes.Constants;
import cci.myfridge.custom.classes.ProductsClass;


/**
 * Created by Fabrice KISTNER on 4/30/2015.
 */
public class Notification_listAdapter extends BaseAdapter {

    private Context context;
    ArrayList<ProductsClass> ProNotiList;
    ProductsClass productsClass_obj;

    public Notification_listAdapter(Context context, ArrayList<ProductsClass> notiList)
    {
        this.context = context;
        this.ProNotiList=notiList;

    }
    @Override
    public int getCount() {
        int siz=ProNotiList.size();

        return siz;
    }

    @Override
    public Object getItem(int position)


    {

        return ProNotiList.get(position);
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
            row=inflater.inflate(R.layout.notification_list_item, null);

        }
        else
        {
            row=convertView;

        }

        productsClass_obj=ProNotiList.get(position);

        TextView titletv=(TextView)row.findViewById(R.id.noti_product_name);
        TextView added_dt=(TextView)row.findViewById(R.id.noti_product_added);
        TextView pro_days=(TextView)row.findViewById(R.id.days_left_val);

        ImageView img1=(ImageView)row.findViewById(R.id.noti_productitemimg);
        titletv.setText(productsClass_obj.getProduct_name());
        added_dt.setText("Date d'ajout : "+productsClass_obj.getProduct_added_date()+"");
        pro_days.setText(productsClass_obj.getProduct_expiry_days());
        img1.setImageResource(0);
        if(!productsClass_obj.getProduct_image().equals(""))
        {
            Picasso.with(context).cancelRequest(img1);
            Picasso.with(context).load(Constants.mWebURL+productsClass_obj.getProduct_image()+"").into(img1);
        }
        else
        {
            Picasso.with(context).cancelRequest(img1);
            img1.setImageResource(R.drawable.dp);
        }


        return row;
    }


}