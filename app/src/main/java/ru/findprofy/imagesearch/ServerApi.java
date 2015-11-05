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
    //код настраивает место поиска под ifunny.co
    private static final String CX = "002491533294088049790:lutaks6ddpi";
    //в день не больше ста запросов, эти коды чистые
    //AIzaSyAMt0Idjov5bzPAY8F0EPLaoxzLdpb2BS0
    //AIzaSyDzdw81tqy0FKuNBB3gquIYIMms1iT3iwA
    private static final String API_KEY = "AIzaSyDuma_Q26xALfsevQY8miJ0Q4NW-NnmqBE";
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

    /**
     * собирает все параметры воедино и генерирует URL
     * @param query поисковой запрос
     * @param start смещение относительно первого лемента
     * @return URL для отправки запроса
     */
    @SuppressWarnings("deprecation")
    public String createUrl(String query, int start) {
        String s = ROOT_URL;
        // обязательные параметры
        // использую устаревший метод, чтобы не отлавливать ошибки.
        // кодировка по умолчанию соответствует нашей
        s += "q=" + URLEncoder.encode(query) + "&key=" + API_KEY + "&cx=" + CX;
        if (start > 1) {
            s += "&start=" + start;
        }
        return s;
    }
}
