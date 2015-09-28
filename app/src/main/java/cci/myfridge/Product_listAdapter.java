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
import cci.myfridge.global.Globals;


/**
 * Created by Fabrice KISTNER on 4/30/2015.
 */
public class Product_listAdapter extends BaseAdapter {

    private Context context;
    ArrayList<ProductsClass> productList;
    Globals mGlobal;
    ProductsClass productsClass_pbj;

    public Product_listAdapter(Context context)
    {
        this.context = context;
        mGlobal=(Globals)context.getApplicationContext();
        productList=mGlobal.mProductsList;

    }
    @Override
    public int getCount() {

        return productList.size();
    }

    @Override
    public Object getItem(int position)


    {

        return productList.get(position);
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
            row=inflater.inflate(R.layout.product_list_item, null);

        }
        else
        {
            row=convertView;

        }

        productsClass_pbj=productList.get(position);
        TextView titletv=(TextView)row.findViewById(R.id.product_name);
       // TextView expiretv=(TextView)row.findViewById(R.id.product_expiry);
        TextView proadded=(TextView)row.findViewById(R.id.product_added);
        ImageView img=(ImageView)row.findViewById(R.id.productitemimg);
        titletv.setText(productsClass_pbj.getProduct_name());
       // expiretv.setText(productsClass_pbj.getProduct_expiry_date());
        proadded.setText("Date d'ajout : "+productsClass_pbj.getProduct_added_date()+"");
        img.setImageResource(0);
        if(!productsClass_pbj.getProduct_image().equals(""))
        {
            Picasso.with(context).cancelRequest(img);
            Picasso.with(context).load(Constants.mWebURL + productsClass_pbj.getProduct_image() + "").into(img);
        } else
        {
            Picasso.with(context).cancelRequest(img);
            img.setImageResource(R.drawable.dp);
        }

        return row;
    }


}