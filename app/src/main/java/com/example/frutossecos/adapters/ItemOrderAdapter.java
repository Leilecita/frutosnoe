package com.example.frutossecos.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frutossecos.R;
import com.example.frutossecos.ValuesHelper;
import com.example.frutossecos.interfaces.OnAddItemListener;
import com.example.frutossecos.network.ApiClient;
import com.example.frutossecos.network.Error;
import com.example.frutossecos.network.GenericCallback;
import com.example.frutossecos.network.models.ItemOrder;
import com.example.frutossecos.types.Constants;

import java.text.DecimalFormat;
import java.util.List;

public class ItemOrderAdapter  extends BaseAdapter<ItemOrder,ItemOrderAdapter.ViewHolder> {

    private OnAddItemListener onAddItemOrderLister = null;

    public void setOnAddItemListener(OnAddItemListener lister){
        onAddItemOrderLister = lister;
    }

    private Context mContext;

    public ItemOrderAdapter(Context context, List<ItemOrder> items){
        setItems(items);
        mContext = context;
    }

    public ItemOrderAdapter(){

    }

    public List<ItemOrder> getListItem(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView name;
        public TextView cant;
        public TextView total_price;
        public TextView price;
        public TextView type;


        public ViewHolder(View v){
            super(v);
            name= v.findViewById(R.id.name_product);
            cant= v.findViewById(R.id.cant);
            total_price= v.findViewById(R.id.total_amount);
            price= v.findViewById(R.id.price);
            type= v.findViewById(R.id.type);

        }
    }

    @Override
    public ItemOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_order_new,parent,false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }


    private void clearViewHolder(ItemOrderAdapter.ViewHolder vh){
        if(vh.name!=null)
            vh.name.setText(null);
        if(vh.cant!=null)
            vh.cant.setText(null);
        if(vh.price!=null)
            vh.price.setText(null);
        if(vh.total_price!=null)
            vh.total_price.setText(null);

        if(vh.type!=null)
            vh.type.setText(null);

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        System.out.println(String.valueOf((double) tmp / factor));
        return (double) tmp / factor;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        clearViewHolder(holder);

        final ItemOrder currentItem=getItem(position);
        holder.name.setText(currentItem.product_name);

        if(currentItem.price_type.equals(Constants.TYPE_PRICE_KG)){
            holder.type.setText("1kg");
        }else if (currentItem.price_type.equals(Constants.TYPE_PRICE_MED_KG)){
            holder.type.setText("1/2kg");
        }else{
            holder.type.setText("1/4kg");
        }

        Double cant=currentItem.quantity;

        holder.cant.setText(ValuesHelper.get().getIntegerQuantityRounded(cant));

        if(currentItem.price_type.equals(Constants.TYPE_PRICE_KG)){
            holder.price.setTextColor(mContext.getResources().getColor(R.color.FishyLetraDark));

        }else{
            holder.price.setTextColor(mContext.getResources().getColor(R.color.FishyLetraDark));
        }

        String text= ValuesHelper.get().getIntegerQuantityRounded(currentItem.price);

        holder.price.setText(text);

        Double total=cant*currentItem.price;

        holder.total_price.setText("$"+round(total,2));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(mContext, holder.itemView);
                popup.getMenuInflater().inflate(R.menu.menu_product_order, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_delete:
                                deleteItem(currentItem,position,holder);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
            }
        });
    }

    private double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    private void deleteItem(final ItemOrder currentItem,final Integer position,ViewHolder holder ){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_delete_item, null);
        builder.setView(dialogView);

        final TextView nameFish = dialogView.findViewById(R.id.name);
        final TextView kg = dialogView.findViewById(R.id.kg);
        final TextView cancel = dialogView.findViewById(R.id.cancel);
        final Button ok = dialogView.findViewById(R.id.ok);

        nameFish.setText(holder.name.getText().toString());
        kg.setText(holder.cant.getText().toString()+"kg");

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ApiClient.get().deleteItemOrder(currentItem.id, new GenericCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        Toast.makeText(mContext,"El producto ha sido borrado",Toast.LENGTH_SHORT).show();
                        removeItem(position);
                        if(onAddItemOrderLister!=null){
                            onAddItemOrderLister.onAddItemToOrder(0l,0l,0l,0d,false,""
                                    ,"",0.0);
                        }
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
        dialog.show();

    }
}
