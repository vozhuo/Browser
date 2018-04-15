package cf.vozhuo.app.broswser.search_history.presenter;


import android.content.Context;

import java.util.ArrayList;

import cf.vozhuo.app.broswser.search_history.model.SearchModelImpl;
import cf.vozhuo.app.broswser.search_history.storage.SearchBean;
import cf.vozhuo.app.broswser.search_history.model.OnSearchListener;
import cf.vozhuo.app.broswser.search_history.model.SearchModel;
import cf.vozhuo.app.broswser.search_history.view.MySearchView;

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
    public void search(String value) {
        searchModel.save(value);
        searchModel.search(value, this);
    }

    @Override
    public void fuzzySearch(String value) {
        searchModel.fuzzySearch(value, this);
    }

    @Override
    public void onSortSuccess(ArrayList<SearchBean> results) {
        searchView.showHistories(results);
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
