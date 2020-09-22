package com.dwiromadon.puskes.server;

public class BaseURL {

//    public static String baseUrl = "http://192.168.18.9:5050/";
    public static String baseUrl = "http://172.31.0.59:5000/";
//    public static String baseUrl = "http://192.168.43.246:5000/";
//    public static String baseUrl = "http://10.11.7.238:5050/";

    public static String inputPuskemas          = baseUrl + "puskes/input";
    public static String updatePuskes         = baseUrl + "puskes/ubah/";
    public static String getJarak               = baseUrl + "puskes/getjarak/";
    public static String tambahGambarPuskes    = baseUrl + "puskes/ubahgambar/";
    public static String updatePetDataPuskes     = baseUrl + "puskes/ubahpuskes/";

    //input history
    public static String inputHistory           = baseUrl + "history/input";
    public static String getHistory             = baseUrl + "history/getjarak";
    public static String hapusHistory             = baseUrl + "history/hapus/";
}