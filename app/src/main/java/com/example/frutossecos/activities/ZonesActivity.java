package com.example.frutossecos.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frutossecos.CustomLoadingListItemCreator;
import com.example.frutossecos.R;
import com.example.frutossecos.adapters.ZoneAdapter;
import com.example.frutossecos.network.ApiClient;
import com.example.frutossecos.network.Error;
import com.example.frutossecos.network.GenericCallback;
import com.example.frutossecos.network.models.Zone;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.List;

public class ZonesActivity extends BaseActivity implements Paginate.Callbacks {

    @Override
    public int getLayoutRes() {
        return R.layout.activity_zones;
    }

    private RecyclerView mRecyclerView;
    private ZoneAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        setTitle("Zonas");

        mRecyclerView = this.findViewById(R.id.list_zones);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ZoneAdapter(this, new ArrayList<Zone>());
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton addProduct = findViewById(R.id.add_neighborhood);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addZone();
            }
        });

        implementsPaginate();

    }
    private void listNeighborhoods(){
        loadingInProgress=true;
        ApiClient.get().getZonesByPage(mCurrentPage, new GenericCallback<List<Zone>>() {
            @Override
            public void onSuccess(List<Zone> data) {
                if (data.size() == 0) {
                    hasMoreItems = false;
                }else{
                    int prevSize = mAdapter.getItemCount();
                    mAdapter.pushList(data);
                    mCurrentPage++;
                    if(prevSize == 0){
                        layoutManager.scrollToPosition(0);
                    }
                }
                loadingInProgress = false;
            }

            @Override
            public void onError(Error error) {
                loadingInProgress = false;
            }
        });
    }


    private void addZone() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.create_zone, null);
        builder.setView(dialogView);

        final TextView name = dialogView.findViewById(R.id.neighborhood_name);

        final TextView cancel = dialogView.findViewById(R.id.cancel);
        final Button ok = dialogView.findViewById(R.id.ok);
        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Zone z= new Zone(name.getText().toString().trim());
                ApiClient.get().postZone(z, new GenericCallback<Zone>() {
                    @Override
                    public void onSuccess(Zone data) {
                        mAdapter.pushItem(data);
                    }

                    @Override
                    public void onError(Error error) {

                    }
                });

                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_zone, menu);
        return true;
    }

    private void loadNeighborhood(){
        String name=null;
        if(mAdapter.isChecked()){
            name=mAdapter.getItem(mAdapter.getPosChecked()).name;
        }
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",name);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                loadNeighborhood();
                return true;
            case android.R.id.home:
                loadNeighborhood();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void implementsPaginate(){

        loadingInProgress=false;
        mCurrentPage=0;
        hasMoreItems = true;

        paginate= Paginate.with(mRecyclerView,this)
                .setLoadingTriggerThreshold(2)
                .addLoadingListItem(true)
                .setLoadingListItemCreator(new CustomLoadingListItemCreator())
                .setLoadingListItemSpanSizeLookup(new LoadingListItemSpanLookup() {
                    @Override
                    public int getSpanSize() {
                        return 0;
                    }
                })
                .build();
    }

    @Override
    public void onLoadMore() {
        listNeighborhoods();
    }

    @Override
    public boolean isLoading() {
        return loadingInProgress;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return !hasMoreItems;
    }
}
