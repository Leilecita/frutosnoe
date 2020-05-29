package com.example.frutossecos.network.models;

public class ItemOrder {

    public Long id;
    public Long product_id;
    public Long order_id;
    public Double quantity;
    public String created;
    public String product_name;
    public String price_type;
    public Double price;


    public ItemOrder(Long product_id, Long order_id, Double quantity,String product_name,String price_type,Double price){
        this.product_id=product_id;
        this.order_id=order_id;
        this.quantity=quantity;
        this.price_type=price_type;
        this.product_name=product_name;
        this.price=price;
    }
}
