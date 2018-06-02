package tk.vozhuo.browser.interfaces.presenter;

import android.content.Context;

import java.util.ArrayList;

import tk.vozhuo.browser.interfaces.model.SearchModelImpl;
import tk.vozhuo.browser.entity.SearchBean;
import tk.vozhuo.browser.interfaces.model.OnSearchListener;
import tk.vozhuo.browser.interfaces.model.SearchModel;
import tk.vozhuo.browser.interfaces.MySearchView;

public class SearchPresenterImpl implements SearchPresenter, OnSearchListener {

    private static final int historyMax = 5;
    private MySearchView searchView;
    private SearchModel searchModel;

    public SearchPresenterImpl(MySearchView searchView, Context context) {
        this.searchView = searchView;
        this.searchModel = new SearchModelImpl(context, historyMax);
    }

    @Override
    public void remove(String key) {
        searchModel.remove(key);
        searchModel.sortHistory(this);
    }

    @Override
    public void clear() {
        searchModel.clear();
        searchModel.sortHistory(this);
    }

    @Override
    public void sortHistory() {
        searchModel.sortHistory(this);
    }

    @Override
    public void search(String value, boolean save) {
        if(save) searchModel.save(value);
        searchModel.search(value, this);
    }

    @Override
    public void fuzzySearch(String value) {
        searchModel.fuzzySearch(value, this);
    }

    @Override
    public void onSortSuccess(ArrayList<SearchBean> results) {
        searchView.showContains(results);
    }

    @Override
    public void searchSuccess(String value) {
        searchView.searchSuccess(value);
    }

    @Override
    public void fuzzySearchSuccess(ArrayList<SearchBean> results) {
        searchView.showContains(results);
    }

}
