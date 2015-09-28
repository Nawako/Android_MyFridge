package cci.myfridge.async;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cci.myfridge.FragmentHome;
import cci.myfridge.NavDrawerActivity;
import cci.myfridge.R;
import cci.myfridge.custom.classes.Constants;
import cci.myfridge.custom.classes.ProductsClass;
import cci.myfridge.global.Globals;


public class GetProductTask extends AsyncTask<String, Void, String> {



    Activity mActivity;
    Globals mGlobal;
    Context mContext;
    SharedPreferences sharedPreferences;

    String toastmsg;


	public GetProductTask(Activity activity,String tmsg) {
		this.mActivity = activity;

        this.toastmsg=tmsg;
        mGlobal=(Globals)mActivity.getApplicationContext();
        sharedPreferences=mActivity.getSharedPreferences("MyFridge",Context.MODE_PRIVATE);
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();


	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		String response = "";
        String id=sharedPreferences.getString("id","");
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(Constants.mWebURL+Constants.mGetproduct);
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

            postParameters.add(new BasicNameValuePair("user_id", id));

            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
            request.setEntity(formEntity);
			HttpResponse httpresponse = httpClient.execute(request);
	        response = EntityUtils.toString(httpresponse.getEntity());
			Log.v("GetProduct Task: ", response);

		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}

		return response;
	}
	
	@Override
	protected void onPostExecute(String result) {

		if (!result.equalsIgnoreCase("")) {
			
			try {
                JSONObject jsonObject=new JSONObject(result);
                String status=jsonObject.getString("status");
                if(Integer.valueOf(status)==0) {
                    JSONArray jsonArray = jsonObject.getJSONArray("products");
                    if (jsonArray.length() > 0) {
                        mGlobal.mProductsList.clear();
                        ProductsClass productsClass_obj;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            productsClass_obj = new ProductsClass();
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            productsClass_obj.product_name = jsonObject1.getString("product_name");
                            productsClass_obj.product_image = jsonObject1.getString("product_image");
                            productsClass_obj.product_id = jsonObject1.getString("product_id");
                            productsClass_obj.product_price = jsonObject1.getString("product_price");
                            productsClass_obj.product_expiry_date = jsonObject1.getString("product_expiry_dt");
                            productsClass_obj.product_added_date = jsonObject1.getString("added_dt");
                            //productsClass_obj.product_about_expires = jsonObject1.getString("about_expire");
                            productsClass_obj.product_description = jsonObject1.getString("product_desc");
                            productsClass_obj.product_userid = jsonObject1.getString("user_id");
                            mGlobal.mProductsList.add(productsClass_obj);
                        }
                            NavDrawerActivity.dialog.dismiss();
                            if(!toastmsg.equals("")) {
                                Toast.makeText(mActivity, toastmsg, Toast.LENGTH_LONG).show();
                            }
                        //FragmentHome.showProducts();
                        Fragment fragment=new FragmentHome();
                        FragmentTransaction ft=mActivity.getFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame,fragment);
                        ft.commit();


                        }

                    }
                else
                {
                    if (mGlobal.callFromNotification)
                    {
                        Toast.makeText(mActivity, toastmsg, Toast.LENGTH_LONG).show();
                        Bundle bundle=new Bundle();
                        bundle.putString("SendFromNotification","yes");
                        Fragment fragment=new FragmentHome();
                        fragment.setArguments(bundle);
                        FragmentTransaction ft=mActivity.getFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame,fragment);
                        ft.commit();
                        mGlobal.callFromNotification=false;
                    }
                    NavDrawerActivity.dialog.dismiss();
                    mGlobal.mProductsList.clear();
                    FragmentHome.showProducts();
                }


			} catch (Exception e) {
				e.printStackTrace();


			}
			}
        else
        {

        }
        new GetNotificationsTask(mActivity,"").execute();

		}





    }
	



