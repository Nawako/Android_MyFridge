package cci.myfridge.async;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cci.myfridge.NavDrawerActivity;
import cci.myfridge.custom.classes.Constants;
import cci.myfridge.global.Globals;


public class DeleteProductTask extends AsyncTask<String, Void, String> {


    Activity mActivity;
    Globals mGlobal;
    Context mContext;


	public DeleteProductTask(Activity activity, Context context) {
		this.mActivity = activity;
        this.mContext=context;
        mGlobal=(Globals)mActivity.getApplicationContext();
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
        NavDrawerActivity.ShowProgressDialog("Suppression du produit");

	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		String response = "";
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(Constants.mWebURL+Constants.mDeleteProduct);
           List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

            postParameters.add(new BasicNameValuePair("product_id", params[0]));

            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
           request.setEntity(formEntity);
			HttpResponse httpresponse = httpClient.execute(request);
	        response = EntityUtils.toString(httpresponse.getEntity());
			Log.v("Get Signin Task: ", response);

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
                if(Integer.valueOf(status)==0)
                {

                    mGlobal.mNotiList.clear();
                    NavDrawerActivity.notificationList.clear();
                    new GetProductTask(mActivity,jsonObject.getString("message")).execute();

                }
                else
                {
                    NavDrawerActivity.dialog.dismiss();
                    Toast.makeText(mActivity,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
                }





			} catch (Exception e) {
				e.printStackTrace();
                NavDrawerActivity.dialog.dismiss();

			}
			}
        else
        {
            NavDrawerActivity.dialog.dismiss();
        }

		}
    }
	



