package com.demo.touristguide.DataModels;

public class TranslatorDataModel {

    String Name;
    String Price;
    String Desc;
    String PhoneNumber;
    int Picture;

    public TranslatorDataModel() {
    }


    public TranslatorDataModel(String name, String price, String desc, String phoneNumber, int picture) {

        Name = name;
        Price = price;
        Desc = desc;
        PhoneNumber = phoneNumber;
        Picture = picture;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public int getPicture() {
        return Picture;
    }

    public void setPicture(int picture) {
        Picture = picture;
    }


}