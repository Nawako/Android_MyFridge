package cci.myfridge;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.camera.CropImageIntentBuilder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Calendar;

import cci.myfridge.async.AddProductTask;
import cci.myfridge.async.EditProductTask;
import cci.myfridge.custom.classes.Constants;
import cci.myfridge.custom.classes.ProductsClass;
import cci.myfridge.global.Globals;

/**
 * Created by Fabrice KISTNER on 4/29/2015.
 */
public class FragmentAddProduct extends Fragment implements View.OnClickListener {

    static View rootview;

    Context context;
    Button btn_AddProduct;
    ImageView mProduct_img;
    Activity mActivity;
    Globals mGlobal;
    Bitmap mSelectedImage;
    EditText mTxt_productName,mTxt_productExpirydt,mTxt_productPrice,mTxt_productDesc;
    private final static int PHOTO_PICKER_ID = 20;
    ProductsClass productsClass_obj;
    int year,month,day;
    Calendar mcurrentDate;
    Calendar mEnterdDate;
    private static int REQUEST_PICTURE = 0;
    private static int REQUEST_CROP_PICTURE = 1;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.context=container.getContext();
        rootview=inflater.inflate(R.layout.fragment_add_product,null);
        btn_AddProduct=(Button)rootview.findViewById(R.id.btn_add_prod);
        mProduct_img=(ImageView)rootview.findViewById(R.id.product_img);
        mTxt_productName=(EditText)rootview.findViewById(R.id.product_name);
        mTxt_productExpirydt=(EditText)rootview.findViewById(R.id.product_expiry_date);
        mTxt_productPrice=(EditText)rootview.findViewById(R.id.product_price);
        mTxt_productDesc=(EditText)rootview.findViewById(R.id.description);



        return rootview;



    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btn_AddProduct.setOnClickListener(this);
        mProduct_img.setOnClickListener(this);
        mTxt_productExpirydt.setOnClickListener(this);
        mGlobal=(Globals)context.getApplicationContext();
        mActivity=getActivity();
        setHasOptionsMenu(false);// cache le menu
        if(mGlobal.editProduct) {
            // Vérifie si c'est un produit à éditer (et donc pré-remplir les détails)
            Bundle bundle = getArguments();
            if (bundle != null) {
                productsClass_obj = (ProductsClass) bundle.getSerializable("PRODUCT_OBJ");
                mTxt_productName.setText(productsClass_obj.getProduct_name());
                mTxt_productName.setEnabled(false);
                mTxt_productExpirydt.setText(productsClass_obj.getProduct_expiry_date());
                mTxt_productPrice.setText(productsClass_obj.getProduct_price());
                mTxt_productDesc.setText(productsClass_obj.getProduct_description());
                if (!productsClass_obj.getProduct_image().equals("")) {
                    Picasso.with(context).load(Constants.mWebURL + productsClass_obj.getProduct_image() + "").into(mProduct_img);
                }
                btn_AddProduct.setText("Sauvegarde du produit");
            }
        }
    }

    @Override
    public void onClick(View view) {

        boolean isvalid=true;

        SharedPreferences sharedPreferences=mActivity.getSharedPreferences("MyFridge",Context.MODE_PRIVATE);
        String user_id=sharedPreferences.getString("id","");
        switch (view.getId())
        {

            case R.id.btn_add_prod:
                if(mTxt_productName.getText().toString().isEmpty())
                {
                    isvalid=false;
                    mTxt_productName.setError("S'il vous plaît entrez un nom pour le produit");
                    mTxt_productName.requestFocus();
                }

                if(mTxt_productExpirydt.getText().toString().isEmpty())
                {
                    if(isvalid)
                    {
                    mTxt_productExpirydt.requestFocus();
                    }
                    isvalid=false;
                    mTxt_productExpirydt.setError("S'il vous plaît entrez une date d'expiration valide");

                }

                if(mTxt_productPrice.getText().toString().isEmpty())
                {
                    if(isvalid)
                    {
                        mTxt_productPrice.requestFocus();
                    }
                    isvalid=false;
                    mTxt_productPrice.setError("S'il vous plaît entrez un prix");

                }


                String mEncodedImage = "";

                if(mSelectedImage != null) {
                    mEncodedImage = mGlobal.encodeTobase64(mSelectedImage);// Encode en base 64
                }

                if(mGlobal.editProduct)
                {
                    mGlobal.editProduct=false;
                    // L'async task met à jour le produit
                    new EditProductTask(mActivity,context).execute(productsClass_obj.getProduct_id(),mTxt_productName.getText().toString(),mEncodedImage
                            ,mTxt_productPrice.getText().toString(),mTxt_productDesc.getText().toString()
                            ,mTxt_productExpirydt.getText().toString(),user_id);
                }
                else {

                    if (isvalid) {

                        // L'async task ajoute un nouveau produit
                        new AddProductTask(mActivity, context).execute(mTxt_productName.getText().toString(), mEncodedImage
                                , mTxt_productPrice.getText().toString(), mTxt_productDesc.getText().toString()
                                , mTxt_productExpirydt.getText().toString(), user_id);
                    }
                }
                break;

            case R.id.product_img:
                selectImage();
                break;

            case R.id.product_expiry_date:
                mTxt_productExpirydt.requestFocus();
                pickdate();
                break;
        }
    }

    // Ouvre le date picker
    public void pickdate()
    {

        mcurrentDate=Calendar.getInstance();

        year=mcurrentDate.get(Calendar.YEAR);
        month=mcurrentDate.get(Calendar.MONTH);
        day=mcurrentDate.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener()
        {
            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday)
            {
                mEnterdDate=Calendar.getInstance();
                mEnterdDate.set(Calendar.YEAR, selectedyear);
                mEnterdDate.set(Calendar.MONTH, selectedmonth);
                mEnterdDate.set(Calendar.DAY_OF_MONTH, selectedday);
                if(mcurrentDate.before(mEnterdDate))// Vérifie que la date entrez est bien dans le futur
                {
                    year = selectedyear;
                    month = selectedmonth+1;
                    day = selectedday;
                    mTxt_productExpirydt.setText(""+year+"-"+month+"-"+day+"");
                    mTxt_productExpirydt.setError(null);
                }
                else
                {
                    mTxt_productExpirydt.setError("S'il vous plaît entrez une date valide");
                    mTxt_productExpirydt.requestFocus();

                }

            }
        },year, month, day);

        mDatePicker.setTitle("S'il vous plaît sélectionner une date");
        mDatePicker.show();
    }
    // Options pour ajouter une image
    private void selectImage() {
        final CharSequence[] items = { "Prendre une photo", "Sélectionner depuis la gallerie",
                "Annuler" };

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("Sélectionner une photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Prendre une photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_PICTURE);
                } else if (items[item].equals("Sélectionner depuis la gallerie")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Choisir le fichier"),
                            REQUEST_PICTURE);
                } else if (items[item].equals("Annuler")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Uri imageUri = null;
        try {
            imageUri = imageReturnedIntent.getData();
        }catch (NullPointerException e) {

        }

        if ((requestCode == REQUEST_PICTURE) && (resultCode == Activity.RESULT_OK)) {


            // When the user is done picking a picture, let's start the CropImage Activity,
            // setting the output image file and size to 200x200 pixels square.

//                CropImageIntentBuilder cropImage = new CropImageIntentBuilder(200, 200, croppedImage);
//                cropImage.setOutlineColor(0xFF03A9F4);
//                cropImage.setSourceImage(imageReturnedIntent.getData());
//
//                startActivityForResult(cropImage.getIntent(context), REQUEST_CROP_PICTURE);

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
            startActivityForResult(cropIntent, 1);
        } else if ((requestCode == REQUEST_CROP_PICTURE) && (resultCode == -1)) {
            // When we are done cropping, display it in the ImageView.
            Bitmap selectedImage = null;
            try {
                Bundle extras = imageReturnedIntent.getExtras();
                selectedImage = extras.getParcelable("data");
            } catch (Exception ex) {

            }
            if(selectedImage != null) {
                mSelectedImage = selectedImage;
                mProduct_img.setBackgroundResource(0);
                mProduct_img.setImageBitmap(mGlobal.getCircleBitmap(mSelectedImage));
        //        mSelectedImage = mProduct_img.getDrawingCache();
            }
        }
    }
}
