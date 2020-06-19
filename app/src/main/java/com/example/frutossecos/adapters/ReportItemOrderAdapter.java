package com.example.frutossecos.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frutossecos.R;
import com.example.frutossecos.ValuesHelper;
import com.example.frutossecos.network.models.ReportsOrder.ReportItemOrder;
import com.example.frutossecos.types.Constants;

import java.util.List;

public class ReportItemOrderAdapter extends BaseAdapter<ReportItemOrder,ReportItemOrderAdapter.ViewHolder> {

    private Context mContext;

    public ReportItemOrderAdapter(Context context, List<ReportItemOrder> items){
        setItems(items);
        mContext = context;
    }

    public ReportItemOrderAdapter(){

    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        public TextView name;
        public TextView type;
        public TextView cant;
        public TextView amount;
        public TextView price;

        public ViewHolder(View v){
            super(v);
            name= v.findViewById(R.id.name_product);
            cant= v.findViewById(R.id.cant);
            type= v.findViewById(R.id.type);
            amount= v.findViewById(R.id.total_amount);
            price= v.findViewById(R.id.price);
        }
    }

    @Override
    public ReportItemOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_order_simple,parent,false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    private void clearViewHolder(ReportItemOrderAdapter.ViewHolder vh){
        if(vh.name!=null)
            vh.name.setText(null);
        if(vh.cant!=null)
            vh.cant.setText(null);
        if(vh.amount!=null)
            vh.amount.setText(null);
        if(vh.type!=null)
            vh.type.setText(null);
        if(vh.price!=null)
            vh.price.setText(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        clearViewHolder(holder);

        final ReportItemOrder currentItem=getItem(position);

        holder.name.setText(currentItem.name);
        holder.cant.setText(ValuesHelper.get().getIntegerQuantityRounded(currentItem.quantity));
        holder.price.setText(ValuesHelper.get().getIntegerQuantityRounded(currentItem.price));

        if(currentItem.price_type.equals(Constants.TYPE_PRICE_MED_KG)){
            holder.type.setText("1/2kg");
        }else if(currentItem.price_type.equals(Constants.TYPE_PRICE_CUARTER_KG)){
            holder.type.setText("1/4kg");
        }else{
            holder.type.setText("1kg");
        }



        holder.amount.setText(String.valueOf(round(currentItem.price*currentItem.quantity,2)));
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        System.out.println(String.valueOf((double) tmp / factor));
        return (double) tmp / factor;
    }


}

