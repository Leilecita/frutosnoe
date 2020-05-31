package com.example.frutossecos.network.models;

public class Order {

    public Long id;
    public Long client_id;
    public String delivery_date;
    public String delivery_time;
    public String defaulter;
    public String observation;
    public String state;
    public Integer priority;
    public String created;
    public String prepared;
    public Double debt_value;

    public Order(Long client_id, String delivery_date, String observation,String state,String delivery_time){
        this.client_id=client_id;
        this.delivery_date=delivery_date;
        this.observation=observation;
        this.state=state;
        this.delivery_time=delivery_time;
        this.defaulter="false";
        this.prepared="false";
    }
}
