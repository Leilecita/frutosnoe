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
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frutossecos.CustomLoadingListItemCreator;
import com.example.frutossecos.R;
import com.example.frutossecos.adapters.ProductAdapter;
import com.example.frutossecos.network.ApiClient;
import com.example.frutossecos.network.Error;
import com.example.frutossecos.network.GenericCallback;
import com.example.frutossecos.network.models.AmountProducts;
import com.example.frutossecos.network.models.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ProductActivity extends BaseActivity implements Paginate.Callbacks{

    @Override
    public int getLayoutRes() {
        return R.layout.activity_products;
    }

    private RecyclerView mRecyclerView;
    private ProductAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView cantProducts;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;
    private String mQuery = "";
    private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        setTitle("Productos");

        mRecyclerView = this.findViewById(R.id.list_products);
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ProductAdapter(this, new ArrayList<Product>());
        mRecyclerView.setAdapter(mAdapter);

        cantProducts= findViewById(R.id.cant_products);

        FloatingActionButton addProduct= findViewById(R.id.add_product);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createProduct();
            }
        });

        loadingInProgress=false;
        mCurrentPage=0;
        hasMoreItems = true;

        registerForContextMenu(mRecyclerView);
        getTotProducts();
        implementsPaginate();

    }

    private void getTotProducts(){
        ApiClient.get().getTotProducts(new GenericCallback<AmountProducts>() {
            @Override
            public void onSuccess(AmountProducts data) {
                cantProducts.setText(String.valueOf(data.total));
            }

            @Override
            public void onError(Error error) {

            }
        });
    }
    private void clearview(){
        mCurrentPage = 0;
        mAdapter.clear();
        hasMoreItems=true;
    }

    private void clearAndList(){
        clearview();
        listProducts(mQuery);
        getTotProducts();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_top, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchItem.expandActionView();
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Ingrese nombre");

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.requestFocus();
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(!newText.trim().toLowerCase().equals(mQuery)) {
                    mCurrentPage = 0;
                    mAdapter.clear();
                    listProducts(newText.trim().toLowerCase());
                }
                return false;
            }
        });
        super.onCreateOptionsMenu(menu);

        return true;
    }


    private void createProduct(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_create_product, null);
        builder.setView(dialogView);

        final TextView name= dialogView.findViewById(R.id.product_name);
        final EditText price= dialogView.findViewById(R.id.product_price);
        final EditText half_price= dialogView.findViewById(R.id.half_price);
        final EditText cuarter_price= dialogView.findViewById(R.id.cuarter_price);
        final TextView stock= dialogView.findViewById(R.id.product_stock);
        final Button ok= dialogView.findViewById(R.id.ok);
        final TextView cancel= dialogView.findViewById(R.id.cancel);

        final AlertDialog dialog = builder.create();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String priceProduct=price.getText().toString().trim();
                String halfprice=half_price.getText().toString().trim();
                String cuarterprice=cuarter_price.getText().toString().trim();
                String stockProduct=stock.getText().toString().trim();
                String nameProduct=name.getText().toString().trim();

                if(!priceProduct.matches("") && !stockProduct.matches("") && !nameProduct.matches("") && !halfprice.matches("") && !cuarterprice.matches("")){

                    Product newProduct= new Product(nameProduct,Double.valueOf(priceProduct),Double.valueOf(halfprice),Double.valueOf(cuarterprice),Double.valueOf(stockProduct));
                    ApiClient.get().postProduct(newProduct, new GenericCallback<Product>() {
                        @Override
                        public void onSuccess(Product data) {
                            clearAndList();

                            Toast.makeText(dialogView.getContext(),"Se ha creado el producto "+data.name, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(Error error) {

                        }
                    });

                    dialog.dismiss();

                }else{
                    Toast.makeText(dialogView.getContext()," Complete todo los datos por favor ", Toast.LENGTH_LONG).show();
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void listProducts(final String query) {
        loadingInProgress=true;
        this.mQuery = query;
        final String newToken = UUID.randomUUID().toString();
        this.token =  newToken;


        ApiClient.get().getAliveProductsByPage(mCurrentPage, "alive",mQuery, new GenericCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> data) {

                if(token.equals(newToken)) {

                    if (query == mQuery) {

                        if (data.size() == 0) {
                            hasMoreItems = false;
                        } else {
                            int prevSize = mAdapter.getItemCount();
                            mAdapter.pushList(data);
                            mCurrentPage++;
                            if (prevSize == 0) {
                                layoutManager.scrollToPosition(0);
                            }
                        }
                        loadingInProgress = false;
                    }
                }
            }

            @Override
            public void onError(Error error) {
                loadingInProgress = false;
            }
        });
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
        listProducts(mQuery);
    }

    @Override
    public boolean isLoading() {
        return loadingInProgress;
    }

    @Override
    public boolean hasLoadedAllItems() {
        return !hasMoreItems;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
