package com.meiqua.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.meiqua.lostlibrary.CaptureActivityOrientation;
import com.meiqua.lostlibrary.MainActivity;
import com.meiqua.lostlibrary.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanForReturnFragment extends Fragment {
    private int btnState = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnState = 0;
        Log.i("-scan->","created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.menuSearch = false;
        MainActivity.menuGo=false;
        android.support.v7.widget.Toolbar toolbar =
                (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.tool_bar);
        toolbar.setTitle("return books");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // better use try catch here
        getActivity().invalidateOptionsMenu();

        View rootView = inflater.inflate(R.layout.scan_for_return_fragment, container, false);
        Button scanBarcode = (Button) rootView.findViewById(R.id.scan_barcode);

        scanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button btn = (Button)v;
                if (btnState == 0) {
                   btnState=1;
                    startCapture();
                } else if (btnState == 1) {
                   btnState=2;
                    startCapture();
                } else if (btnState == 2) {
                   btnState=0;
                    //here don't call starCapture()
                    //so has to change button by below codes
                    Drawable drawable=getResources().getDrawable(R.mipmap.scan_book);
                    drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
                    btn.setCompoundDrawables(null,drawable, null, null);
                    btn.setText("扫描书本");
                }

            }
        });

        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        Log.i("-scan->","fragment called");
    //    change button here ,or you will see button changed before capture activity be called
            Button btn=(Button)this.getView().findViewById(R.id.scan_barcode);
        if (btnState == 0) {
            Drawable drawable=this.getResources().getDrawable(R.mipmap.scan_book);
            drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
            btn.setCompoundDrawables(null,drawable, null, null);
            btn.setText("扫描书本");
        } else if (btnState == 1) {
            Drawable drawable=getResources().getDrawable(R.mipmap.scan_jia);
            drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
            btn.setCompoundDrawables(null,drawable, null, null);
            btn.setText("扫描书架");
        } else if (btnState == 2) {
            Drawable drawable = getResources().getDrawable(R.mipmap.smile_face);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            btn.setCompoundDrawables(null, drawable, null, null);
            btn.setText("谢谢配合");

//            MainActivity mainActivity=(MainActivity)getActivity();
//            mainActivity.setJsonObject(null);
//            mainActivity.setScanContentReady("");
            //set it to empty string
        }

    }
    private void startCapture()
    {
        IntentIntegrator intentIntegrator = new IntentIntegrator(getActivity());
        //if use IntentIntegrator.forFragment(this) we can't back to mainActivity
        //by pressing back button
        //so according to
        //https://github.com/journeyapps/zxing-android-embedded/blob/master/sample-nosupport/src/main/java/example/zxing/MainActivity.java
        // I change it to getActivity() and call super in MainActivity
        //but then I find it doesn't work..
        //then I find a way to make it work in fragment by just calling getFragmentManager
        //However,at last I decide to deal with the data in mainActivity
        intentIntegrator.setCaptureActivity(CaptureActivityOrientation.class)
                .setOrientationLocked(false)
                .setBeepEnabled(false)     //this is important
                        //or you will here a beep sound
                        // find it for a long time ..
                .setPrompt("请将二维码置于框内")
                .setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
                .initiateScan();
    }
}
