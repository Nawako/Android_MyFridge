package cci.myfridge;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import cci.myfridge.async.UpdateSettingTask;
import cci.myfridge.global.Globals;

/**
 * Created by Fabrice KISTNER on 4/29/2015.
 */
public class FragmentSetting extends Fragment
{

    static View rootview;


    Activity mActivity;
    Globals mGlobal;

    Button btn_updateSettings;
    EditText threshhold;
    CheckBox allowNoti;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mActivity=getActivity();
        rootview=inflater.inflate(R.layout.fragment_setting,null);
        btn_updateSettings=(Button)rootview.findViewById(R.id.update_setting);
        threshhold=(EditText)rootview.findViewById(R.id.et_days);
        allowNoti=(CheckBox)rootview.findViewById(R.id.chk_allownoti);
        return rootview;



    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mGlobal=(Globals)mActivity.getApplicationContext();
        // if settings are saved loading the saved settings
        if(!mGlobal.threshhold.equals(""))
        {
            threshhold.setText(mGlobal.threshhold);
        }
       if(mGlobal.allowNoti==true)
       {
           allowNoti.setChecked(true);
       }
        btn_updateSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isValid=true;
                SharedPreferences sharedPreferences=mActivity.getSharedPreferences("MyFridge",Context.MODE_PRIVATE);
                String id=sharedPreferences.getString("id","");
                String isAllowNoti;
                setHasOptionsMenu(false);//hide menu
                if(allowNoti.isChecked())
                {
                    isAllowNoti="YES";
                    mGlobal.allowNoti=true;

                }
                else
                {
                    isAllowNoti="NO";
                    mGlobal.allowNoti=false;
                }
                if(threshhold.getText().toString().isEmpty())
                {
                    threshhold.setError("S'il vous plaît entrez le nombre de jour que vous souhaitez avant qu'un produit n'expire");
                    isValid=false;
                    threshhold.requestFocus();
                }
                    if(isValid) {
                        mGlobal.threshhold=threshhold.getText().toString();
                        //async task to update settings
                        new UpdateSettingTask(mActivity).execute(id, isAllowNoti, threshhold.getText().toString());
                    }

            }
        });




    }
}
