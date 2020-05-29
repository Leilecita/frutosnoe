package com.example.frutossecos.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frutossecos.Events.EventProductState;
import com.example.frutossecos.R;
import com.example.frutossecos.interfaces.OnAddItemListener;
import com.example.frutossecos.interfaces.OnStartActivity;
import com.example.frutossecos.network.ApiClient;
import com.example.frutossecos.network.Error;
import com.example.frutossecos.network.GenericCallback;
import com.example.frutossecos.network.models.ItemOrder;
import com.example.frutossecos.network.models.Product;
import com.example.frutossecos.types.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class ProductOrderAdapter extends BaseAdapter<Product,ProductOrderAdapter.ViewHolder> {

    private OnAddItemListener onAddItemOrderLister = null;

    private OnStartActivity onStart= null;

    public void setOnAddItemListener(OnAddItemListener lister){
        onAddItemOrderLister = lister;
    }

    public void setOnStart(OnStartActivity lister){
        onStart=lister;
    }

    private Context mContext;
    private Long mOrderId;

    public ProductOrderAdapter(Context context, List<Product> products){
        setItems(products);
        mContext = context;
        mOrderId=90L;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(EventProductState event){
        for(int i=0;i<getListProduct().size();++i){
            if(getListProduct().get(i).id.equals(event.getIdProduct())){
                if(event.mState.equals("edited")){
                    getListProduct().get(i).stock=event.getStock();
                    updateItem(i,getListProduct().get(i));
                }
            }
        }
    }


    public void setOrderId(Long orderId){
        mOrderId=orderId;
    }

    public Long getOrderId(){return mOrderId;}

    public ProductOrderAdapter(){

    }


    public List<Product> getListProduct(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView name;
        public TextView cant;



        public ViewHolder(View v){
            super(v);
            name= v.findViewById(R.id.name_product);

            cant= v.findViewById(R.id.cant);

        }
    }

    @Override
    public ProductOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_select_product,parent,false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    private void clearViewHolder(ProductOrderAdapter.ViewHolder vh){
        if(vh.name!=null)
            vh.name.setText(null);
        if(vh.cant!=null)
            vh.cant.setText(null);


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        clearViewHolder(holder);

        final Product currentProduct=getItem(position);

        holder.name.setText(currentProduct.name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createItemOrder(currentProduct);
            }
        });
    }

    private String getPriceTypeSelected(CheckBox check_min, CheckBox cheack_med, CheckBox check_cuarter){
        if(check_min.isChecked()){
            return Constants.TYPE_PRICE_KG;
        }else if (cheack_med.isChecked()){
            return Constants.TYPE_PRICE_MED_KG;
        }else{
            return Constants.TYPE_PRICE_CUARTER_KG;
        }
    }

    private Double getPriceSelected(CheckBox check_min,CheckBox check_med,CheckBox check_cuarter,Product p){
        if(check_min.isChecked()){
            return p.price;
        }else if(check_med.isChecked()){
            return p.half_price;
        }else{
            return p.cuarter_price;
        }
    }

    private void createItemOrder(final Product p){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_set_quantity, null);
        builder.setView(dialogView);

        final TextView name = dialogView.findViewById(R.id.name);
        final TextView quantity = dialogView.findViewById(R.id.quantity);
        final TextView price = dialogView.findViewById(R.id.price);
        final TextView med_price = dialogView.findViewById(R.id.half_price);
        final TextView cuarter_price = dialogView.findViewById(R.id.cuarter_price);
        final TextView cancel = dialogView.findViewById(R.id.cancel);
        final Button ok = dialogView.findViewById(R.id.ok);
        final CheckBox check_min = dialogView.findViewById(R.id.check_min);
        final CheckBox check_med = dialogView.findViewById(R.id.check_med);
        final CheckBox check_cuarter = dialogView.findViewById(R.id.check_cuarter);

        name.setText(p.name);
        price.setText("$"+String.valueOf(p.price));
        med_price.setText("$"+p.half_price);
        cuarter_price.setText("$"+p.cuarter_price);

        check_min.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(check_min.isChecked()){
                    check_min.setChecked(true);
                    check_med.setChecked(false);
                    check_cuarter.setChecked(false);
                }else{
                    check_min.setChecked(false);
                }
            }
        });

        check_med.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(check_med.isChecked()){
                    check_med.setChecked(true);
                    check_min.setChecked(false);
                    check_cuarter.setChecked(false);
                }else{
                    check_med.setChecked(false);
                }
            }
        });

        check_cuarter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(check_cuarter.isChecked()){
                    check_cuarter.setChecked(true);
                    check_min.setChecked(false);
                    check_med.setChecked(false);
                }else{
                    check_cuarter.setChecked(false);
                }
            }
        });


        final AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fishQuantity=quantity.getText().toString().trim();

                if(!fishQuantity.matches("")){

                    if(p.stock>= Double.valueOf(fishQuantity)){
                        ItemOrder itemOrder=new ItemOrder(p.id,mOrderId,Double.valueOf(fishQuantity)
                                ,p.name,getPriceTypeSelected(check_min,check_med,check_cuarter),
                                getPriceSelected(check_min,check_med,check_cuarter,p));

                        ApiClient.get().postItemOrder(itemOrder, new GenericCallback<ItemOrder>() {
                            @Override
                            public void onSuccess(ItemOrder data) {
                                Toast.makeText(mContext, "Se ha agregado el producto "+name.getText().toString(),Toast.LENGTH_SHORT).show();

                                if(onAddItemOrderLister!=null){
                                    onAddItemOrderLister.onAddItemToOrder(data.id,data.product_id,data.order_id,data.quantity,true,
                                            data.product_name,data.price_type,data.price);
                                }
                            }

                            @Override
                            public void onError(Error error) {

                            }
                        });
                        dialog.dismiss();
                    }else{
                        dialog.dismiss();
                        loadStock(p.name,p.id);
                    }
                }else{
                    Toast.makeText(mContext,"Dato inválido",Toast.LENGTH_LONG).show();
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


    }

    private void loadStock(String fish_name,final Long id_product){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_start_product_activity, null);
        builder.setView(dialogView);

        final TextView name=  dialogView.findViewById(R.id.name);
        name.setText(fish_name);

        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final TextView ok=  dialogView.findViewById(R.id.ok);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startProductsActivity(id_product);
            }
        });
        dialog.show();
    }

    private void startProductsActivity(Long id_product){

        if(onStart!=null){
            onStart.onStartProducts(id_product);
        }
    }
}

