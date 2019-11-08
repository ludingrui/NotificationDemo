package com.test.notificationdemo;

public class PriceItem {

    String id;
    String name;
    float price;
    String time;

    @Override
    public String toString() {
        return "id = " + id + ",name  = " + name + ",price = " + price + ",time = " + time;
    }
}
