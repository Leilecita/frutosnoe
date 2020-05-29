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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frutossecos.R;
import com.example.frutossecos.network.ApiClient;
import com.example.frutossecos.network.Error;
import com.example.frutossecos.network.GenericCallback;
import com.example.frutossecos.network.models.Zone;

import java.util.List;

public class ZoneAdapter extends BaseAdapter<Zone,ZoneAdapter.ViewHolder> {

    private Context mContext;
    private boolean isChecked;
    private Integer posChecked;

    public ZoneAdapter(Context context, List<Zone> neighborhoods) {
        setItems(neighborhoods);
        mContext = context;
        isChecked=false;
        posChecked=null;
    }

    public ZoneAdapter() {

    }

    public Integer getPosChecked(){
        return posChecked;
    }
    public boolean isChecked(){
        return isChecked;
    }

    public List<Zone> getList() {
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView options;
        public CheckBox select;


        public ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            options = v.findViewById(R.id.options);
            select = v.findViewById(R.id.checkbox);
        }
    }

    @Override
    public ZoneAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.zone_cardview, parent, false);
        ZoneAdapter.ViewHolder vh = new ZoneAdapter.ViewHolder(v);

        return vh;
    }

    private void clearViewHolder(ZoneAdapter.ViewHolder vh) {
        if (vh.name != null)
            vh.name.setText(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ZoneAdapter.ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final Zone currentNeigh = getItem(position);
        holder.select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isChecked){
                    holder.select.setChecked(true);
                    isChecked=true;
                    posChecked=position;
                }else if (position==posChecked){
                    holder.select.setChecked(false);
                    isChecked=false;
                    posChecked=null;
                }else if(isChecked && position!=posChecked){
                    holder.select.setChecked(false);
                }
            }
        });


        holder.name.setText(currentNeigh.name);
        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(mContext, holder.options);
                popup.getMenuInflater().inflate(R.menu.menu_products, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_delete:
                                deleteZone(currentNeigh, position);
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

    private void deleteZone(final Zone n, final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_delete_zone, null);
        builder.setView(dialogView);
        final TextView name = dialogView.findViewById(R.id.name);
        final TextView cancel = dialogView.findViewById(R.id.cancel);
        final Button ok = dialogView.findViewById(R.id.ok);

        name.setText(n.name);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiClient.get().deleteZone(n.id, new GenericCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        removeItem(position);
                        Toast.makeText(mContext, "Se ha eliminado el barrio " + name.getText().toString(), Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(mContext, "Error al eliminar barrio " + name.getText().toString(), Toast.LENGTH_LONG).show();
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