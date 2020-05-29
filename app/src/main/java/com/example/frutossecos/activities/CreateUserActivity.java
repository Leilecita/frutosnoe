package com.example.frutossecos.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.NavUtils;

import com.example.frutossecos.R;
import com.example.frutossecos.network.ApiClient;
import com.example.frutossecos.network.Error;
import com.example.frutossecos.network.GenericCallback;
import com.example.frutossecos.network.models.Client;
import com.example.frutossecos.network.models.Zone;

import java.util.ArrayList;
import java.util.List;

public class CreateUserActivity extends BaseActivity{

    private EditText mUserName;
    private EditText mUserPhone;
    private EditText mUserAddress;
    private EditText mFacebook;
    private EditText mInstagram;
    private ImageView home;
    private ImageView ubic;
    private ImageView user;
    private ImageView inst;
    private ImageView face;

    private AutoCompleteTextView zone;

    private ImageView mAddNeighborhood;

    private ArrayAdapter<String> adapter;
    private String mNeighborhood;
    private boolean validateNeighbor;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_create_user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        mUserName = findViewById(R.id.user_name);
        mUserPhone =  findViewById(R.id.user_phone);
        mUserAddress =  findViewById(R.id.user_address);
        mAddNeighborhood=findViewById(R.id.add_neigh);
        mInstagram=findViewById(R.id.instagram);
        mFacebook=findViewById(R.id.facebook);

        home=findViewById(R.id.home);
        ubic=findViewById(R.id.ubic);
        user=findViewById(R.id.user);
        inst=findViewById(R.id.inst);
        face=findViewById(R.id.face);

        home.setColorFilter(this.getResources().getColor(R.color.word_clear));
        ubic.setColorFilter(this.getResources().getColor(R.color.word_clear));
        user.setColorFilter(this.getResources().getColor(R.color.word_clear));
        inst.setColorFilter(this.getResources().getColor(R.color.word_clear));
        face.setColorFilter(this.getResources().getColor(R.color.word_clear));
        mAddNeighborhood.setColorFilter(this.getResources().getColor(R.color.colorAccent));


        zone=findViewById(R.id.nei);
        validateNeighbor=false;
        mNeighborhood=null;

        mAddNeighborhood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CreateUserActivity.this, ZonesActivity.class);
                startActivityForResult(i, 1);
            }
        });

        listZones();
    }

    private void listZones(){
        zone.setError(null);
        ApiClient.get().getAllZones(new GenericCallback<List<Zone>>() {
            @Override
            public void onSuccess(List<Zone> data) {
                final List<String> listneighborhoods=createArray(data);
                adapter = new ArrayAdapter<String>(getBaseContext(),
                        android.R.layout.simple_dropdown_item_1line, listneighborhoods);
                zone.setThreshold(1);
                zone.setAdapter(adapter);
                zone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (!b) {
                            String val = zone.getText() + "";
                            if(listneighborhoods.contains(val)){
                                validateNeighbor=true;
                            }else{
                                validateNeighbor=false;
                                zone.setError("Barrio inválido");
                            }
                        }
                    }
                });
            }

            @Override
            public void onError(Error error) {

            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                zone.clearFocus();
                if(validateNeighbor){
                    String name=mUserName.getText().toString().trim();
                    String address=mUserAddress.getText().toString().trim();
                    String zon=zone.getText().toString().trim();
                    String phone=mUserPhone.getText().toString().trim();
                    String inst=mInstagram.getText().toString().trim();
                    String fac=mFacebook.getText().toString().trim();


                    Client c=new Client(name,address,phone,fac,inst,zon,0);

                    final ProgressDialog progress = ProgressDialog.show(this, "Creando cliente",
                            "Aguarde un momento", true);

                    ApiClient.get().postClient(c, new GenericCallback<Client>() {
                        @Override
                        public void onSuccess(Client data) {
                            finish();
                            progress.dismiss();
                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });

                    return true;
                }else{

                    Toast.makeText(this, "Ingrese un barrio válido",Toast.LENGTH_LONG).show();
                    return true;
                }

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private List<String> createArray(List<Zone> list){
        List<String> listN=new ArrayList<>();
        for(int i=0; i < list.size();++i){
            if(list.get(i) != null && list.get(i).name != null){
                listN.add(list.get(i).name);
            }
        }
        return listN;
    }

    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode,resultCode,data);

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                mNeighborhood=result;
                zone.setText(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
            reloadInfo();
        }
    }
    private void reloadInfo(){
        listZones();
        mUserPhone.setText(mUserPhone.getText().toString());
        mUserAddress.setText(mUserAddress.getText().toString());
        mUserName.setText(mUserName.getText().toString());
        if(mNeighborhood!=null){
            zone.setText(mNeighborhood);
            validateNeighbor=true;
        }
    }
}