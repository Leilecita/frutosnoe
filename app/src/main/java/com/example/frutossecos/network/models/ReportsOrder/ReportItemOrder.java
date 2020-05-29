package com.example.frutossecos.network.models.ReportsOrder;

public class ReportItemOrder {
    public String name;
    public Double price;
    public Double quantity;
    public String price_type;

    public  ReportItemOrder(String fish_name,Double price,Double quantity, String price_type){
        this.name=fish_name;
        this.price=price;
        this.quantity=quantity;
        this.price_type=price_type;
    }
}
