package cci.myfridge;


import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import cci.myfridge.async.UpdateProfileTask;
import cci.myfridge.custom.classes.Constants;
import cci.myfridge.global.Globals;

/**
 * Created by Fabrice KISTNER on 4/29/2015.
 */
public class FragmentProfile extends Fragment implements View.OnClickListener {

    static View rootview;
    Context context;
    ProgressDialog pd;
    ImageView profile_pic;
    EditText tv_firstName,tv_lastName,tv_displayName,tv_phno,tv_email;
    Button btn_editProfile;
    SharedPreferences sharedPreferences;
    Activity mActivity;
    public final String FIRST_NAME="first_name";
    public final String LAST_NAME="last_name";
    public final String PHOTO="photo";
    public final String PHONENO="phoneno";
    public final String DISPLAY_NAME="display_name";
    public final String EMAIL="email";
    public final String ID="id";
    boolean editProfile;
    Bitmap mSelectedImage;
    private final static int PHOTO_PICKER_ID = 20;
    Globals mGlobal;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.context=container.getContext();
        this.mActivity=getActivity();
        rootview=inflater.inflate(R.layout.fragment_profile, null);
        profile_pic=(ImageView)rootview.findViewById(R.id.user_dp);
        tv_firstName=(EditText)rootview.findViewById(R.id.first_name_val);
        tv_lastName=(EditText)rootview.findViewById(R.id.last_name_val);
        tv_displayName=(EditText)rootview.findViewById(R.id.display_name_val);
        tv_phno=(EditText)rootview.findViewById(R.id.phno_val);
        tv_email=(EditText)rootview.findViewById(R.id.email_val);
        btn_editProfile=(Button)rootview.findViewById(R.id.btn_edit_profile);
        setHasOptionsMenu(false);
        return rootview;



    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //getting user data from shared preference and showing in edittexts and disable to edit all edittexts
        sharedPreferences=mActivity.getSharedPreferences("MyFridge",Context.MODE_PRIVATE);
        mGlobal=(Globals)context.getApplicationContext();
        tv_firstName.setText(sharedPreferences.getString(FIRST_NAME,""));
        tv_firstName.setEnabled(false);
        tv_lastName.setText(sharedPreferences.getString(LAST_NAME,""));
        tv_lastName.setEnabled(false);
        tv_displayName.setText(sharedPreferences.getString(DISPLAY_NAME,""));
        tv_displayName.setEnabled(false);
        tv_phno.setText(sharedPreferences.getString(PHONENO,""));
        tv_phno.setEnabled(false);
        tv_email.setText(sharedPreferences.getString(EMAIL,""));
        tv_email.setEnabled(false);
        editProfile=false;
        setHasOptionsMenu(false);//hide menu

        if(!sharedPreferences.getString(PHOTO,"").equals(""))
        {
            Picasso.with(context).load(Constants.mWebURL+sharedPreferences.getString(PHOTO,"")+"").into(profile_pic);
        }
        btn_editProfile.setOnClickListener(this);
        profile_pic.setOnClickListener(this);
        profile_pic.setClickable(false);




    }


    @Override
    public void onClick(View view) {
        boolean isvalid=true;
        switch (view.getId())
        {

            case R.id.btn_edit_profile:
                if(!editProfile) {
                    //enable to edit all text fields
                    tv_firstName.setEnabled(true);
                    tv_lastName.setEnabled(true);
                    tv_displayName.setEnabled(true);
                    tv_phno.setEnabled(true);
                    profile_pic.setClickable(true);
                    tv_email.setEnabled(true);
                    btn_editProfile.setText("Mis à jour");
                    editProfile=true;
                    return;
                }
                if (editProfile) {
                    if (tv_firstName.getText().toString().isEmpty()) {
                        isvalid = false;
                        tv_firstName.setError("S'il vous plaît entrez votre prénom");
                        tv_firstName.requestFocus();
                    }
                    if (tv_lastName.getText().toString().isEmpty()) {
                        if(isvalid)
                        {
                            tv_lastName.requestFocus();
                        }
                        isvalid = false;
                        tv_lastName.setError("S'il vous plaît entrez votre nom de famille");

                    }
                    if (tv_displayName.getText().toString().isEmpty()) {
                        if(isvalid)
                        {
                            tv_displayName.requestFocus();
                        }
                        isvalid = false;
                        tv_displayName.setError("S'il vous plaît entrez votre pseudonyme");

                    }
                    if (tv_email.getText().toString().isEmpty()) {
                        if(isvalid)
                        {
                            tv_email.requestFocus();
                        }
                        isvalid = false;
                        tv_email.setError("S'il vous plaît entrez votre mot de passe");

                    }
                    if (!tv_email.getText().toString().isEmpty()&& isvalid) {
                        if(!mGlobal.emailValidator(tv_email.getText().toString()))
                        {
                            if(isvalid)
                            {
                                tv_email.requestFocus();
                            }
                            isvalid = false;
                            tv_email.setError("Email invalide");


                        }
                    }
                    if (tv_phno.getText().toString().isEmpty()) {
                        if(isvalid)
                        {
                            tv_phno.requestFocus();
                        }
                        isvalid = false;
                        tv_phno.setError("S'il vous plaît entrez votre numéro de téléphone");

                    }
                    if (!tv_phno.getText().toString().isEmpty()) {
                        if(tv_phno.getText().toString().length()<5) {
                            if (isvalid) {
                                tv_phno.requestFocus();
                            }
                            isvalid = false;
                            tv_phno.setError("S'il vous plaît entrez un numéro valide");
                        }
                    }


                    String mEncodedImage = "";
                    if (mSelectedImage != null) {
                        //converting image to base 64
                        mEncodedImage = mGlobal.encodeTobase64(mSelectedImage);
                    }
                    if (isvalid) {
                        editProfile=false;
                        new UpdateProfileTask(mActivity, context).execute(tv_firstName.getText().toString(),tv_lastName.getText().toString(),
                                tv_displayName.getText().toString(),tv_email.getText().toString(),tv_phno.getText().toString(),
                                mEncodedImage,sharedPreferences.getString(ID,""));

                    }
                }
                break;
            case R.id.user_dp:
                // open dialog to choose picture
                ShowPictureDialog();
                break;
        }
    }
    public void ShowPictureDialog() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent,"Choisir sa photo de profil"), PHOTO_PICKER_ID);
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
                        e.printStackTrace();
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
                    profile_pic.setBackgroundResource(0);
                    profile_pic.setImageBitmap(mGlobal.getCircleBitmap(mSelectedImage));
                }
                break;

        }
    }
}
