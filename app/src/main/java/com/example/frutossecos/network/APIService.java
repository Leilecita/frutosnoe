package com.example.frutossecos.network;



import com.example.frutossecos.network.models.AmountProducts;
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

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import rx.Observable;

public interface APIService {

    @GET("orders.php")
    Observable<Response<List<SummaryDay>>>  getSummaryDay(@Query("method") String method, @Query("delivery_date") String date);

    @GET("orders.php")
    Observable<Response<ValuesDay>>  getValuesDay(@Query("method") String method, @Query("delivery_date") String date);

    @GET("orders.php")
    Observable<Response<List<ReportOrder>>>  getOrdersReportByUserIdByPage(@Query("page") Integer page, @Query("client_id") Long user_id);

    @GET("orders.php")
    Observable<Response<List<ReportOrder>>>  getAllOrdersByPage(@Query("method") String method,@Query("page") Integer page);


    @GET("orders.php")
    Observable<Response<Order>>  finishOrder(@Query("method") String method, @Query("order_id") Long order_id);

    @GET("orders.php")
    Observable<Response<Order>>  preparedOrder(@Query("method") String method, @Query("order_id") Long order_id);

    @GET("orders.php")
    Observable<Response<Order>>  send_account(@Query("method") String method,@Query("state") String state, @Query("order_id") Long order_id);

    @GET("orders.php")
    Observable<Response<Order>>  done_payment(@Query("method") String method,@Query("state") String state, @Query("order_id") Long order_id,@Query("debt_value") Double debt_value);

    @GET("orders.php")
    Observable<Response<ValuesOrderReport>>  getValuesOrdersReport(@Query("method") String method, @Query("delivery_date") String date);

    @GET("orders.php")
    Observable<Response<ValuesOrderReport>>  getTotalOrdersPendient(@Query("method") String method);


    @GET("orders.php")
    Observable<Response<List<ReportOrder>>>  searchOrders(@Query("method") String method, @Query("delivery_date") String date, @Query("query") String query);


    @GET("orders.php")
    Observable<Response<List<ReportOrder>>>  getOrdersByZoneAndTime(@Query("method") String method, @Query("delivery_date") String date, @Query("zone") String zone, @Query("time") String time);

    @GET("orders.php")
    Observable<Response<List<ReportOrder>>>  getOrders(@Query("method") String method,@Query("page") Integer page, @Query("delivery_date") String date, @Query("zone") String zone, @Query("time") String time, @Query("query") String query);


    @GET("orders.php")
    Observable<Response<Order>>  updatePriority(@Query("method") String method, @Query("order_id") Long order_id, @Query("priority") Integer priority);


    @GET("items_order.php")
    Observable<Response<AmountResult>>  getAmountByOrder(@Query("method") String method, @Query("order_id") Long order_id);
    //ORDERS

    @GET("orders.php")
    Observable<Response<Order>> getOrder(@Query("id") Long id);

    @POST("orders.php")
    Observable<Response<Order>> postOrder(@Body Order product);

    @PUT("orders.php")
    Observable<Response<Order>> putOrder(@Body Order product);

    @DELETE("orders.php")
    Observable<ResponseBody>  deleteOrder(@Query("id") Long id);

    @GET("clients.php")
    Observable<Response<ReportClient>> getClients(@Query("method") String method, @Query("page") Integer page, @Query("query") String query);

    @GET("clients.php")
    Observable<Response<Client>> getClient(@Query("id") Long id);

    @POST("clients.php")
    Observable<Response<Client>> postClient(@Body Client c);

    @PUT("clients.php")
    Observable<Response<Client>> putClient(@Body Client c);

    @DELETE("clients.php")
    Observable<ResponseBody>  deleteClient(@Query("id") Long id);

    //PRODUCTS

    @GET("products.php")
    Observable<Response<List<Product>>> getAliveProductsByPage( @Query("page") Integer page, @Query("state") String state, @Query("query") String query);

    @GET("products.php")
    Observable<Response<AmountProducts>> getTotProducts(@Query("method") String method);


    @GET("products.php")
    Observable<Response<Product>> getProduct(@Query("id") Long id);

    @POST("products.php")
    Observable<Response<Product>> postProduct(@Body Product product);

    @PUT("products.php")
    Observable<Response<Product>> putProduct(@Body Product product);

    @DELETE("products.php")
    Observable<ResponseBody>  deleteProduct(@Query("id") Long id);

    //zones
    @GET("zones.php")
    Observable<Response<List<Zone>>> getZones();

    @GET("zones.php")
    Observable<Response<List<Zone>>> getZonesByPage(@Query("page") Integer page);

    @GET("zones.php")
    Observable<Response<List<Zone>>> getAllZones(@Query("method") String method);

    @GET("zones.php")
    Observable<Response<Zone>> existZone(@Query("name") String name);

    @POST("zones.php")
    Observable<Response<Zone>> postZone(@Body Zone zone);

    @PUT("zones.php")
    Observable<Response<Zone>> putZone(@Body Zone zone);

    @DELETE("zones.php")
    Observable<ResponseBody>  deleteZone(@Query("id") Long id);

    //ITEMS_ORDER

    @GET("items_order.php")
    Observable<Response<List<ItemOrder>>> getItemsOrder();

    @GET("items_order.php")
    Observable<Response<List<ItemOrder>>> getItemsOrderByOrderId(@Query("order_id") Long id);

    @GET("items_order.php")
    Observable<Response<ItemOrder>> getItemOrder(@Query("id") Long id);

    @POST("items_order.php")
    Observable<Response<ItemOrder>> postItemOrder(@Body ItemOrder itemOrder);


    @PUT("items_order.php")
    Observable<Response<ItemOrder>> putItemOrder(@Body ItemOrder itemOrder);


    @DELETE("items_order.php")
    Observable<ResponseBody>  deleteItemOrder(@Query("id") Long id);
}
