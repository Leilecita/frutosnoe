package com.example.frutossecos.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.frutossecos.R;
import com.example.frutossecos.activities.CreateOrderActivity;
import com.example.frutossecos.activities.UserHistoryOrdersActivity;
import com.example.frutossecos.network.ApiClient;
import com.example.frutossecos.network.Error;
import com.example.frutossecos.network.GenericCallback;
import com.example.frutossecos.network.models.Client;
import com.example.frutossecos.network.models.Zone;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClientAdapter extends BaseAdapter<Client,ClientAdapter.ViewHolder> {

    private Context mContext;
    private ArrayAdapter<String> adapter;
    private boolean validateNeigh;

    List<String> listColor=new ArrayList<>();
    Random random;



    public ClientAdapter(Context context, List<Client> users) {
        setItems(users);
        mContext = context;
        validateNeigh = false;

        listColor.add("#AED581");
        listColor.add("#E6EE9C");
        listColor.add("#C5E1A5");
        listColor.add("#9CCC65");
        listColor.add("#8BC34A");
        listColor.add("#558B2F");
        random=new Random();

    }


    public ClientAdapter() {

    }

    public List<Client> getListUser() {
        return getList();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text_name;
        public ImageView create;
        public ImageView photo;
        public ImageView note;
        public ImageView debt;
        public TextView debt_value;


        public ViewHolder(View v) {
            super(v);
            text_name = v.findViewById(R.id.text_name);
            create = v.findViewById(R.id.create_order);
            photo = v.findViewById(R.id.photo_user);
            note = v.findViewById(R.id.note);
            debt = v.findViewById(R.id.debt);
            debt_value = v.findViewById(R.id.debt_value);
        }
    }

    @Override
    public ClientAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new View
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_item_client, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    private void clearViewHolder(ClientAdapter.ViewHolder vh) {
        if (vh.text_name != null)
            vh.text_name.setText(null);
        if (vh.create != null) {
            //vh.create.setImageResource(android.R.color.transparent);
           // vh.create.setBorderWidth(0);
           // vh.create.setBorderColor(mContext.getResources().getColor(R.color.white));
        }
        if (vh.note != null)
            vh.note.setImageResource(android.R.color.transparent);
        if (vh.debt != null)
            vh.debt.setImageResource(android.R.color.transparent);
        if (vh.debt_value != null)
            vh.debt_value.setText(null);

    }



    private Drawable getDrawableFirstLetter(Client user) {

        //get first letter of each String item
        String firstLetter = String.valueOf(user.name.charAt(0));
        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color

       // int color = generator.getColor(user);
        String color = listColor.get(random.nextInt(listColor.size()));


        TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .width(100)
                .height(100)
                .endConfig()
                .buildRound(firstLetter, Color.parseColor(color));
        return drawable;
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        clearViewHolder(holder);

        final Client currentUser = getItem(position);

        if (currentUser.pendient_orders > 0) {
            holder.create.setColorFilter(mContext.getResources().getColor(R.color.word_clear));
            holder.create.setImageResource(R.drawable.prepare);

           // holder.create.setBorderWidth(4);
            //holder.note.setImageResource(R.drawable.prueba3);
            //holder.note.setVisibility(View.VISIBLE);
            //holder.create.setBorderColor(mContext.getResources().getColor(R.color.FishyLetra));
            // holder.create.setImageResource(R.drawable.fishy_santi2);
           // Glide.with(mContext).load(R.drawable.fishy_santi2).into(holder.create);
        } else {
            holder.create.setColorFilter(mContext.getResources().getColor(R.color.word_clear));
            holder.create.setImageResource(R.drawable.addnewsan);

          //  holder.note.setVisibility(View.GONE);
            // holder.create.setImageResource(R.drawable.fishy_santi);
          //  Glide.with(mContext).load(R.drawable.fishy_santi).into(holder.create);
        }


        holder.photo.setImageDrawable(getDrawableFirstLetter(currentUser));

       // holder.create.setColorFilter(mContext.getResources().getColor(R.color.colorPrimaryDark));
        holder.text_name.setText(currentUser.name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createInfoDialog(currentUser, position);
            }
        });


        holder.create.setColorFilter(mContext.getResources().getColor(R.color.word_clear));
        holder.create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startCreateOrderActivity(currentUser);
            }
        });

        if (currentUser.defaulter.equals("true")) {
         /*   holder.debt.setImageDrawable(mContext.getResources().getDrawable(R.drawable.money));
            holder.debt_value.setVisibility(View.VISIBLE);
            holder.debt_value.setText("$"+String.valueOf(currentUser.debt_value));
            */
        } else {
            holder.debt_value.setVisibility(View.GONE);
        }


    }

    private void startCreateOrderActivity(Client c){
        CreateOrderActivity.start(mContext,c);
    }

    private void startClientHistoryActivity(Client c){
        UserHistoryOrdersActivity.start(mContext,c);
    }

    private void createInfoDialog(final Client u, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_client_information, null);

        builder.setView(dialogView);

        final TextView name=  dialogView.findViewById(R.id.user_name);
        final TextView address=  dialogView.findViewById(R.id.user_address);
        final TextView neighbor=  dialogView.findViewById(R.id.user_neighboor);
        final TextView phone=  dialogView.findViewById(R.id.user_phone);
        final TextView inst=  dialogView.findViewById(R.id.user_inst);
        final TextView face=  dialogView.findViewById(R.id.user_face);
        final ImageView delete=  dialogView.findViewById(R.id.deleteuser);
        final ImageView edituser=  dialogView.findViewById(R.id.edituser);
        final ImageView history=  dialogView.findViewById(R.id.historyuser);
        final ImageView call=  dialogView.findViewById(R.id.phone);

        name.setText(u.name);
        address.setText(u.address);
        phone.setText(u.phone);
        neighbor.setText(u.zone);
        inst.setText(u.instagram);
        face.setText(u.facebook);

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + u.phone));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        final AlertDialog dialog = builder.create();
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startClientHistoryActivity(u);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser(u,position);
                dialog.dismiss();
            }
        });

        edituser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edithUser(u,position, new OnUserEditedCallback() {
                    @Override
                    public void onUserEdited(Client newUser) {
                        name.setText(newUser.name);
                        address.setText(newUser.address);
                        phone.setText(newUser.phone);
                        neighbor.setText(newUser.zone);
                        inst.setText(newUser.instagram);
                        face.setText(newUser.facebook);
                    }
                });

            }
        });

        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    private void deleteUser( final Client u,final int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View dialogView = inflater.inflate(R.layout.dialog_delete_client, null);
        builder.setView(dialogView);

        final TextView name=  dialogView.findViewById(R.id.user_name);
        final TextView address=  dialogView.findViewById(R.id.user_address);
        final TextView phone=  dialogView.findViewById(R.id.user_phone);
        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final Button ok=  dialogView.findViewById(R.id.ok);

        name.setText(u.name);
        phone.setText(u.phone);
        address.setText(u.address);

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                ApiClient.get().deleteClient(u.id, new GenericCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        Toast.makeText(dialogView.getContext(), "El cliente "+u.name+" ha sido eliminado.", Toast.LENGTH_LONG).show();
                        removeItem(position);
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

    private void edithUser(final Client userToEdith, final int position, final OnUserEditedCallback callback){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_edith_client, null);

        builder.setView(dialogView);

        final EditText nameEdith=dialogView.findViewById(R.id.edith_name);
        final EditText addressEdith=dialogView.findViewById(R.id.edith_address);
        final EditText phoneEdith=dialogView.findViewById(R.id.edith_phone);
        final EditText inst=dialogView.findViewById(R.id.edith_inst);
        final EditText face=dialogView.findViewById(R.id.edith_face);
        final AutoCompleteTextView neighborEdith=dialogView.findViewById(R.id.edith_neighborhood);


        phoneEdith.setText(userToEdith.phone);
        addressEdith.setText(userToEdith.address);
        nameEdith.setText(userToEdith.name);
        neighborEdith.setHint(userToEdith.zone);
        inst.setText(userToEdith.instagram);
        face.setText(userToEdith.facebook);

        phoneEdith.setHint("-");
        addressEdith.setHint("-");
        nameEdith.setHint("-");
        inst.setHint("-");
        face.setHint("-");
       // neighborEdith.setHint("zona");

        listNeighborhoods(neighborEdith);

        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final Button ok=  dialogView.findViewById(R.id.ok);

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                neighborEdith.clearFocus();

                final String nameNew= nameEdith.getText().toString().trim();
                String addressNew= addressEdith.getText().toString().trim();
                String phoneNew= phoneEdith.getText().toString().trim();
                String neighborNew= neighborEdith.getText().toString().trim();
                String instaNew= inst.getText().toString().trim();
                String faceNew= face.getText().toString().trim();

                userToEdith.name=nameNew;
                userToEdith.address=addressNew;
                userToEdith.phone=phoneNew;
                userToEdith.instagram=instaNew;
                userToEdith.facebook=faceNew;
                if(!neighborNew.matches("")){
                    userToEdith.zone=neighborNew;
                }


                if(!validateNeigh && !neighborNew.matches("") ){
                    Toast.makeText(mContext, "El barrio es inválido",Toast.LENGTH_LONG).show();

                }else{

                    ApiClient.get().putClient(userToEdith, new GenericCallback<Client>() {
                        @Override
                        public void onSuccess(Client data) {
                            notifyItemChanged(position);
                            userToEdith.zone=data.zone;

                            Toast.makeText(mContext, "El usuario ha sido editado",Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onError(Error error) {
                        }
                    });

                    dialog.dismiss();
                    callback.onUserEdited(userToEdith);

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

    interface OnUserEditedCallback {
        void onUserEdited(Client user);
    }

    private void listNeighborhoods(final AutoCompleteTextView neig){

        ApiClient.get().getZones(new GenericCallback<List<Zone>>() {
            @Override
            public void onSuccess(List<Zone> data) {
                final List<String> listneighborhoods=createArray(data);
                adapter = new ArrayAdapter<String>(mContext,
                        android.R.layout.simple_dropdown_item_1line, listneighborhoods);
                neig.setThreshold(1);
                neig.setAdapter(adapter);
                neig.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (!b) {
                            String val = neig.getText() + "";

                            if(listneighborhoods.contains(val)){
                                validateNeigh=true;

                            }else{
                                validateNeigh=false;
                                neig.setError("Barrio inválido");
                            }
                        }else{
                        }
                    }
                });
            }

            @Override
            public void onError(Error error) {
            }
        });
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


}
