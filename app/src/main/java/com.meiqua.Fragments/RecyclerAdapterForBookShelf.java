package com.meiqua.Fragments;


import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.meiqua.lostlibrary.R;
import com.meiqua.model.item;

import java.util.List;



import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.meiqua.lostlibrary.ExampleApplication;
import com.meiqua.lostlibrary.MainActivity;
import com.meiqua.lostlibrary.R;
import com.meiqua.model.item;

import java.util.List;

public class RecyclerAdapterForBookShelf extends
        android.support.v7.widget.RecyclerView.Adapter<RecyclerAdapterForBookShelf.ViewHolder> {
    private final String TAG = "RecyclerAdapterForBookShelf";

    public String getTAG() {
        return this.TAG;
    }

    private List<item> mDataset;

    public List<item> getmDataset() {
        return this.mDataset;
    }

    public static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        private TextView textView;

        public ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.book_vertical);

            textView.setMovementMethod(new ScrollingMovementMethod());
        }

        public TextView getTextView() {
            return textView;
        }

    }


    public RecyclerAdapterForBookShelf(List<item> dataSet) {
        mDataset = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.book_vertical, viewGroup, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        item object = mDataset.get(position);
        holder.getTextView().setText(object.getTitle()+"\n"+"\n"+object.getAuthor());
        Log.i("-data->", "bookShelf  "+object.getTitle()+"\n"+"\n"+object.getAuthor());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

