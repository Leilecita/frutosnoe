package com.example.frutossecos.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frutossecos.DateHelper;
import com.example.frutossecos.DownloadTask;
import com.example.frutossecos.Events.EventListUsersState;
import com.example.frutossecos.Events.EventOrderState;
import com.example.frutossecos.R;
import com.example.frutossecos.activities.CreateOrderActivity;
import com.example.frutossecos.interfaces.ItemTouchHelperAdapter;
import com.example.frutossecos.interfaces.ItemTouchHelperViewHolder;
import com.example.frutossecos.interfaces.OnStartDragListener;
import com.example.frutossecos.interfaces.OrderFragmentListener;
import com.example.frutossecos.network.ApiClient;
import com.example.frutossecos.network.ApiUtils;
import com.example.frutossecos.network.Error;
import com.example.frutossecos.network.GenericCallback;
import com.example.frutossecos.network.models.Order;
import com.example.frutossecos.network.models.ReportsOrder.ReportItemOrder;
import com.example.frutossecos.network.models.ReportsOrder.ReportOrder;

import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ReportOrderAdapter extends BaseAdapter<ReportOrder,ReportOrderAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private Context mContext;
    private boolean mOnlyAdress;
    private boolean mHistoryuser;
    private OnStartDragListener mDragStartListener;

    private OrderFragmentListener onOrderFragmentLister= null;

    public void setOnOrderFragmentLister(OrderFragmentListener lister){
        onOrderFragmentLister=lister;
    }

    public ReportOrderAdapter(Context context, List<ReportOrder> orders, OnStartDragListener dragListner){
        setItems(orders);
        mContext = context;
        mDragStartListener = dragListner;
        mOnlyAdress=false;
        mHistoryuser=false;
    }

    public ReportOrderAdapter(){

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        public TextView name;
        public TextView address;
        public TextView horario;
        public TextView amount;
        public ImageView listItemsOrder;
        public ImageView phone;
        public ImageView prepared;
        public ImageView money;
        public ImageView mens;
        public TextView state;
        public TextView neighborhood;
        public TextView priority;
        public ImageView options;
        public ImageView stateImage;
        public CardView cardView;

        public TextView time;
        public TextView debt_value;

        public ViewHolder(View v){
            super(v);
            name= v.findViewById(R.id.user_name);
            address= v.findViewById(R.id.user_address);
            amount= v.findViewById(R.id.amount_order);
            listItemsOrder= v.findViewById(R.id.list);
            horario= v.findViewById(R.id.horario);
            prepared= v.findViewById(R.id.prepared);
            money= v.findViewById(R.id.money);
            phone= v.findViewById(R.id.phone);
            mens= v.findViewById(R.id.mens);
            state= v.findViewById(R.id.state);
            neighborhood= v.findViewById(R.id.neighborhood);
            stateImage= v.findViewById(R.id.stateImage);
            options= v.findViewById(R.id.options);
            priority= v.findViewById(R.id.priority);
            time=v.findViewById(R.id.time);
            cardView=v.findViewById(R.id.card_view);
            debt_value=v.findViewById(R.id.debt_value);

        }

        @Override
        public void onItemSelected() {
            itemView.setAlpha(0.7f);
        }

        @Override
        public void onItemClear() {
            itemView.setAlpha(1f);
        }
    }


    @Override
    public ReportOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        // Create a new View
        View v;
        if(mOnlyAdress){
            v = LayoutInflater.from(mContext).inflate(R.layout.car_item_address,parent,false);
        }else{
            v = LayoutInflater.from(mContext).inflate(R.layout.card_item_order2,parent,false);

        }
        //  View v = LayoutInflater.from(mContext).inflate(R.layout.card_item_order,parent,false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    public void setOnlyAddress(boolean val){
        this.mOnlyAdress=val;
    }


    public void setHistoryUser(boolean val){
        this.mHistoryuser=val;

    }
    public boolean getOnlyAddress(){return this.mOnlyAdress;}

    @Override
    public boolean isDragEnabled(){return getOnlyAddress();}

    @Override
    public void onItemDismiss(int position) {
        //  removeItem(position);
        // notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {

        ReportOrder repOrder= getList().get(fromPosition);
        ReportOrder newOrder= repOrder;

        getList().remove(fromPosition);
        getList().add(toPosition,newOrder);
        notifyItemMoved(fromPosition,toPosition);

        savepriority(newOrder.order_id,toPosition);
        savepriority(getList().get(fromPosition).order_id,fromPosition);


        return true;
    }



    private void savepriority(Long order_id, Integer position){

        ApiClient.get().updatePriority(order_id, position, new GenericCallback<Order>() {
            @Override
            public void onSuccess(Order data) {

            }

            @Override
            public void onError(Error error) {

            }
        });
    }

    private void clearViewHolder(ReportOrderAdapter.ViewHolder vh){

        if(vh.address!=null)
            vh.address.setText(null);
        if(vh.priority!=null)
            vh.priority.setText(null);
        if(!mOnlyAdress){
            if(vh.amount!=null)
                vh.amount.setText(null);
            if(vh.name!=null)
                vh.name.setText(null);
            if(vh.horario!=null)
                vh.horario.setText(null);
            if(vh.state!=null)
                vh.state.setText(null);

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        clearViewHolder(holder);
        final ReportOrder r= getItem(position);

        holder.address.setText(r.address);

        if(r.state.equals("pendiente")){
            holder.stateImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.pendien_dor_dark));
            holder.stateImage.setColorFilter(mContext.getResources().getColor(R.color.colorPrimaryDark));
        }else if (r.state.equals("entregado")){
            holder.stateImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.done_doble));
            holder.stateImage.setColorFilter(mContext.getResources().getColor(R.color.colorPrimaryDark));
        }else{

        }

        if(!r.delivery_time.equals("Horario")){
            holder.horario.setText(r.delivery_time);
        }

        if(!mOnlyAdress){

            if(r.debt_value != null){
                if (Double.compare(r.debt_value, 0.0) > 0) {

                    holder.debt_value.setVisibility(View.VISIBLE);
                    holder.debt_value.setText(String.valueOf(r.debt_value));
                }else{
                    holder.debt_value.setVisibility(View.GONE);
                }
            }


            if(r.prepared.equals("true")){
                holder.prepared.setImageDrawable(mContext.getResources().getDrawable(R.drawable.prepare));
                holder.prepared.setColorFilter(mContext.getResources().getColor(R.color.colorPrimaryDark));
            }else{
                holder.prepared.setImageDrawable(mContext.getResources().getDrawable(R.drawable.topreapre));
                holder.prepared.setColorFilter(mContext.getResources().getColor(R.color.colorPrimaryDark));
            }

            holder.prepared.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //showDialogFactura(r,position);
                }
            });

           /* if(r.send_account.equals("true")){
                holder.factura.setImageDrawable(mContext.getResources().getDrawable(R.drawable.factura));
            }else{
                holder.factura.setImageDrawable(mContext.getResources().getDrawable(R.drawable.factura_dor));
            }*/

            if(r.defaulter.equals("true")){
                holder.money.setImageDrawable(mContext.getResources().getDrawable(R.drawable.money_roj));
            }else{
                holder.money.setImageDrawable(mContext.getResources().getDrawable(R.drawable.money_dor));
            }
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    showOrderInfo(r,position);
                    return false;
                }
            });


            holder.money.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialogMoney(r,position);
                }
            });
        }

        if(mOnlyAdress){
            holder.neighborhood.setText(r.zone);
            holder.time.setText(r.order_obs);
            holder.priority.setText(String.valueOf(r.priority));
            holder.itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getActionMasked() ==  MotionEvent.ACTION_BUTTON_PRESS) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return false;
                }
            });
        }else{
            if(r.order_obs!=null)
                holder.priority.setText(r.order_obs);

            holder.name.setText(r.name);
            if(r.total_amount != null)
            holder.amount.setText(String.valueOf(round(r.total_amount,2)));
            holder.listItemsOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showItemsList(r);
                }
            });
            holder.phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + r.phone));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });
            holder.mens.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendWhatsapp("hola",r.phone);
                }
            });
            holder.options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popup = new PopupMenu(mContext, holder.options);
                    popup.getMenuInflater().inflate(R.menu.menu_report_order, popup.getMenu());

                    Menu menuOpts = popup.getMenu();

                    if(r.state.equals("pendiente")){
                        menuOpts.getItem(0).setTitle("Entregado");
                    }else if(r.state.equals("entregado")){
                        menuOpts.getItem(0).setTitle("No entregado");
                    }

                    if(r.prepared.equals("true")){
                        menuOpts.getItem(1).setTitle("No preparado");
                    }else{
                        menuOpts.getItem(1).setTitle("Preparado");
                    }


                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.menu_edit:
                                    finishOrder(r,position);
                                    return true;
                                case R.id.menu_prepare:
                                    preparedOrder(r,position);
                                    return true;
                                case R.id.download:


                                    downloadPDF(r.order_id,r.name,r.phone);
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
        if(mHistoryuser){
            holder.priority.setText(DateHelper.get().getOnlyDate(r.delivery_date));
        }
    }



    private void finishOrder(final ReportOrder r, final Integer position){

        ApiClient.get().finishOrder(r.order_id, new GenericCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                r.state=data.state;
                updateItem(position,r);

                EventBus.getDefault().post(new EventListUsersState());

                EventBus.getDefault().post(new EventOrderState(data.id,"finish",r.delivery_date));

                if(onOrderFragmentLister!=null){
                    onOrderFragmentLister.refreshPendientOrders();
                }
            }

            @Override
            public void onError(Error error) {
            }
        });

    }

    private void preparedOrder(final ReportOrder r, final Integer position){

        ApiClient.get().preparedOrder(r.order_id, new GenericCallback<Order>() {
            @Override
            public void onSuccess(Order data) {
                System.out.println(data.prepared);
                r.prepared=data.prepared;
                updateItem(position,r);


                //EventBus.getDefault().post(new EventOrderState(data.id,"finish",r.delivery_date));
               /* if(onOrderFragmentLister!=null){
                    onOrderFragmentLister.refreshPendientOrders();
                }*/
            }

            @Override
            public void onError(Error error) {
                System.out.println("acaaaaaaaaaaa");
            }
        });


    }
    private void deleteOrder(final ReportOrder r, final Integer position){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_delete_order, null);
        builder.setView(dialogView);

        final TextView name=  dialogView.findViewById(R.id.user_name);
        final TextView delivery=  dialogView.findViewById(R.id.deliver_date);
        final TextView amount=  dialogView.findViewById(R.id.total_amount);
        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final Button ok=  dialogView.findViewById(R.id.ok);

        name.setText(r.name);
        delivery.setText(r.delivery_date);
        amount.setText(String.valueOf(r.total_amount));

        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ApiClient.get().deleteOrder(r.order_id, new GenericCallback<Void>() {
                    @Override
                    public void onSuccess(Void data) {
                        removeItem(position);
                        if(onOrderFragmentLister!=null){
                            onOrderFragmentLister.refreshPendientOrders();
                        }

                        EventBus.getDefault().post(new EventOrderState(r.client_id,"deleted",r.delivery_date));

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


    private void showOrderInfo(final ReportOrder r,final Integer position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_order_information, null);
        builder.setView(dialogView);
        final TextView name=  dialogView.findViewById(R.id.user_name);
        final TextView zone=  dialogView.findViewById(R.id.user_neighboor);
        final TextView delivery_date=  dialogView.findViewById(R.id.deliver_date);
        final TextView created=  dialogView.findViewById(R.id.created_date);
        final ImageView deleteOrder=  dialogView.findViewById(R.id.deleteuser);

        name.setText(r.name);
        zone.setText(r.zone);
        delivery_date.setText(r.delivery_date);
        created.setText(serverToUserFormatted(r.order_created));

        final AlertDialog dialog = builder.create();

        deleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                deleteOrder(r,position);
            }
        });
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    private void startEdithOrderActivity(ReportOrder reportorder){
        CreateOrderActivity.startEdithOrder(mContext,reportorder);
    }

    private void showItemsList(final ReportOrder r){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_show_items, null);
        builder.setView(dialogView);
        final TextView name=  dialogView.findViewById(R.id.user_name);

        RecyclerView recycler= dialogView.findViewById(R.id.list_items);
        ReportItemOrderAdapter adapter= new ReportItemOrderAdapter(mContext,new ArrayList<ReportItemOrder>());
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(mContext);
        recycler.setLayoutManager(layoutManager);

        recycler.setAdapter(adapter);
        adapter.setItems(r.items);
        TextView amount= dialogView.findViewById(R.id.amount);
        amount.setText(String.valueOf(round(r.total_amount,2)));
        name.setText(r.name);

        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final Button ok=  dialogView.findViewById(R.id.ok);
        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startEdithOrderActivity(r);
            }
        });
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    private String generateText(ArrayList<String> list){
        StringBuffer cadena = new StringBuffer();
        for (int x=0;x<list.size();x++){
            cadena =cadena.append(list.get(x));
        }
        return cadena.toString();
    }




    private void sendWhatsapp(String text, String phone){


        Uri uri = Uri.parse("smsto:" + phone);
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        i.putExtra(Intent.EXTRA_TEXT,text);
        //i.setType("application/pdf");
        mContext.startActivity(Intent.createChooser(i, ""));
    }

    public String serverToUserFormatted(String date){

        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            format1.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date1 = format1.parse(date);
            format1.setTimeZone(TimeZone.getDefault());
            return changeFormatDate(format1.format(date1));
        }catch (ParseException e){

        }
        return "";
    }


    public void downloadPDF(Long orderId,String name,String phone)
    {
        // String URL= "http://192.168.0.14/fishyserver/orders.php?method=generatePdf";
        String URL= ApiUtils.BASE_URL + "orders.php?method=generatePdf&order_id="+orderId;
        // new DownloadFile().execute(URL, "maven.pdf");
        //String URL= "http://localhost/fishyserver/pdfs/salida.pdf";
        new DownloadTask(mContext, URL,"Order-"+name+".pdf",phone);

    }


    public String changeFormatDate(String date){
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date1 = format1.parse(date);
            SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            String stringdate2 = format2.format(date1);
            return stringdate2;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "dd/MM/yyyy";
    }

   /* private void showDialogFactura(final ReportOrder r,final Integer pos){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_factura, null);
        builder.setView(dialogView);

        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final TextView text=  dialogView.findViewById(R.id.text);
        final TextView title=  dialogView.findViewById(R.id.title);
        final Button ok=  dialogView.findViewById(R.id.ok);
        text.setText("¿Se ha entregado la factura?");
        title.setText("FACTURA");

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ApiClient.get().send_account(r.order_id, "true", new GenericCallback<Order>() {
                    @Override
                    public void onSuccess(Order data) {
                        r.send_account="true";
                        updateItem(pos,r);
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
                ApiClient.get().send_account(r.order_id, "false", new GenericCallback<Order>() {
                    @Override
                    public void onSuccess(Order data) {
                        r.send_account="false";
                        updateItem(pos,r);
                    }

                    @Override
                    public void onError(Error error) {

                    }
                });
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    }
*/
    private void showDialogMoney(final ReportOrder r,final Integer pos){

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_defaulter, null);
        builder.setView(dialogView);

        final TextView cancel=  dialogView.findViewById(R.id.cancel);
        final TextView text=  dialogView.findViewById(R.id.text);
        final TextView title=  dialogView.findViewById(R.id.title);

        final EditText debt_value=  dialogView.findViewById(R.id.debt_value);
        final Button ok=  dialogView.findViewById(R.id.ok);

        text.setText("¿Adeuda el pago?");
        title.setText("PAGO");

        debt_value.setHint(String.valueOf(r.debt_value));

        final AlertDialog dialog = builder.create();


        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {


                if (!debt_value.getText().toString().trim().equals("")) {

                    final Double val = Double.valueOf(debt_value.getText().toString().trim());

                    ApiClient.get().done_payment(r.order_id, "true", val, new GenericCallback<Order>() {
                        @Override
                        public void onSuccess(Order data) {
                            r.defaulter = "true";
                            r.debt_value=data.debt_value;
                            updateItem(pos, r);

                            EventBus.getDefault().post(new EventListUsersState());

                        }

                        @Override
                        public void onError(Error error) {
                        }
                    });
                    dialog.dismiss();

                } else {
                    Toast.makeText(mContext, "Tipo de dato inválido", Toast.LENGTH_SHORT).show();
                }
            }
        });



        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ApiClient.get().done_payment(r.order_id, "false",0.0, new GenericCallback<Order>() {
                    @Override
                    public void onSuccess(Order data) {
                        r.defaulter="false";
                        r.debt_value=data.debt_value;
                        updateItem(pos,r);
                    }
                    @Override
                    public void onError(Error error) {
                       // DialogHelper.get().showMessage("Error", "No se pudo realizar el movimiento",mContext);
                    }
                });
                dialog.dismiss();
            }
        });
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}
