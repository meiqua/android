package com.meiqua.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meiqua.lostlibrary.MainActivity;
import com.meiqua.lostlibrary.R;
import com.meiqua.model.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollectionFragment extends Fragment {
    private RecyclerAdapter adapter;
    private List<item> itemList = new ArrayList<item>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //update location in collection
        MainActivity mainActivity=(MainActivity)getActivity();

        Map<String, ?> map = MainActivity.getSharePreference(MainActivity.file);
        //get id
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            String key = entry.getKey();
            if (key.substring(key.length()-2, key.length())//length-2 get the last two char
                    .equals("id")) {
                mainActivity.postData(mainActivity.getUrl(), key.substring(0, key.length() - 2),"update");
            }

        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.menuSearch = false;
        MainActivity.menuGo=false;
        android.support.v7.widget.Toolbar toolbar =
                (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.tool_bar);
        toolbar.setTitle("my collection");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // better use try catch here
        getActivity().invalidateOptionsMenu();

        itemList.clear();
        setList();

        View view = inflater.inflate(R.layout.collection_fragment, container, false);
        android.support.v7.widget.RecyclerView mRecyclerView;
        mRecyclerView = (android.support.v7.widget.RecyclerView) view
                .findViewById(R.id.collection);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(
                new DividerItemDecoration(getActivity(), null));
        mRecyclerView.scrollToPosition(0);
        adapter = new RecyclerAdapter(itemList);
        mRecyclerView.setAdapter(adapter);

        return view;
    }

    public void setList() {
        String col = "Collected";
        String aut = "Author";
        String thu = "ThumbnailUrl";
        String til = "Title";
        String loc = "Location";
        String id = "id";
        List<String> idTemp = new ArrayList<String>();
        Map<String, ?> map = MainActivity.getSharePreference(MainActivity.file);
        //get id
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            String key = entry.getKey();
            if (key.substring(key.length()-2, key.length())//length-2 get the last two char
                    .equals(id)) {                         // much time on this
                                                          //because count star from 0..
                    idTemp.add(key.substring(0, key.length() - 2));
               Log.i("-id-->", "" + key.substring(0, key.length() - 2));
            }

        }

        for (int i = 0; i < idTemp.size(); i++) {
            String idT = idTemp.get(i);
            item itemT=new item();    //item must be created at here.or only a reference
                                      //will be added to itemList
            itemT.setCollected((Boolean) map.get(idT + col));
            itemT.setAuthor((String) map.get(idT + aut));
            itemT.setThumbnailUrl((String) map.get(idT + thu));
            itemT.setTitle((String) map.get(idT + til));
            itemT.setLocation((String) map.get(idT + loc));
            itemT.setId((String) map.get(idT + id));
            itemList.add(itemT);
           // Log.i("-title-->", "" + itemT.getTitle());
        }
    }
}
