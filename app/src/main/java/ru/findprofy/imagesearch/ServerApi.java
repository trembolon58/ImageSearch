package ru.findprofy.imagesearch;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.net.URLEncoder;

public class ServerApi {

    private static final String ROOT_URL = "https://www.googleapis.com/customsearch/v1?";
    private static final String CX = "002491533294088049790:lutaks6ddpi";
    private static final String API_KEY = "AIzaSyDlWIgHozOoZBS0F9bZkggwWFR_r87oT6Q";
    private static volatile ServerApi serverApi;
    private RequestQueue queue;

    public RequestQueue getQueue() {
        return queue;
    }

    private ServerApi(RequestQueue queue) {
        this.queue = queue;
    }

    public static ServerApi getInstance(Context context) {
        if (serverApi == null) {
            synchronized (ServerApi.class) {
                if (serverApi == null) {
                    serverApi = new ServerApi(Volley.newRequestQueue(context));
                }
            }
        }
        return serverApi;
    }

    /**
     * функция получения картикок из поиска гугл
     * @param query поисковой запрос
     * @param start смещение относительно первой найденой картинки
     * @param responseListener колбек для успешного запроса
     * @param errorListener колбек для неудачного запроса
     */
    public void getImages(String query, int start, Response.Listener<String> responseListener,
                          Response.ErrorListener errorListener) {
        StringRequest getRequest = new StringRequest(Request.Method.GET, createUrl(query, start),
                responseListener, errorListener);
        queue.add(getRequest);
    }

    @SuppressWarnings("deprecation")
    public String createUrl(String query, int start) {
        String s = ROOT_URL;
        //обязательные параметры
        s += "q=" + URLEncoder.encode(query) + "&key=" + API_KEY + "&cx=" + CX;
        if (start > 1) {
            s += "&start=" + start;
        }
        return s;
    }
}
