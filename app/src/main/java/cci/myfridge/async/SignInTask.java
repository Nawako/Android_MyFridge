package cci.myfridge.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import cci.myfridge.R;
import cci.myfridge.custom.classes.Constants;
import cci.myfridge.global.Globals;


public class SignInTask extends AsyncTask<String, Void, String> {


	ProgressDialog dialog;
    Activity mActivity;
    Globals mGlobal;
    Context mContext;

    SharedPreferences sharedPreferences;
    public final String FIRST_NAME="first_name";
    public final String LAST_NAME="last_name";
    public final String PHOTO="photo";
    public final String PHONENO="phoneno";
    public final String DISPLAY_NAME="display_name";
    public final String EMAIL="email";
    public final String ID="id";


	public SignInTask(Activity activity, Context context) {
		this.mActivity = activity;
        this.mContext=context;
		dialog = new ProgressDialog(activity);
		String str = "Connexion en cours";
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
		try {
            //Appel du webservice
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost request = new HttpPost(Constants.mWebURL+Constants.mLogin);

            //Prepare les params
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();

            postParameters.add(new BasicNameValuePair("user_id", params[0]));
            postParameters.add(new BasicNameValuePair("password", params[1]));
            postParameters.add(new BasicNameValuePair("gcm_id", params[2]));


            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
           request.setEntity(formEntity);
			HttpResponse httpresponse = httpClient.execute(request);
            //Get réponse serveur
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
                    // Si l'utilisateur se log avec succès, sauvegarde les détails utilisateur dans le sharedPref et
                    // ouvre NavDrawerActivity
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
                    mGlobal.OpenActivity(mContext, mActivity, NavDrawerActivity.class, R.anim.push_left_in, R.anim.push_left_out);

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
	



