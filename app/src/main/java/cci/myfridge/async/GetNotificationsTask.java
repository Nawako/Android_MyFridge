package cci.myfridge.async;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

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

import cci.myfridge.NavDrawerActivity;
import cci.myfridge.custom.classes.Constants;
import cci.myfridge.custom.classes.ProductsClass;
import cci.myfridge.global.Globals;


public class GetNotificationsTask extends AsyncTask<String, Void, String> {



    Activity mActivity;
    Globals mGlobal;
    Context mContext;
    SharedPreferences sharedPreferences;
    String settings;



	public GetNotificationsTask(Activity activity, String seting) {
		this.mActivity = activity;
        this.settings=seting;

        mGlobal=(Globals)mActivity.getApplicationContext();
        sharedPreferences=mActivity.getSharedPreferences("MyFridge",Context.MODE_PRIVATE);
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();

        try {
            mGlobal.mNotiList.clear();
            NavDrawerActivity.notificationList.clear();
        }
        catch (Exception e) {}
	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		String response = "";
        String id=sharedPreferences.getString("id","");
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(Constants.mWebURL+Constants.mGetNotificatios);
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
                        //mGlobal.mNotiList.clear();
                        //NavDrawerActivity.notificationList.clear();
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
                            productsClass_obj.product_expiry_days=jsonObject1.getString("days");
                            productsClass_obj.product_description = jsonObject1.getString("product_desc");
                            productsClass_obj.product_userid = jsonObject1.getString("user_id");
                            boolean isGlobalexisting=false;
                            //boolean isNewexisting=false;
                            for(int j=0;j<mGlobal.mNotiList.size();j++)//check if product alredy exists in list
                            {
                                ProductsClass obj_productClass=mGlobal.mNotiList.get(j);
                                if(obj_productClass.getProduct_name().equals(productsClass_obj.getProduct_name()))
                                {
                                    isGlobalexisting=true;
                                }
                            }
                            if (!isGlobalexisting)// if product not existing in list add new product local and global notification list
                            {
                                mGlobal.mNotiList.add(productsClass_obj);
                                NavDrawerActivity.notificationList.add(productsClass_obj);
                            }





                        }

                    }
                    if(NavDrawerActivity.notificationList.size()>0) {
                        NavDrawerActivity.updateCount();
                        if(mGlobal.allowNoti)
                        {
                            NavDrawerActivity.ShownotiDialog();
                        }
                    }

                }
             if(settings.equals("settings"))
             {
                 mGlobal.callFromNotification=true;
                 new GetProductTask(mActivity,"Les paramètres ont été mis à jour.").execute();
             }


			} catch (Exception e) {
				e.printStackTrace();


			}
			}
        else
        {

        }

		}





    }
	



