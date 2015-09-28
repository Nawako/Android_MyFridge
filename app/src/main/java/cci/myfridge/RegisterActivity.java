package cci.myfridge;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import cci.myfridge.async.RegisterTask;
import cci.myfridge.global.Globals;


public class RegisterActivity extends ActionBarActivity implements View.OnClickListener {

    private Context mContext;
    public static RegisterActivity mActivity;
    Globals mGlobal;
    Button btn_register;
    ImageView mProfile_img;
    EditText mTxt_firstname,mTxt_lastname,mTxt_displayname,mTxt_phone,mTxt_password,mTxt_email;
    Bitmap mSelectedImage;
    String gcm_id;
    GoogleCloudMessaging gcm;
    private final static int PHOTO_PICKER_ID = 20;
    private static int REQUEST_PICTURE = 1;
    private static int REQUEST_CROP_PICTURE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Enlève la notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.fragment_register);
        Init();
        gcm=GoogleCloudMessaging.getInstance(mContext);
        registerInBackground();//Registering device on gcm and getting device id for Push notification
        LayoutInflater inflater=(LayoutInflater)this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        View actionbarView=inflater.inflate(R.layout.custom_action_bar,null);//inflate actoinbar layout
        getSupportActionBar().setCustomView(actionbarView);//set custome layput to actionbar
        getSupportActionBar().setDisplayOptions(getSupportActionBar().DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.header));
        btn_register.setOnClickListener(this);
        mProfile_img.setOnClickListener(this);


    }

    public void Init() {
        this.mContext = this;
        this.mActivity = this;
        mGlobal = (Globals) mActivity.getApplicationContext();

        btn_register=(Button)findViewById(R.id.btn_register);
        mProfile_img=(ImageView)findViewById(R.id.user_img);
        mTxt_firstname=(EditText)findViewById(R.id.user_first_name);
        mTxt_lastname=(EditText)findViewById(R.id.user_last_name);
        mTxt_displayname=(EditText)findViewById(R.id.user_display_name);
        mTxt_phone=(EditText)findViewById(R.id.user_phno);
        mTxt_password=(EditText)findViewById(R.id.user_password);
        mTxt_email=(EditText)findViewById(R.id.user_email);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        boolean isvalid=true;
        switch (view.getId())
        {
            case R.id.btn_register:
            if(mTxt_firstname.getText().toString().isEmpty())
            {
                isvalid=false;
                mTxt_firstname.setError("S'il vous plaît entrez votre prénom");//setting error to text field in case of empty field
                mTxt_firstname.requestFocus();
            }
                if(mTxt_lastname.getText().toString().isEmpty())
                {
                    if(isvalid)
                    {mTxt_lastname.requestFocus();}
                    isvalid=false;
                    mTxt_lastname.setError("S'il vous plaît entrez votre nom de famille");

                }
                if(mTxt_displayname.getText().toString().isEmpty())
                {
                    if(isvalid)
                    {mTxt_displayname.requestFocus();}

                    isvalid=false;
                    mTxt_displayname.setError("S'il vous plaît entrez votre pseudonyme");

                }
                if(mTxt_email.getText().toString().isEmpty())
                {
                    if(isvalid)
                    {
                        mTxt_email.requestFocus();
                    }
                    isvalid=false;
                    mTxt_email.setError("S'il vous plaît entrez un mail valide");

                }
                if(!mTxt_email.getText().toString().isEmpty()&& isvalid)
                {
                    if(!mGlobal.emailValidator(mTxt_email.getText().toString()))
                    {
                        if(isvalid)
                        {
                            mTxt_email.requestFocus();
                        }

                        isvalid=false;
                        mTxt_email.setError("S'il vous plaît entrez un mail valide");

                    }
                }
                if(mTxt_phone.getText().toString().isEmpty())
                {
                    if(isvalid)
                    {mTxt_phone.requestFocus();}

                    isvalid=false;
                    mTxt_phone.setError("S'il vous plaît entrez un numéro de téléphone");

                }
                if(!mTxt_phone.getText().toString().isEmpty())
                {
                    if(mTxt_phone.getText().toString().length()<5) {
                        if (isvalid) {
                            mTxt_phone.requestFocus();
                        }
                        isvalid=false;
                        mTxt_phone.setError("S'il vous plaît entrez un numéro de téléphone valide");
                    }



                }
                if(mTxt_password.getText().toString().isEmpty())
                {
                    if(isvalid)
                {
                    mTxt_password.requestFocus();
                }

                    isvalid=false;
                    mTxt_password.setError("S'il vous plaît entrez votre mot de passe");

                }

                String mEncodedImage = "";
                if(mSelectedImage != null) {
                    mEncodedImage = mGlobal.encodeTobase64(mSelectedImage);//encode image en base 64 pour l'envoyer au server
                }
                    if(isvalid)
                {
                    //appel async et passent les valeurs pour enregistrer l'utilisateur
                    new RegisterTask(mActivity,mContext).execute(mTxt_firstname.getText().toString(),mTxt_lastname.getText().toString()
                            ,mTxt_displayname.getText().toString(),mTxt_email.getText().toString(),mTxt_phone.getText().toString()
                            ,mTxt_password.getText().toString(),mEncodedImage,gcm_id);
                    mGlobal.callFromRegisterActivity = true;
                }
                break;
            case R.id.user_img:
                ShowPictureDialog();// options pour sélectionner photo ou prendre photo
                break;
        }

    }
    public void ShowPictureDialog() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Choisir une image de profil"), PHOTO_PICKER_ID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        Uri imageUri = null;
        try {
            imageUri = imageReturnedIntent.getData();
        }catch (NullPointerException e) {

        }
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Intent intent;

        switch(requestCode) {
            case PHOTO_PICKER_ID:
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        //call the standard crop action intent (the user device may not support it)
                        Intent cropIntent = new Intent("com.android.camera.action.CROP");
                        //indicate image type and Uri
                        cropIntent.setDataAndType(imageUri, "image/*");
                        //set crop properties
                        cropIntent.putExtra("crop", "true");
                        //indicate aspect of desired crop
                        cropIntent.putExtra("aspectX", 1);
                        cropIntent.putExtra("aspectY", 1);
                        //indicate output X and Y
                        cropIntent.putExtra("outputX", 256);
                        cropIntent.putExtra("outputY", 256);
                        //retrieve data on return
                        cropIntent.putExtra("return-data", true);
                        //start the activity - we handle returning in onActivityResult
                        startActivityForResult(cropIntent, 30);
                    } catch (Exception e) {
                        Toast toast = Toast.makeText(this,"This device doesn't support the crop action! Exception: " + e.toString(),Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }
                break;
            case 30:
                Bitmap selectedImage = null;
                try {
                    Bundle extras = imageReturnedIntent.getExtras();
                    selectedImage = extras.getParcelable("data");
                } catch (Exception ex) {

                }
                if(selectedImage != null) {
                    mSelectedImage = selectedImage;
                    mProfile_img.setBackgroundResource(0);
                    mProfile_img.setImageBitmap(mGlobal.getCircleBitmap(mSelectedImage));// obtient l'image ronde
                }
                break;

        }
    }
    private void registerInBackground() {
        new AsyncTask() {

            @Override
            protected String doInBackground(Object[] params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(mContext);//getting gcm object
                    }
                    gcm_id = gcm.register("378874944053"); //register the device and get gcm device id by sending google console project no as param
                    Log.i("IIIIIIIIDDDDDDDDLlD", gcm_id);
                    msg = "Device registered, registration ID=" + gcm_id;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);
    }


    }

