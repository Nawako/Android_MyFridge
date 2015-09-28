package cci.myfridge.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
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


public class UpdateProfileTask extends AsyncTask<String, Void, String> {


	ProgressDialog dialog;
    Activity mActivity;
    Globals mGlobal;
    Context mContext;
    String firstName,lastName,displayName,photo,phone,email,id;
    SharedPreferences sharedPreferences;
    public final String FIRST_NAME="first_name";
    public final String LAST_NAME="last_name";
    public final String PHOTO="photo";
    public final String PHONENO="phoneno";
    public final String DISPLAY_NAME="display_name";
    public final String EMAIL="email";
    public final String ID="id";
    LayoutInflater inflater;



	public UpdateProfileTask(Activity activity, Context context) {
		this.mActivity = activity;
        this.mContext=context;
		dialog = new ProgressDialog(activity);
		String str = "Mise à jour du profil";
		dialog.setTitle(str+"...");
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);
		dialog.show();
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
        firstName=params[0];
        lastName=params[1];
        displayName=params[2];
        email=params[3];
        phone=params[4];
        photo=params[5];
        id=params[6];
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(Constants.mWebURL+Constants.mUpdateProfile);
           List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

            postParameters.add(new BasicNameValuePair("first_name", params[0]));
            postParameters.add(new BasicNameValuePair("last_name", params[1]));
            postParameters.add(new BasicNameValuePair("display_name", params[2]));
            postParameters.add(new BasicNameValuePair("email", params[3]));
            postParameters.add(new BasicNameValuePair("phone", params[4]));
            postParameters.add(new BasicNameValuePair("photo", params[5]));
            postParameters.add(new BasicNameValuePair("platform","ANDROID"));
            postParameters.add(new BasicNameValuePair("user_id", params[6]));


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
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString(FIRST_NAME,jsonObject.getString("firstname"));
                    editor.putString(LAST_NAME,jsonObject.getString("lastname"));
                    editor.putString(DISPLAY_NAME,jsonObject.getString("displayname"));
                    editor.putString(EMAIL,jsonObject.getString("email"));
                    editor.putString(PHONENO,jsonObject.getString("phone"));
                    editor.putString(PHOTO,jsonObject.getString("photo"));
                    editor.putString(ID,jsonObject.getString("user_id"));
                    editor.commit();
                    dialog.dismiss();
                    Toast.makeText(mActivity,"Le profil a été mis à jour avec succès",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(mActivity,NavDrawerActivity.class);
                    mActivity.startActivity(intent);



                }
                else
                {
                    dialog.dismiss();
                    Toast.makeText(mActivity,jsonObject.getString("message"),Toast.LENGTH_LONG).show();
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
	



