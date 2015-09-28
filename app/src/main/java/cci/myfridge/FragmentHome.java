package cci.myfridge;


import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;

import cci.myfridge.async.DeleteProductTask;
import cci.myfridge.async.GetNotificationsTask;
import cci.myfridge.async.GetProductTask;
import cci.myfridge.custom.classes.Constants;
import cci.myfridge.custom.classes.ProductsClass;
import cci.myfridge.global.Globals;

/**
 * Created by Fabrice KISTNER on 4/29/2015.
 */
public class FragmentHome extends Fragment
{

    static View rootview;
    static ListView product_list;
    static Context context;
    ProgressDialog pd;
    static Product_listAdapter product_listAdapter;
    Dialog dialog;
    Button btn_add_product;
    ProductsClass productsClass_obj;
    Globals mGlobal;
    Activity mActivity;
    String test="";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.context=container.getContext();
        this.mActivity=getActivity();
        rootview=inflater.inflate(R.layout.fragment_home,null);
        product_list=(ListView)rootview.findViewById(R.id.lv_products);
        btn_add_product=(Button)rootview.findViewById(R.id.btn_ad_product);
        Bundle bundle=getArguments();

        if(bundle!=null) {

            test = bundle.getString("SendFromNotification", "");
        }
            return rootview;
 }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGlobal=(Globals)context.getApplicationContext();
        // Montre le menu de tri
        setHasOptionsMenu(true);

        if (!test.equals("yes")) {

            if (mGlobal.mProductsList.size() == 0 || mGlobal.callFromLoginActivity || mGlobal.callFromRegisterActivity) {
                mGlobal.callFromLoginActivity = false;
                mGlobal.callFromRegisterActivity = false;
                NavDrawerActivity.ShowProgressDialog("Chargement des produits");
                new GetProductTask(mActivity, "").execute();
                new GetNotificationsTask(mActivity,"").execute();
            }
            else
            {
                showProducts();
            }
        }


        btn_add_product.setOnClickListener(new View.OnClickListener() {
            // opening add product fragment
            @Override
            public void onClick(View view) {
                Fragment fragment=new FragmentAddProduct();
                FragmentTransaction ft=getFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame,fragment);
                ft.commit();
            }
        });
        dialog=new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        product_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                productsClass_obj= (ProductsClass) product_listAdapter.getItem(i);
                showProdDialog();
            }
        });

}
    // Ouvre le dialog pour EDITER ou SUPPRIMER un produit
    public void showProdDialog()
    {
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_product_edit_delete);
        Button mBtnEditView = (Button) dialog.findViewById(R.id.btn_pro_edit);
        Button mBtnDelView = (Button) dialog.findViewById(R.id.btn_pro_delete);
        TextView mProdAdded=(TextView)dialog.findViewById(R.id.product_added);
        TextView mProName=(TextView)dialog.findViewById(R.id.product_name);
        ImageView mProdImg=(ImageView)dialog.findViewById(R.id.productitemimg);
        if(!productsClass_obj.getProduct_image().equals(""))
        {
            Picasso.with(context).load(Constants.mWebURL+productsClass_obj.getProduct_image()+"").into(mProdImg);
        }
        mProdAdded.setText(productsClass_obj.getProduct_added_date());
        mProName.setText(productsClass_obj.getProduct_name());
        mBtnEditView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                Fragment fragment=new FragmentAddProduct();
                mGlobal.editProduct=true;
                Bundle bundle=new Bundle();
                bundle.putSerializable("PRODUCT_OBJ",productsClass_obj);
                FragmentTransaction ft=getFragmentManager().beginTransaction();
                fragment.setArguments(bundle);
                ft.replace(R.id.content_frame,fragment);
                ft.commit();
            }
        });
        mBtnDelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                // Execute delete product task
                new DeleteProductTask(mActivity,context).execute(productsClass_obj.getProduct_id());
            }
        });
        dialog.show();
    }
    // Populate la listView avec les produits
    public static void showProducts()
    {
        product_listAdapter=new Product_listAdapter(context);
        product_list.setAdapter(product_listAdapter);

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.custom_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.date_sort:// Tri les produits par date
                if (mGlobal.mProductsList.size()> 0) {
                    Collections.sort(mGlobal.mProductsList, new Comparator<ProductsClass>() {

                        @Override
                        public int compare(ProductsClass productsClass1, ProductsClass productsClass2) {

                            return productsClass1.getProduct_added_date().compareTo(productsClass2.getProduct_added_date());
                        }
                    });
                    // Raffraîchit la liste après le tri
                    showProducts();
                }
                return true;
            case R.id.name_sort:// Tri les produits par nom
                if (mGlobal.mProductsList.size() > 0) {
                    Collections.sort(mGlobal.mProductsList, new Comparator<ProductsClass>() {

                        @Override
                        public int compare(ProductsClass productsClass, ProductsClass productsClass2) {
                            return productsClass.getProduct_name().compareTo(productsClass2.getProduct_name());
                        }
                    });
                    // Raffraîchit la liste après le tri
                    showProducts();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    }
