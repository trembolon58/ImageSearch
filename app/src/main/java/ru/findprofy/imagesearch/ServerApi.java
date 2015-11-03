package ru.findprofy.imagesearch;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class ServerApi {

    private static final String ROOT_URL = "https://www.googleapis.com/customsearch/v1?";
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
     * @param q поисковой запрос
     * @param responseListener колбек для успешного запроса
     * @param errorListener колбек для неудачного запроса
     */
    public void getImages(Query q, Response.Listener<String> responseListener,
                          Response.ErrorListener errorListener) {
        StringRequest getRequest = new StringRequest(Request.Method.GET, createUrl(q),
                responseListener, errorListener);
        queue.add(getRequest);
    }

    /**
     * преобразует параметры в поисковой url
     * @param q параметры, для которых необходимо запрос создать
     * @return url GET запроса с данными параметрами
     */
    private String createUrl(Query q) {
        return ROOT_URL;
    }

}
