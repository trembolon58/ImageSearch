package ru.findprofy.imagesearch;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> implements View.OnClickListener   {

    private LruCache<String, Bitmap> mCache;
    private FragmentActivity activity;
    private ProgressBar progressBar;
    private ArrayList<Image> images;
    private ServerApi api;
    // поисковой запрос
    private String query;
    // смещение в поисковой выдаче
    private int start;
    // если имеется не завершенный запрос, следующий посылать не будем
    private boolean hasNotCompletedResponse;
    private boolean finedAll;

    /**
     * успешный запрос к серверу
     */
    private Response.Listener<String> listener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            try {
                if (images.size() == 0) {
                    progressBar.setVisibility(View.GONE);
                }
                hasNotCompletedResponse = false;
                JSONObject obj = new JSONObject(response);
                parseItems(obj);

            } catch (Exception e) {
                // возможна ситуация, что ответ придет к уже неактивной Activity
                try {
                    // для отладки
                    if (images.size() == 0) {
                        Toast.makeText(activity, response, Toast.LENGTH_LONG).show();
                    }
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
                if (images.size() == 0) {
                    progressBar.setVisibility(View.GONE);
                }
                hasNotCompletedResponse = false;
                if (error.networkResponse.statusCode == 403) {
                    Toast.makeText(activity, "403 : дневной лимит запросов исчерпан", Toast.LENGTH_LONG).show();
                } else if (error.networkResponse.statusCode == 400) {// найдено все
                    finedAll = true;
                } else {
                    Snackbar.make(progressBar, "Не удалось подключиться к серверу", Snackbar.LENGTH_LONG)
                            .setAction("Повторить?", ImagesAdapter.this).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * конструктор класса
     * @param activity необходима для запуска просмотра полной версии изображения
     * @param progressBar показывает пользователю, что идет запрос на сервер
     * @param query поисковой запрос
     */
    public ImagesAdapter(FragmentActivity activity, ProgressBar progressBar, String query) {
        super();
        this.progressBar = progressBar;
        this.query = query;
        this.activity = activity;
        start = 1;
        images = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            mCache = new LruCache<>(200);
        }
        api = ServerApi.getInstance(activity);
        downloadMore();
    }

    /**
     * парсит успешный ответ с сервера
     * @param response ответ сервера
     * @throws Exception генерируется, если формат ответа не соответствует стандартному
     */
    private void parseItems(JSONObject response) throws Exception{
        JSONArray items = response.getJSONArray("items");
        String thumbnail;
        String src = null;
        JSONObject o;
        int width;
        int height;
        int lastSize = images.size();
        start += items.length();
        for (int i = 0; i < items.length(); ++i) {
            JSONObject item = items.getJSONObject(i);
            if (item.has("pagemap")) {
                item = item.getJSONObject("pagemap");
                if (item.has("cse_image")) {
                    o = item.getJSONArray("cse_image").getJSONObject(0);
                    //реальное изображение
                    src = o.getString("src");
                }
                if (item.has("cse_thumbnail")) {
                    //превью
                    o = item.getJSONArray("cse_thumbnail").getJSONObject(0);
                    width = o.getInt("width");
                    height = o.getInt("height");
                    thumbnail = o.getString("src");
                    images.add(new Image(width, height, thumbnail, src));
                }
            }
        }
        ImagesAdapter.this.notifyItemRangeInserted(lastSize, images.size());
    }

    /**
     * устанавливает новый поисковой запрос
     * @param q поисковой запрос
     */
    public void setQuery(String q) {
        // сбрасываем всю информацию
        query = q;
        start = 1;
        finedAll = false;
        images.clear();
        notifyDataSetChanged();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            mCache.evictAll();
        }
        downloadMore();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setOnClickListener(images.get(position));
        // подгружаем дополнительные, если до конца осталось меньше 10 картинок,
        // чтобы пользователь не замечал подгрузку
        if (images.size() - position < 2 && !hasNotCompletedResponse && !finedAll) {
            downloadMore();
        }
    }

    /**
     * подгружает дополнительные картинки
     */
    public void downloadMore() {
        hasNotCompletedResponse = true;
        // при первом запросе необходимо показать пользователю, что чтото происходит
        if (images.size() == 0) {
            progressBar.setVisibility(View.VISIBLE);
        }
        api.getImages(query, start, listener, errorListener);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    @Override
    public void onClick(View v) {
        api.getImages(query, start, listener, errorListener);
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        public NetworkImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (NetworkImageView) itemView.findViewById(R.id.network_image_view);
        }
        //в данноф версии приложения отображаются больние картинки
        public void setOnClickListener(final Image img) {
            //не будем показывать еще не загруженную картинку
            itemView.setVisibility(View.GONE);
            imageView.setImageUrl(img.getSrc(), new ImageLoader(api.getQueue(), new ImageLoader.ImageCache() {

                public void putBitmap(String url, Bitmap bitmap) {
                    // потом добавим кеширование для старых версий
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                        mCache.put(img.getSrc(), bitmap);
                    }
                    //отобразим карточку, как картинка загрузилось
                    itemView.setVisibility(View.VISIBLE);
                }

                public Bitmap getBitmap(String url) {
                    // ImageLoader дописывает к каждому url случайную часть, чтобы не было совпадений
                    // нам они даже на руку
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                        Bitmap bitmap = mCache.get(img.getSrc());
                        //запросу к сети не будет и картинка сразу отобразится
                        if (bitmap != null) {
                            itemView.setVisibility(View.VISIBLE);
                        }
                        return bitmap;
                    } else {
                        return null;
                    }
                }
            }));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //покажем полное изображение
                    if (img.getSrc() != null) {
                        Fragment f = new ShowImageFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(ShowImageFragment.IMG_SRC_TAG, img.getSrc());
                        f.setArguments(bundle);
                        activity.getSupportFragmentManager().beginTransaction()
                                .add(R.id.container, f).addToBackStack(null).commit();
                    }
                }
            });
        }
    }
}
