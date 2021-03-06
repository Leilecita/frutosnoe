package com.example.frutossecos.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frutossecos.CustomLoadingListItemCreator;
import com.example.frutossecos.DateHelper;
import com.example.frutossecos.Events.EventOrderState;
import com.example.frutossecos.R;
import com.example.frutossecos.adapters.ItemOrderAdapter;
import com.example.frutossecos.adapters.ProductOrderAdapter;
import com.example.frutossecos.interfaces.OnAddItemListener;
import com.example.frutossecos.interfaces.OnStartActivity;
import com.example.frutossecos.network.ApiClient;
import com.example.frutossecos.network.Error;
import com.example.frutossecos.network.GenericCallback;
import com.example.frutossecos.network.models.AmountResult;
import com.example.frutossecos.network.models.Client;
import com.example.frutossecos.network.models.ItemOrder;
import com.example.frutossecos.network.models.Order;
import com.example.frutossecos.network.models.Product;
import com.example.frutossecos.network.models.ReportsOrder.ReportOrder;
import com.paginate.Paginate;
import com.paginate.recycler.LoadingListItemSpanLookup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateOrderActivity extends BaseActivity implements Paginate.Callbacks, OnAddItemListener, OnStartActivity {

    String mDeliverDate;
    String mDeliveryTime;
    Order mOrder;
    String mObservation_order;

    TextView deliverDate;
    TextView deliveryTime;
    TextView userName;

    ImageView observation_order;

    TextView totalAmount;
    boolean mEdithOrder;

    //pagination
    private boolean loadingInProgress;
    private Integer mCurrentPage;
    private Paginate paginate;
    private boolean hasMoreItems;

    public void onStartProducts(Long id_product){
        Intent intent = new Intent(this, ProductActivity.class);
        intent.putExtra("IDPRODUCT", id_product);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                reloadProducts();
            }
        }
    }


    public static void start(Context mContext, Client user){
        Intent i=new Intent(mContext, CreateOrderActivity.class);
        i.putExtra("ID",user.id);
        i.putExtra("NAME",user.name);
        i.putExtra("DIRECCION",user.address);
        mContext.startActivity(i);
    }

    public static void startEdithOrder(Context mContext, ReportOrder reportOrder){
        Intent i=new Intent(mContext, CreateOrderActivity.class);
        i.putExtra("ORDERID",reportOrder.order_id);

        //todo aca decia deliver_ date delivery date
        i.putExtra("DELIVERDATE",reportOrder.delivery_date);
        i.putExtra("USERNAME",reportOrder.name);

        mContext.startActivity(i);
    }

    @Override
    public int getLayoutRes() {
        return R.layout.activity_create_order;
    }

    private RecyclerView mRecyclerView;
    private RecyclerView mItemRecyclerView;
    private ProductOrderAdapter mAdapter;
    private ItemOrderAdapter mItemAdapter;
    private GridLayoutManager gridlayoutmanager;

    private RecyclerView.LayoutManager layoutManagerItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showBackArrow();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        setTitle("Nueva orden");

        mEdithOrder=false;
        mOrder=null;
        mDeliverDate=null;
        mDeliveryTime=null;
        mObservation_order="";

        mRecyclerView = this.findViewById(R.id.list_products);
        mAdapter = new ProductOrderAdapter(this, new ArrayList<Product>());
        mRecyclerView.setAdapter(mAdapter);
        gridlayoutmanager=new GridLayoutManager(this,3);
        mRecyclerView.setLayoutManager(gridlayoutmanager);

        mAdapter.setOnAddItemListener(this);
        mAdapter.setOnStart(this);

        mItemRecyclerView = this.findViewById(R.id.list_items);
        layoutManagerItem = new LinearLayoutManager(this);
        mItemRecyclerView.setLayoutManager(layoutManagerItem);
        mItemAdapter = new ItemOrderAdapter(this, new ArrayList<ItemOrder>());
        mItemRecyclerView.setAdapter(mItemAdapter);
        mItemAdapter.setOnAddItemListener(this);

        totalAmount= findViewById(R.id.total_amount);
        userName= findViewById(R.id.name);
        deliverDate=findViewById(R.id.deliver_date);
        deliveryTime=findViewById(R.id.delivery_time);
        observation_order=findViewById(R.id.obs);

        initActivity();

        deliverDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDeliverDate();
            }
        });
        deliveryTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDeliveryTime();
            }
        });
        observation_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setObservation();
            }
        });
    }
    private void setObservation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_create_observation, null);
        builder.setView(dialogView);

        final TextView obs=  dialogView.findViewById(R.id.obs);
        if(mEdithOrder){
            obs.setHint(mObservation_order);
        }
        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final Button ok=  dialogView.findViewById(R.id.ok);
        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(mEdithOrder){
                    mOrder.observation=obs.getText().toString();
                    mObservation_order=obs.getText().toString();
                    ApiClient.get().putOrder(mOrder, new GenericCallback<Order>() {
                        @Override
                        public void onSuccess(Order data) {

                        }

                        @Override
                        public void onError(Error error) {
                        }
                    });
                }else{
                    mObservation_order=obs.getText().toString();
                }

                dialog.dismiss();
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


    private void selectDeliveryTime(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] items = {
                "Mañana", "Mediodía", "Tarde"
        };

        builder.setTitle("Elige el horario de entrega");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                deliveryTime.setText(items[item]);
                mDeliveryTime=(items[item]);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void initActivity(){
        Long order_id= getIntent().getLongExtra("ORDERID",-1);
        if(order_id != -1){ //esta editando
            mEdithOrder=true;

            mDeliverDate=getIntent().getStringExtra("DELIVERDATE");
            ApiClient.get().getOrder(order_id, new GenericCallback<Order>() {
                @Override
                public void onSuccess(Order data) {
                    mOrder=data;
                    reloadItems();
                    //listProducts();

                    implementsPaginate();

                    mObservation_order=data.observation;
                    deliverDate.setText(DateHelper.get().getOnlyDate(mDeliverDate));
                    userName.setText(getIntent().getStringExtra("USERNAME"));
                }

                @Override
                public void onError(Error error) {

                }
            });

        }else{
            System.out.println("se crea orden");
            userName.setText(getIntent().getStringExtra("NAME"));

            createOrder();
        }

    }

    private void getAmount(){
        ApiClient.get().getAmountByOrder(mOrder.id, new GenericCallback<AmountResult>() {
            @Override
            public void onSuccess(AmountResult data) {
                totalAmount.setText(String.valueOf(round(data.total,2)));
            }

            @Override
            public void onError(Error error) {

            }
        });

    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        System.out.println(String.valueOf((double) tmp / factor));
        return (double) tmp / factor;
    }


    private void reloadItems(){
        mItemAdapter.clear();
        getAmount();
        listItems();
    }

    public void reloadProducts(){
        mCurrentPage=0;
        mAdapter.clear();
        loadingInProgress=false;
        hasMoreItems = true;
        listProducts();
    }

    public void onAddItemToOrder(Long id,Long product_id,Long order_id, Double quantity,boolean create,String product_name
            ,String price_type, Double price){

        if(create){
            ItemOrder i=new ItemOrder(product_id,order_id,quantity,product_name,price_type,price);
            i.id=id;
            mItemAdapter.pushItem(i);

        }
        getAmount();
    }

    //todo borrar todos los pedidos con ordern nro mIdOrder
    private void deleteOrderAndAllItems(){
        if(!mEdithOrder){
            System.out.println("ORDEN ID"+mOrder.id);

            //si se cancela el pedido cuando se CREA la orden , se borra la orden y todos los items.
            //si se estaba editando no es necesario
            final ProgressDialog progress = ProgressDialog.show(this, "Cancelando pedido",
                    "Aguarde un momento", true);


            ApiClient.get().deleteOrder(mOrder.id, new GenericCallback<Void>() {
                @Override
                public void onSuccess(Void data) {
                    EventBus.getDefault().post(new EventOrderState(mOrder.client_id,"deleted",mOrder.delivery_date));
                    finish();
                    progress.dismiss();
                }

                @Override
                public void onError(Error error) {
                }
            });
        }


    }

    private void createOrder(){
        Long userid= getIntent().getLongExtra("ID",-1);

        //la ponemos en borrador porque el crontab elimina a las 7 am, pero si de casualidad, esa creando a esa hora. se borra la orden!
        Order order=new Order(userid,"1999-10-01 00:00:00","","borrador","");

        ApiClient.get().postOrder(order, new GenericCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                mOrder=data;
                implementsPaginate();
                // listProducts();
            }
            @Override
            public void onError(Error error) {

            }
        });
    }

    private void listProducts() {
        loadingInProgress=true;
        ApiClient.get().getAliveProductsByPage(mCurrentPage, "alive", "", new GenericCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> data) {
                mAdapter.setOrderId(mOrder.id);
                if (data.size() == 0) {
                    hasMoreItems = false;
                }else{
                    int prevSize = mAdapter.getItemCount();
                    mAdapter.pushList(data);
                    mCurrentPage++;
                    if(prevSize == 0){
                        gridlayoutmanager.scrollToPosition(0);
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

    private void listItems(){

        if(mOrder!=null){
            ApiClient.get().getItemsOrderByOrderId(mOrder.id, new GenericCallback<List<ItemOrder>>() {
                @Override
                public void onSuccess(List<ItemOrder> data) {
                    mItemAdapter.setItems(data);
                }

                @Override
                public void onError(Error error) {

                }
            });
        }
    }

    private void selectDeliverDate(){

        final DatePickerDialog datePickerDialog;
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        datePickerDialog = new DatePickerDialog(CreateOrderActivity.this,R.style.datepicker,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text
                        String sdayOfMonth = String.valueOf(dayOfMonth);
                        if (sdayOfMonth.length() == 1) {
                            sdayOfMonth = "0" + dayOfMonth;
                        }

                        String smonthOfYear = String.valueOf(monthOfYear + 1);
                        if (smonthOfYear.length() == 1) {
                            smonthOfYear = "0" + smonthOfYear;
                        }
                        //mDeliverDate=sdayOfMonth+"-"+smonthOfYear+"-"+year;
                        mDeliverDate=year+"-"+smonthOfYear+"-"+sdayOfMonth+" 05:00:00";
                        deliverDate.setText(DateHelper.get().getOnlyDate(mDeliverDate));
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void putDeliverDate(){
        if(mOrder!=null){
            mOrder.delivery_date=mDeliverDate;
            if(!mEdithOrder){
                mOrder.state="pendiente";
            }

            if(mDeliveryTime!= null){
                mOrder.delivery_time=mDeliveryTime;
            }else{
                mOrder.delivery_time="Horario";
            }

            mOrder.observation=mObservation_order;

            ApiClient.get().putOrder(mOrder, new GenericCallback<Order>() {
                @Override
                public void onSuccess(Order data) {
                    // Toast.makeText(CreateOrderActivity.this,"Fecha de entrega: "+mDeliverDate,Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(Error error) {
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!mEdithOrder){
            getMenuInflater().inflate(R.menu.menu_create, menu);
            return true;
        }
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                if(mDeliverDate!=null){
                    putDeliverDate();
                    //TODO aca decia deliver_date

                    EventBus.getDefault().post(new EventOrderState(mOrder.client_id,"created",mOrder.delivery_date));
                    if(!mEdithOrder){
                        Toast.makeText(CreateOrderActivity.this,"El pedido ha sido creado para "+getIntent().getStringExtra("NAME"),Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(CreateOrderActivity.this,"El pedido ha sido actualizado ",Toast.LENGTH_LONG).show();
                    }

                    finish();
                }else{
                    Toast.makeText(this, "No se ha elegido día de entrega",Toast.LENGTH_LONG).show();
                }
                return true;
            case android.R.id.home:
                if(!mEdithOrder){
                    showDialogCancelOrder();
                }else{
                    putDeliverDate();
                    finish();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialogCancelOrder(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_cancel_order, null);
        builder.setView(dialogView);

        TextView cancel= dialogView.findViewById(R.id.cancel);
        final TextView ok= dialogView.findViewById(R.id.ok);
        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                deleteOrderAndAllItems();
                // finish();
                dialog.dismiss();
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(!mEdithOrder){
                showDialogCancelOrder();
                return true;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
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
        listProducts();
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
