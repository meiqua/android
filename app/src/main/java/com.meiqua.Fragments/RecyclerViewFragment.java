
//This class is for searching items and listing them in a RecyclerView


package com.meiqua.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meiqua.lostlibrary.MainActivity;
import com.meiqua.lostlibrary.R;
import com.meiqua.model.item;
import com.meiqua.lostlibrary.ExampleApplication;
import android.app.ProgressDialog;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Color;
import android.util.Log;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

public class RecyclerViewFragment extends Fragment {
    private static final String TAG = "RecyclerViewFragment";
    private RecyclerAdapter adapter;
    public RecyclerAdapter getAdapter(){return  adapter;}

    private List<item> itemList = new ArrayList<item>();
    //itemList is the data which will be showed.
    public List<item> getItemList(){return itemList;}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter =new RecyclerAdapter(itemList);
        //following is the data for debug.
//        String url;
//      url="http://f.hiphotos.baidu.com/image/w%3D310/sign=345cefa4d343ad4ba62e40c1b2035a89/8601a18b87d6277f734020392a381f30e924fc65.jpg"
//              ;
//        for (int i=0;i<30;i++)
//        itemList.add(new item("title"+i,"Author",url,"B2-6-1-1-1-2-3","id"+i));
    }
    @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState)
   {

       MainActivity.menuSearch=true;
       MainActivity.menuGo=false;
       android.support.v7.widget.Toolbar toolbar=
       (android.support.v7.widget.Toolbar)getActivity().findViewById(R.id.tool_bar);
       toolbar.setTitle("search books");
       ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       getActivity().invalidateOptionsMenu();

       View view=inflater.inflate(R.layout.recycler_view_frament, container, false);
        android.support.v7.widget.RecyclerView mRecyclerView;
        mRecyclerView=(android.support.v7.widget.RecyclerView)view
                        .findViewById(R.id.recycler_view_for_search);
       mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
       mRecyclerView.addItemDecoration(
               new DividerItemDecoration(getActivity(), null));
       mRecyclerView.scrollToPosition(0);

        //adapter =new RecyclerAdapter(itemList);
       //better new adapter in create,because it may be used before on create view
       mRecyclerView.setAdapter(adapter);
       //initialize recyclerView

      return view;
       //if return inflater.inflate(R.layout.recycler_view_frament, container, false);
       //it will crash!!!  I think It's because it will create a new instance
       // too much time on this bug...
   }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    //    ExampleApplication.getInstance().getRequestQueue().cancelAll("query");
        ExampleApplication.getInstance().getRequestQueue().cancelAll("getBooks");
        //if fragment is destroyed ,the request will be cancelled.
        //maybe write them in onDestroy method better?
    }
}


