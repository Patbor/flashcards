package com.company;
public class Main {

    public static void main(String[] args) {
        Datasource dt = new Datasource();
        dt.open();
        dt.createTable();
        dt.fishApp();
        dt.close();
    }
}
