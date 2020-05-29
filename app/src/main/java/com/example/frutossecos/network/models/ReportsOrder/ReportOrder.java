package com.example.frutossecos.network.models.ReportsOrder;

import java.util.ArrayList;

public class ReportOrder {

    public Long order_id;
    public Long client_id;
    public String name;
    public String order_obs;
    public String order_created;
    public String address;
    public String zone;
    public String defaulter;
    public String prepared;
    public String phone;
    public String state;
    public String delivery_date;
    public String delivery_time;
    public ArrayList<ReportItemOrder> items;
    public Double total_amount;
    public Integer priority;

    public Double debt_value;

    public ReportOrder(){}
}
