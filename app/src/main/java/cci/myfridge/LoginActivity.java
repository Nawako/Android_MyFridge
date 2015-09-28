package cci.myfridge;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import cci.myfridge.async.ForgotPasswordTask;
import cci.myfridge.async.SignInTask;
import cci.myfridge.global.Globals;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

    private Context mContext;
    private LoginActivity mActivity;
    Globals mGlobal;
    Button btn_login,btn_register;
    TextView tv_forgotPss;
    Dialog dialog;
    GoogleCloudMessaging gcm;
    String gcmID;
    EditText mTxtuser_id,mTxtpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.login_activity);
        Init();

        LayoutInflater inflater=(LayoutInflater)this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        View actionbarView=inflater.inflate(R.layout.custom_action_bar,null);
        getSupportActionBar().setCustomView(actionbarView);
        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.header));
        gcm=GoogleCloudMessaging.getInstance(mActivity);
        registerInBackground();// register the device to gcm and get device id

        btn_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        tv_forgotPss.setOnClickListener(this);
        dialog=new Dialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

    }

    public void Init() {
        this.mContext = this;
        this.mActivity = this;
        mGlobal = (Globals) mActivity.getApplicationContext();
        btn_login=(Button)findViewById(R.id.btn_login);
        btn_register=(Button)findViewById(R.id.btn_register);
        tv_forgotPss=(TextView)findViewById(R.id.tv_forgotpass);
        mTxtuser_id=(EditText)findViewById(R.id.txt_username);
        mTxtpassword=(EditText)findViewById(R.id.txt_password);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
          //  return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        boolean isValid=true;
        switch (view.getId())
        {
            case R.id.btn_login:

                if(mTxtuser_id.getText().toString().isEmpty())
            {
                mTxtuser_id.setError("S'il vous plaît entrez un nom d'utilisateur");
                mTxtuser_id.requestFocus();
                isValid=false;

            }
                if(mTxtpassword.getText().toString().isEmpty())
                {
                    if(isValid)
                    {mTxtpassword.requestFocus();}
                    mTxtpassword.setError("S'il vous plaît entrez un mot de passe");
                    isValid=false;
                }

               if (isValid)
               {
                   //async task pour logger l'utilisateur
                   new SignInTask(mActivity,mContext).execute(mTxtuser_id.getText().toString(),mTxtpassword.getText().toString(),gcmID);
                   mGlobal.callFromLoginActivity = true;
               }

                break;
            case R.id.btn_register:
                Intent intent=new Intent(mContext,RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_forgotpass:
                //ouvre le dialog pour le mot de passe oublié
                showForgotPssDialog();//
                break;
        }
    }

    public void showForgotPssDialog()
    {

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_custom_forgot_password);
        Button mBtnSubmitView = (Button) dialog.findViewById(R.id.btn_submit);
        final EditText mTxt_userid=(EditText)dialog.findViewById(R.id.txt_forgotpass_email);
        mBtnSubmitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isvalid=true;
                // TODO Auto-generated method stub

                if(mTxt_userid.getText().toString().isEmpty())
                {
                    mTxt_userid.setError("S'il vous plaît entrez votre mail");
                    isvalid=false;

                }
                if(!mTxt_userid.getText().toString().isEmpty())
                {
                    if(!mGlobal.emailValidator(mTxt_userid.getText().toString()))
                        mTxt_userid.setError("Mail invalide");
                    else
                        isvalid=true;

                }
                if(isvalid)
                {
                    dialog.dismiss();
                    // Appel de l'async task pour envoyer un nouveau mot de passe à l'utilisateur
                new ForgotPasswordTask(mActivity).execute(mTxt_userid.getText().toString());
            }}
        });
                dialog.show();
    }
          private void registerInBackground() {
                new AsyncTask() {

            @Override
            protected String doInBackground(Object[] params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(mContext);// get objet GCM
                    }
                    gcmID = gcm.register("378874944053");// registering to gcm using google console project number
                    Log.i("IIIIIIIIDDDDDDDDLlD", gcmID);
                    msg = "Device registered, registration ID=" + gcmID;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            protected void onPostExecute(String msg) {
                //mDisplay.append(msg + "\n");
            }
        }.execute(null, null, null);
    }
    }

