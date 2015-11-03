package ru.findprofy.imagesearch;
import android.app.Activity;
import android.app.DownloadManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder>   {

    private Activity activity;
    private ArrayList<Image> images;
    private FragmentManager fm;
    private ServerApi api;
    // вся поисковая информация
    private Query query;
    // если имеется не завершенный запрос, следующий посылать не будем
    private boolean hasNotCompletedResponse;
    // не разу не видел, чтобы картинки в поиске закончились, но перестраховаться необходимо
    private boolean findedAll;

    /**
     * успешный запрос к серверу
     */
    private Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            try {
                hasNotCompletedResponse = false;
                ImagesAdapter.this.notifyDataSetChanged();
            } catch (Exception e) {
                // возможна ситуация, что ответ придет к уже неактивной Activity
                try {
                    // для отладки
                    Toast.makeText(activity, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * сервер недоступен, проблемы с соединением и тд.
     */
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            try {
                hasNotCompletedResponse = false;
                /*Snackbar.make(, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Toast.makeText(activity, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public ImagesAdapter(Activity activity, FragmentManager fm, Query q) {
        super();
        this.activity = activity;
        this.fm = fm;
        q.setPage(1);
        api = ServerApi.getInstance(activity);
        api.getImages(q, listener, errorListener);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // подгружаем дополнительные, если до конца осталось меньше 10 картинок,
        // чтобы пользователь не замечал подгрузку
        if (images.size() - position < 10 && !hasNotCompletedResponse && !findedAll) {
            downloadMore();
        }
    }

    public void downloadMore() {
        hasNotCompletedResponse = true;
        // при первом запросе необходимо показать пользователю, что чтото происходит
        if (images.size() == 0) {
            //showProgressDialog();
        }
        api.getImages(query.nextPage(), listener, errorListener);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public void setOnClickListener(final Image img, final FragmentManager fm) {

        }
    }
}
