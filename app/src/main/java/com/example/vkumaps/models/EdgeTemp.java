package com.example.vkumaps.models;

public class EdgeTemp {
    String a,b;
    int weight;
    public EdgeTemp(String a, String b, int weight){
        this.a=a;
        this.b=b;
        this.weight=weight;
    }

    public String getA() {
        return a;
    }

    public String getB() {
        return b;
    }

    public int getWeight() {
        return weight;
    }
}
