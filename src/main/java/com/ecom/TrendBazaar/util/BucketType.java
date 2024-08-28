package com.ecom.TrendBazaar.util;

public enum BucketType
{
    CATEGORY(1,""),
    PRODUCT(2,""),
    PROFILE(3,"");

    private int id;
    private String name;

    BucketType(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
