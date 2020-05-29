package com.example.frutossecos.network.models;

public class Client {
    public Long id;
    public String name;
    public String address;
    public String phone;
    public String zone;
    public Integer pendient_orders;
    public String created;
    public String defaulter;
    public String facebook;
    public String instagram;

    public Double debt_value;



    public Client(String name, String address, String phone,String facebook ,String instragram, String zone,Integer pendient_orders){
        this.name=name;
        this.address=address;
        this.phone=phone;
        this.zone=zone;
        this.pendient_orders=pendient_orders;
        this.defaulter="false";
        this.facebook=facebook;
        this.instagram=instragram;

    }
}
