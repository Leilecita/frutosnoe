package com.example.frutossecos.network.models;

public class Product {


    public Long id;
    public String name;
    public String state;
    public Double price;
    public Double half_price;
    public Double cuarter_price;

    public Double stock;
    public String created;


    public Product(String name, Double price,Double half_price, Double cuarter_price,Double stock){
        this.name=name;
        this.price=price;
        this.half_price=half_price;
        this.cuarter_price=cuarter_price;
        this.stock=stock;
        this.state="alive";

    }
}
