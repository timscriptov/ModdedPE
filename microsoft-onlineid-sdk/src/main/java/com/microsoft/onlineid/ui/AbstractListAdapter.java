package com.microsoft.onlineid.ui;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractListAdapter<T> implements ListAdapter {
    protected List<T> _items = new ArrayList();
    protected Set<DataSetObserver> _observers = new HashSet();

    public abstract View getView(int i, View view, ViewGroup viewGroup);

    public void setContent(Collection<? extends T> items) {
        this._items.clear();
        this._items.addAll(items);
        fireChanged();
    }

    public void setContent(T... items) {
        this._items.clear();
        Collections.addAll(this._items, items);
        fireChanged();
    }

    protected void onChanged() {
    }

    private void fireChanged() {
        onChanged();
        for (DataSetObserver observer : this._observers) {
            observer.onChanged();
        }
    }

    public int getCount() {
        return this._items.size();
    }

    public T getItem(int position) {
        return this._items.get(position);
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isEmpty() {
        return this._items.isEmpty();
    }

    public void registerDataSetObserver(DataSetObserver observer) {
        this._observers.add(observer);
    }

    public void unregisterDataSetObserver(DataSetObserver observer) {
        this._observers.remove(observer);
    }

    public boolean isEnabled(int position) {
        return true;
    }

    public boolean areAllItemsEnabled() {
        return true;
    }
}
