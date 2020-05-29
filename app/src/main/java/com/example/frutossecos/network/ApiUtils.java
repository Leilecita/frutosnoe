package com.example.frutossecos.network;

public class ApiUtils {

    private ApiUtils() {}

    public static final String BASE_URL = "http://192.168.88.7/frutos_secos_server/";

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static final String getImageUrl(String imagePath){
        if(imagePath.startsWith("/")){
            imagePath = imagePath.replaceFirst("/","");
        }
        return BASE_URL + imagePath;
    }
}
