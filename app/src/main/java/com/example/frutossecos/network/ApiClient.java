package com.example.frutossecos.network;


import android.util.Log;

import com.example.frutossecos.network.models.AmountResult;
import com.example.frutossecos.network.models.Client;
import com.example.frutossecos.network.models.ItemOrder;
import com.example.frutossecos.network.models.Order;
import com.example.frutossecos.network.models.Product;
import com.example.frutossecos.network.models.ReportClient;
import com.example.frutossecos.network.models.ReportsOrder.ReportOrder;
import com.example.frutossecos.network.models.ReportsOrder.SummaryDay;
import com.example.frutossecos.network.models.ReportsOrder.ValuesDay;
import com.example.frutossecos.network.models.ReportsOrder.ValuesOrderReport;
import com.example.frutossecos.network.models.Zone;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ApiClient {

    private static ApiClient INSTANCE = new ApiClient();

    private ApiClient(){}

    public static ApiClient get(){
        return INSTANCE;
    }

    public void getAmountByOrder(Long order_id,GenericCallback<AmountResult> callback){
        handleRequest( ApiUtils.getAPIService().getAmountByOrder("amount", order_id), callback);
    }

    public void finishOrder(Long order_id,GenericCallback<Order> callback){
        handleRequest( ApiUtils.getAPIService().finishOrder("finish", order_id), callback);
    }
    public void preparedOrder(Long order_id,GenericCallback<Order> callback){
        handleRequest( ApiUtils.getAPIService().preparedOrder("prepared", order_id), callback);
    }

    public void send_account(Long order_id,String state,GenericCallback<Order> callback){
        handleRequest( ApiUtils.getAPIService().send_account("send_account", state,order_id), callback);
    }

    public void done_payment(Long order_id,String state,Double debt_value,GenericCallback<Order> callback){
        handleRequest( ApiUtils.getAPIService().done_payment("done_payment",state, order_id,debt_value), callback);
    }


    public void getValuesOrderReport(String deliver_date,GenericCallback<ValuesOrderReport> callback){
        handleRequest( ApiUtils.getAPIService().getValuesOrdersReport("getOrdersValues", deliver_date), callback);
    }

    public void getTotalOrdersPendients(GenericCallback<ValuesOrderReport> callback){
        handleRequest( ApiUtils.getAPIService().getTotalOrdersPendient("getTotalOrdersPendient"), callback);
    }

    public void getOrdersReportByUserIdByPage(Integer page,Long user_id ,GenericCallback<List<ReportOrder>> callback){
        handleRequest( ApiUtils.getAPIService().getOrdersReportByUserIdByPage(page,user_id), callback);
    }


    public void getOrders(Integer page,String deliver_date,String zone,String time,String query,GenericCallback<List<ReportOrder>> callback){
        handleRequest( ApiUtils.getAPIService().getOrders("listAndSearchOrders",page, deliver_date,zone,time,query), callback);
    }

    public void getSummaryDay(String deliver_date,GenericCallback<List<SummaryDay>> callback){
        handleRequest( ApiUtils.getAPIService().getSummaryDay("summaryDay", deliver_date), callback);
    }

    public void getValuesDay(String deliver_date,GenericCallback<ValuesDay> callback){
        handleRequest( ApiUtils.getAPIService().getValuesDay("summaryDayValues", deliver_date), callback);
    }


    public void updatePriority(Long order_id,Integer priority,GenericCallback<Order> callback){
        handleRequest( ApiUtils.getAPIService().updatePriority("priority", order_id,priority), callback);
    }


    //ORDER

    public void getOrder(Long id,final GenericCallback<Order> callback){
        handleRequest( ApiUtils.getAPIService().getOrder(id), callback);
    }

    public void postOrder(Order order, GenericCallback<Order> callback){
        handleRequest( ApiUtils.getAPIService().postOrder(order), callback);
    }

    public void putOrder(Order order,GenericCallback<Order> callback){
        handleRequest( ApiUtils.getAPIService().putOrder(order), callback);
    }

    public void deleteOrder(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPIService().deleteOrder(id), callback);
    }

    public void getClients(String query, Integer page, final GenericCallback<ReportClient> callback ){
        handleRequest( ApiUtils.getAPIService().getClients("getClients",page,query), callback);
    }

    public void postClient(Client c,GenericCallback<Client> callback){
        handleRequest( ApiUtils.getAPIService().postClient(c), callback);
    }

    public void putClient(Client c,GenericCallback<Client> callback){
        handleRequest( ApiUtils.getAPIService().putClient(c), callback);
    }

    public void deleteClient(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPIService().deleteClient(id), callback);
    }

    //PRODUCTS

    public void getAliveProductsByPage(Integer page,String state,String query,final GenericCallback<List<Product>> callback){
        handleRequest( ApiUtils.getAPIService().getAliveProductsByPage(page,state,query), callback);
    }

    public void getProduct(Long id,final GenericCallback<Product> callback){
        handleRequest( ApiUtils.getAPIService().getProduct(id), callback);
    }


    public void postProduct(Product product,GenericCallback<Product> callback){
        handleRequest( ApiUtils.getAPIService().postProduct(product), callback);
    }

    public void putProduct(Product product,GenericCallback<Product> callback){
        handleRequest( ApiUtils.getAPIService().putProduct(product), callback);
    }

    public void deleteProduct(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPIService().deleteProduct(id), callback);
    }

    //zones
    public void getZones(final GenericCallback<List<Zone>> callback){
        handleRequest( ApiUtils.getAPIService().getZones(), callback);
    }

    public void getZonesByPage(Integer page,final GenericCallback<List<Zone>> callback){
        handleRequest( ApiUtils.getAPIService().getZonesByPage(page), callback);
    }

    public void getAllZones(final GenericCallback<List<Zone>> callback){
        handleRequest( ApiUtils.getAPIService().getAllZones("getZones"), callback);
    }

    public void existZone(String name, final GenericCallback<Zone> callback){
        handleRequest( ApiUtils.getAPIService().existZone(name), callback);
    }

    public void postZone(Zone zone,GenericCallback<Zone> callback){
        handleRequest( ApiUtils.getAPIService().postZone(zone), callback);
    }

    public void deleteZone(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPIService().deleteZone(id), callback);
    }

    public void putZone(Zone n,GenericCallback<Zone> callback){
        handleRequest( ApiUtils.getAPIService().putZone(n), callback);
    }


    //ITEMS_ORDER

    public void getItemsOrder(final GenericCallback<List<ItemOrder>> callback){
        handleRequest( ApiUtils.getAPIService().getItemsOrder(), callback);
    }

    public void getItemsOrderByOrderId(Long orderId, final GenericCallback<List<ItemOrder>> callback){
        handleRequest( ApiUtils.getAPIService().getItemsOrderByOrderId(orderId), callback);
    }

    public void postItemOrder(ItemOrder itemOrder,GenericCallback<ItemOrder> callback){
        handleRequest( ApiUtils.getAPIService().postItemOrder(itemOrder), callback);
    }


    public void deleteItemOrder(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPIService().deleteItemOrder(id), callback);
    }



    private <T> void handleRequest(Observable<Response<T>> request, final GenericCallback<T> callback){
        request.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<T>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Error error = new Error();
                        error.result = "error";
                        error.message = "Generic Error";
                        e.printStackTrace();
                        if( e instanceof HttpException){
                            try {
                                String body = ((HttpException) e).response().errorBody().string();
                                Gson gson = new Gson();
                                error =  gson.fromJson(body,Error.class);
                            }catch (Exception e1){
                                e1.printStackTrace();
                            }
                        }
                        callback.onError(error);
                    }

                    @Override
                    public void onNext(Response<T>  response) {
                        callback.onSuccess(response.data);
                    }
                });
    }

    private void handleDeleteRequest(Observable<ResponseBody> request, final GenericCallback<Void> callback){
        request.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Error error = new Error();
                        error.result = "error";
                        error.message = "Generic Error";
                        e.printStackTrace();
                        Log.e("RETRO", e.getMessage());
                        if( e instanceof HttpException){
                            try {
                                String body = ((HttpException) e).response().errorBody().string();
                                Gson gson = new Gson();
                                error =  gson.fromJson(body,Error.class);
                                Log.e("RETRO", body);
                            }catch (Exception e1){
                                e1.printStackTrace();
                            }
                        }
                        callback.onError(error);
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        callback.onSuccess(null);
                    }
                });
    }


}
