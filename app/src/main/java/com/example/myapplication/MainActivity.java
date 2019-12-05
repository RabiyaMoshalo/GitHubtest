package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Model.QRGeoModel;
import com.example.myapplication.Model.QRURLMode;
import com.example.myapplication.Model.QRVCardModel;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.w3c.dom.Text;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scannerView =(ZXingScannerView)findViewById(R.id.zxscan);
        txtResult=(TextView)findViewById(R.id.txt_result);





        ///////////////get permission
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {

                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                       scannerView.setResultHandler(MainActivity.this);
                        scannerView.startCamera();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                        Toast.makeText(MainActivity.this,"Please Accept The Permission",Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();


    }

    @Override
    protected void onDestroy() {
        scannerView.stopCamera();
        super.onDestroy();
    }


    @Override
    public void handleResult(Result rawResult) {

       processRawResult(rawResult.getText());


        if(Patterns.WEB_URL.matcher(rawResult.getText()).matches()) {
            // Open URL
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(rawResult.getText()));
            startActivity(browserIntent);
        }


    }



    private void processRawResult(String text) {
        if (text.startsWith("BEGIN:")){
                String[] tokens = text.split("\n");
            QRVCardModel qrvCardModel = new QRVCardModel();
            for (int i = 0; i < tokens.length; i++)
            {
                if (tokens[i].startsWith("BEGIN:")) {
                    qrvCardModel.setType(tokens[i].substring("BEGIN:".length())); //Remove the begin to get the type
                }

                else if (tokens[i].startsWith("N")) {
                    qrvCardModel.setName(tokens[i].substring("N:".length()));
                }

                else if (tokens[i].startsWith("ORG")) {
                    qrvCardModel.setOrg(tokens[i].substring("ORG:".length()));
                }

                else if (tokens[i].startsWith("TEL:")) {

                    qrvCardModel.setTel(tokens[i].substring("TEL:".length()));
                }

                else if (tokens[i].startsWith("URL:")) {
                    qrvCardModel.setUrl(tokens[i].substring("URL:".length()));
                }

                else if (tokens[i].startsWith("EMAIL:")) {
                    qrvCardModel.setEmail(tokens[i].substring("EMAIL:".length()));
                }

                else if (tokens[i].startsWith("ADS:")) {
                    qrvCardModel.setEmail(tokens[i].substring("ADS:".length()));
                }

                else if (tokens[i].startsWith("NOTE:")) {
                    qrvCardModel.setNote(tokens[i].substring("NOTE:".length()));
                }

                else if (tokens[i].startsWith("SUMMERY:")) {
                    qrvCardModel.setSummer(tokens[i].substring("SUMMERY:".length()));
                }
                else if (tokens[i].startsWith("DTSTART:")) {
                    qrvCardModel.setDtstart(tokens[i].substring("DTSTART:".length()));
                }
                else if (tokens[i].startsWith("DTEND:")) {
                    qrvCardModel.setDtend(tokens[i].substring("DTEND:".length()));
                }

                txtResult.setText(qrvCardModel.getType());
            }

            }
            else if (text.startsWith("hhtp://")||
                text.startsWith("hhtps://")||
                text.startsWith("www."))

            {
               QRURLMode qrurlMode = new QRURLMode(text);
               txtResult.setText(qrurlMode.getUrl());
        }
            else if (text.startsWith("geo:"))
            {
            QRGeoModel qrGeoModel= new QRGeoModel();
            String delims = "[ ,?q= ] +";
            String tokens[]= text.split(delims);
            for (int i=0; i< tokens.length;i++)
            {

                if (tokens[i].startsWith("geo:"))
                {
        qrGeoModel.setLat(tokens[i].substring("geo:".length()));

                }
            }
        qrGeoModel.setLat(tokens[0].substring("geo".length()));
        qrGeoModel.setLng(tokens[1]);
        qrGeoModel.setGeo_place(tokens[2]);
        txtResult.setText(qrGeoModel.getLat()+"/"+qrGeoModel.getLng());

        }

            else
        {
            txtResult.setText(text);
        }
        scannerView.resumeCameraPreview(MainActivity.this);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        super.onBackPressed();

            ///// add comment
    }
}

