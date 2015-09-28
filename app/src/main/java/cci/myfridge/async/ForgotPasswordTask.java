package cci.myfridge.async;

import android.app.Activity;
import android.app.ProgressDialog;
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

import cci.myfridge.custom.classes.Constants;
import cci.myfridge.global.Globals;


public class ForgotPasswordTask extends AsyncTask<String, Void, String> {


	ProgressDialog dialog;
    Activity mActivity;
    Globals mGlobal;

	public ForgotPasswordTask(Activity activity) {
		this.mActivity = activity;
		dialog = new ProgressDialog(activity);
		String str = "Envoi d'un mail";
		dialog.setTitle(str+"...");
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		dialog.show();
        mGlobal=(Globals)mActivity.getApplicationContext();
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();

	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		String response = "";
		try {
            //calling service
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(Constants.mWebURL+Constants.mForgotPwd);
           List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

            postParameters.add(new BasicNameValuePair("email", params[0]));

            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
           request.setEntity(formEntity);
			HttpResponse httpresponse = httpClient.execute(request);
	        response = EntityUtils.toString(httpresponse.getEntity());
            //Get response
            response= response.replace("\uFEFF", ""); //Enlève les caractères qu'on ne voit pas de la réponse
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
                    dialog.dismiss();
                    Toast.makeText(mActivity,jsonObject.getString("reason"),Toast.LENGTH_LONG).show();
                   // mGlobal.OpenActivity(mContext, mActivity, LoginActivity.class, R.anim.push_left_in, R.anim.push_left_out);

                }
                else
                {
                    dialog.dismiss();
                    Toast.makeText(mActivity,jsonObject.getString("reason"),Toast.LENGTH_LONG).show();
                }





			} catch (Exception e) {
				e.printStackTrace();
				dialog.cancel();

			}
			}
        else
        {
            dialog.dismiss();
        }

		}





    }
	



