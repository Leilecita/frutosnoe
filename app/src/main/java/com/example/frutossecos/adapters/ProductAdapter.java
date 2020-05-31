package com.example.frutossecos.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frutossecos.R;
import com.example.frutossecos.network.APIService;
import com.example.frutossecos.network.ApiClient;
import com.example.frutossecos.network.Error;
import com.example.frutossecos.network.GenericCallback;
import com.example.frutossecos.network.models.Product;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ProductAdapter  extends BaseAdapter<Product,ProductAdapter.ViewHolder> {

    private Context mContext;

    public ProductAdapter(Context context, List<Product> products){
        setItems(products);
        mContext = context;
    }

    public ProductAdapter(){

    }

    public List<Product> getListProduct(){
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView name;
        public TextView price;
        public TextView med_price;
        public TextView cuarter_price;
        public TextView stock;
        public TextView soldedCant;
        public ImageView options;

        public ViewHolder(View v){
            super(v);
            name= v.findViewById(R.id.name_product);
            price= v.findViewById(R.id.price_product);
            stock= v.findViewById(R.id.stock_product);
            options=v.findViewById(R.id.options);
            med_price=v.findViewById(R.id.med_price);
            cuarter_price=v.findViewById(R.id.cuarter_price);

        }
    }

    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_item_product,parent,false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    private void clearViewHolder(ProductAdapter.ViewHolder vh){
        if(vh.name!=null)
            vh.name.setText(null);
        if(vh.price!=null)
            vh.price.setText(null);
        if(vh.med_price!=null)
            vh.med_price.setText(null);
        if(vh.cuarter_price!=null)
            vh.cuarter_price.setText(null);
        if(vh.stock!=null)
            vh.stock.setText(null);
        if(vh.soldedCant!=null)
            vh.soldedCant.setText(null);

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        clearViewHolder(holder);

        final Product currentProduct=getItem(position);

        holder.name.setText(currentProduct.name);

        holder.price.setText("$"+getIntegerQuantity(currentProduct.price));
        if(currentProduct.half_price!=null)
            holder.med_price.setText("$"+getIntegerQuantity(currentProduct.half_price));

        if(currentProduct.cuarter_price!=null)
            holder.cuarter_price.setText("$"+getIntegerQuantity(currentProduct.cuarter_price));

        holder.stock.setText(getIntegerQuantity(currentProduct.stock));

        holder.options.setColorFilter(mContext.getResources().getColor(R.color.word_clear));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(mContext, holder.options);
                popup.getMenuInflater().inflate(R.menu.menu_products, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_delete:
                                deleteProduct(currentProduct, position);
                                return true;
                            case R.id.menu_edit:
                                edithProduct(currentProduct, position,holder);

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

    private void deleteProduct(final Product p,final int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_delete_product, null);
        builder.setView(dialogView);

        final TextView name = dialogView.findViewById(R.id.name);
        final TextView price = dialogView.findViewById(R.id.price);
        final TextView cancel = dialogView.findViewById(R.id.cancel);
        final Button ok = dialogView.findViewById(R.id.ok);

        name.setText(p.name);
        price.setText(getIntegerQuantity(p.price));
        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ApiClient.get().deleteProduct(p.id, new GenericCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        removeItem(position);
                        Toast.makeText(mContext, "Se ha eliminado el producto "+p.name, Toast.LENGTH_LONG).show();
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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void edithProduct(final Product p,final int position, final ViewHolder holder){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_edith_product, null);
        builder.setView(dialogView);


        final TextView edit_name= dialogView.findViewById(R.id.edit_name);
        final TextView edit_price= dialogView.findViewById(R.id.edit_price);
        final TextView edit_wholesaler_price= dialogView.findViewById(R.id.edit_half_price);
        final TextView edit_cuarter_price= dialogView.findViewById(R.id.edit_cuarter_price);
        final TextView edit_stock= dialogView.findViewById(R.id.edit_stock);
        final TextView cancel= dialogView.findViewById(R.id.cancel);
        final Button ok= dialogView.findViewById(R.id.ok);


        edit_name.setText(p.name);
        edit_name.setTextColor(mContext.getResources().getColor(R.color.colorDialogButton));
        edit_price.setText(getIntegerQuantity(p.price));
        edit_price.setTextColor(mContext.getResources().getColor(R.color.colorDialogButton));

        edit_wholesaler_price.setText(getIntegerQuantity(p.half_price));
        edit_wholesaler_price.setTextColor(mContext.getResources().getColor(R.color.colorDialogButton));

        edit_cuarter_price.setText(getIntegerQuantity(p.cuarter_price));
        edit_cuarter_price.setTextColor(mContext.getResources().getColor(R.color.colorDialogButton));

        edit_stock.setText(getIntegerQuantity(p.stock));
        edit_stock.setTextColor(mContext.getResources().getColor(R.color.colorDialogButton));

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productName=edit_name.getText().toString().trim();
                String productPrice=edit_price.getText().toString().trim();
                String productWholesalerPrice=edit_wholesaler_price.getText().toString().trim();
                String productCuarterPrice=edit_cuarter_price.getText().toString().trim();
                String productStock=edit_stock.getText().toString().trim();


                if(!productName.matches("")){
                    p.name=productName;
                }

                if(!productPrice.matches("")) {
                    p.price=Double.valueOf(productPrice);
                }

                if(!productWholesalerPrice.matches("")) {
                    p.half_price=Double.valueOf(productWholesalerPrice);
                }

                if(!productCuarterPrice.matches("")) {
                    p.cuarter_price=Double.valueOf(productWholesalerPrice);
                }

                if(!productStock.matches("")) {
                    p.stock=Double.valueOf(productStock);
                }

                updateItem(position,p);

                Product p2= new Product(p.name,p.price,p.half_price,p.cuarter_price,p.stock);
                p2.id=p.id;

                ApiClient.get().putProduct(p2, new GenericCallback<Product>() {
                    @Override
                    public void onSuccess(Product data) {
                      //  EventBus.getDefault().post(new EventProductState(p.id,"edited",p.stock));
                        Toast.makeText(dialogView.getContext(), " El producto "+data.name +" ha sido modificado ", Toast.LENGTH_LONG).show();
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
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    private String getIntegerQuantity(Double val){
        String[] arr=String.valueOf(val).split("\\.");
        int[] intArr=new int[2];
        intArr[0]=Integer.parseInt(arr[0]);
        intArr[1]=Integer.parseInt(arr[1]);

        if(intArr[1] == 0){
            return String.valueOf(intArr[0]);
        }else{
            return String.valueOf(val);
        }

    }

}