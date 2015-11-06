package ru.findprofy.imagesearch;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class FindImageFragment extends Fragment {

    private RecyclerView list;
    private ProgressBar progressBar;
    private boolean previewDeleted;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_find_image, container, false);
        list = (RecyclerView) root.findViewById(R.id.image_list);
        progressBar = (ProgressBar) root.findViewById(R.id.progress);
        //в данной версии отображаются большие изображения
        StaggeredGridLayoutManager sglm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //постоянные перестройки бесят
        sglm.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        list.setLayoutManager(sglm);
        list.setItemAnimator(new DefaultItemAnimator());
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.search_button);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //удаляем рекласное сообщение)
                if (!previewDeleted && getView() != null) {
                    View preview = getView().findViewById(R.id.preview);
                    ((FrameLayout) getView()).removeView(preview);
                    previewDeleted = true;
                }

                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    view.clearFocus();
                }
                updateQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        });
    }

    private void updateQuery(String query) {
        ImagesAdapter adapter = (ImagesAdapter) list.getAdapter();
        if (adapter == null) {
            list.setAdapter(new ImagesAdapter(getActivity(), progressBar, query));
        } else {
            adapter.setQuery(query);
        }
    }

}
