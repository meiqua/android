
//This class is for managing fragments.(one activity--all fragments)

//there are four main functions in this mainActiviity
//1.newIntent will get the infomation from searchView in actionBar
//2.post data to server
//3.deal with infomation from scan 二维码
//4.static method of SharePreference to sava data to the local xml file.(function will be used in collectFragment) 

package com.meiqua.lostlibrary;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.support.v7.widget.SearchView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.meiqua.Fragments.BookShelfFragment;
import com.meiqua.Fragments.CardViewFragment;
import com.meiqua.Fragments.MapFragment;
import com.meiqua.Fragments.RecyclerAdapter;
import com.meiqua.Fragments.RecyclerViewFragment;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.meiqua.model.item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import HelperClass.*;

import HelperClass.MyjsonPostRequest;

public class MainActivity extends AppCompatActivity { //default is extends actionbarActivity

    private final String TAG = "MainActivity";
    private final String url = "http://115.28.205.144:8080/library/struts2/dispatch";
                                  //this is my server url 

    public String getUrl() {
        return url;
    }

    ;
    //this url is where query data be posted to

    static public boolean menuSearch = false;
    static public boolean menuGo = false;
    //all the app has just one toolbar.these fields are to manage the item of toolbar.

    static Context context;
    //this is for share-Preferences

    public static String file = "MyCollection";
    //this is where collection data saved

    private ProgressDialog pDialog;
    //this is for loading data

    private boolean readyId = false;
    private boolean readyLocation = false;

    private JSONObject jsonObject;

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    private String scanContentReady = "";

    public void setScanContentReady(String scanContentReady) {
        this.scanContentReady = scanContentReady;
    }
    //for ScanForReturn


    private MapFragment mapFragment;

    public MapFragment getMapFragment() {
        return mapFragment;
    }
    //manage map fragment in main activity,because it will be access in other fragment
    //if don't, I have to new one in other fragment
    //well, new one in other fragment also seem ok...

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCanceledOnTouchOutside(true);
        pDialog.setCancelable(true);

        jsonObject=new JSONObject();

        mapFragment = new MapFragment();
        mapFragment.produceHotSpotInfoList();
        //must initialize hotSpot here so setMarker will work


        android.support.v7.widget.Toolbar toolbar
                = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle("Welcome");
        setSupportActionBar(toolbar);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        CardViewFragment cardViewFragment = new CardViewFragment();
        //add CardViewFragment
        fragmentTransaction.add(R.id.fragment_container, cardViewFragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("-search->", "query will been post ");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            //when search be clicked
            String query = intent.getStringExtra(SearchManager.QUERY);
            //get data in searchView

            //postData here
            postData(url, query, "query");
            Log.i("-search->", "query has been post " + query);
            //url is where data should be post to.Set tag as query.
        }
    }

    public void postData(String url, String query, String tag) {
        //just post data in url
        try {
            query = URLEncoder.encode(query, "UTF-8");
            //   query = URLEncoder.encode(query, "UTF-8");
            Log.i("-query->", query);
            //encode twice
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        String response = url;
        String response = url + "?" + "key=" + tag + "&content=" + query;
//        StringRequest postReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
        if (tag.equals("query")) {
            RecyclerViewFragment rv = (RecyclerViewFragment) getFragmentManager().findFragmentByTag("fragmentRecyclerView");
            //don't worry about fragment may not be found.SearchView only appear in recyclerViewFragment
            //and if recyclerViewFragment is destroyed,the post query will be cancelled
            Log.i("-data->", "get url for query" + response);
            getData(response, "getBooks", rv.getItemList(), rv.getAdapter());
            //response is a url.
            //getData will get data from the url,set tag as getBooks and then display data in
            //recyclerViewFragment
        } else if (tag.equals("shelfQuery")) {
            BookShelfFragment bookShelfFragment = (BookShelfFragment) getFragmentManager().findFragmentByTag("bookShelfFragment");
            Log.i("-data->", "get url for shelfQuery" + response);
            getData(response, "getShelf", bookShelfFragment.getItemList(), bookShelfFragment.getAdapter());
        } else if (tag.equals("update")) {
            getData(url + "?key=update" + "&content=" + query, tag);
        }else if(tag.equals("return"))
        {
            getData(url + "?key=return" + "&content=" + query, tag);
        }

        //        StringRequest postReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {

//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                if (error != null)
//                    VolleyLog.d(TAG, "Error: " + error.getMessage());
//            }
//        }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put(tag, query);
//                //post a map.
//                return params;
//            }
//        };
        //  postReq.setTag(tag);
        //    ExampleApplication.getInstance().addToRequestQueue(postReq, tag);
        //set tag so post can be cancelled in onDestroy.
    }

    private void getData(String url, String tag)
    //will be called in MainActivity NewIntent
    {
        Log.i("-data->", "get data is called");
        //1  getdata from url
        pDialog.show();
        JsonArrayRequest itemReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        Log.i("-data->", "get " + response);
                        hidePDialog();
                        // Parsing json
                        if(response.length()>0)
                        {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject obj = response.getJSONObject(i);
                                    item mitem = new item();
                                    mitem.setId(obj.getString("id"));
                                    mitem.setLocation(obj.getString("location"));
                                    mitem.setState(obj.getInt("state"));
                                    MainActivity.updateSharePreferenceById(MainActivity.file, mitem);
                                    //update
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("-data->", "get error");
                if (error != null) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    Log.i("-data->", "get error" + error.getMessage());
                } else
                    Log.i("-data->", "get error null");
                hidePDialog();
            }
        });
        itemReq.setTag(tag);
        ExampleApplication.getInstance().addToRequestQueue(itemReq, tag);


        //1

    }


    //at beginning I use json array request, but I can't find a way to post data rather than
    //post in url. If in url ,Chinese(可恶的中文编码！！) will be encode wrong no matter what effort I do
    //  At last I try to use string to transit .so you can see getData method above use jsonArray...
    //  then everything seem to be ok...
    //several days on this bug
    private void getData(String url, String tag,
                         final List<item> itemList,
                         final android.support.v7.widget.RecyclerView.Adapter adapter)
    //will be called in MainActivity NewIntent
    {
        //parse url to change the get to post,because content may contain Chinese
        itemList.clear();
        final String key = url.substring(url.indexOf("=") + 1, url.indexOf("&"));
        final String content = url.substring(url.lastIndexOf("=") + 1, url.length());

        final Map<String, String> map = new HashMap<String, String>();
        map.put("key", key);
        map.put("content", content);

        final JSONObject Jb = new JSONObject();
        try {
            Jb.put("key", key);
            Jb.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        url = url.substring(0, url.indexOf("?"));
        Log.i("-data->", "url " + url + "  key " + key + "  content " + content);
        //this is a simple parse,so
        //don't change the url in get data
//
//


        Log.i("-data->", "get data is called");
        //1  getdata from url
        pDialog.show();

        StringRequest itemReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String responseString) {
                hidePDialog();
                JSONArray response = null;
                try {
                    response = new JSONArray(responseString);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < response.length(); i++) {
                    JSONObject obj = null;
                    try {
                        obj = response.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    item mitem = new item();

                    try {
                        mitem.setAuthor(URLDecoder.decode(obj.getString("author"), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        mitem.setTitle(URLDecoder.decode(obj.getString("title"), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        mitem.setThumbnailUrl(obj.getString("thumbnailUrl"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //    Log.i("images",mitem.getThumbnailUrl());
                    try {
                        mitem.setId(obj.getString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        mitem.setLocation(obj.getString("location"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        mitem.setState(obj.getInt("state"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //request for data
                    //data must have these feature: Json "image"  "title"  "author"
                    //       if (obj.getString("id") != null)

                    Log.i("-data->", "item " + mitem.getTitle() + "  " + mitem.getAuthor());
                    itemList.add(mitem);
                    // Parsing json
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null)
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("key", key);
                params.put("content", content);
                //post a map.
                return params;
            }
        };
//        JsonArrayRequest itemReq = new JsonArrayRequest(url,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        Log.d(TAG, response.toString());
//                        Log.i("-data->", "get " + response);
//                        hidePDialog();
//                        // Parsing json
//                        for (int i = 0; i < response.length(); i++) {
//                            try {
//                                JSONObject obj = response.getJSONObject(i);
//                                item mitem = new item();
//                                if (obj.getString("author") != null) {
//                                    try {
//                                        mitem.setAuthor(URLDecoder.decode(obj.getString("author"), "UTF-8"));
//                                        Log.i("-data->", "get " + mitem.getAuthor());
//                                    } catch (UnsupportedEncodingException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                if (obj.getString("title") != null){
//                                    try {
//                                        mitem.setTitle(URLDecoder.decode(obj.getString("title"), "UTF-8"));
//                                    } catch (UnsupportedEncodingException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                if (obj.get("thumbnailUrl") != null)
//                                    mitem.setThumbnailUrl(obj.getString("thumbnailUrl"));
//                            //    Log.i("images",mitem.getThumbnailUrl());
//
//                                if (obj.getString("id") != null)
//                                    mitem.setId(obj.getString("id"));
//                                if (obj.getString("location") != null)
//                                    mitem.setLocation(obj.getString("location"));
//                                //request for data
//                                //data must have these feature: Json "image"  "title"  "author"
//                         //       if (obj.getString("id") != null)
//
//                                    itemList.add(mitem);
//                                Log.i("-data->", "get " + itemList.size());
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.i("-data->", "get error");
//                if (error != null) {
//                    VolleyLog.d(TAG, "Error: " + error.getMessage());
//                    Log.i("-data->", "get error" + error.getMessage());
//                } else
//                    Log.i("-data->", "get error null");
//                hidePDialog();
//            }
//        });
        itemReq.setTag(tag);
        ExampleApplication.getInstance().addToRequestQueue(itemReq, tag);


        //1

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // initialize searchView here
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchView.setSubmitButtonEnabled(false);
        //you may set searchView textColor as below
//        SearchView.SearchAutoComplete textView = ( SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
//        textView.setTextColor(Color.WHITE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_go) {
            dialogGo();
            //deal with the item "go" in mapFragment
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                //deal with the back button of the phone
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ExampleApplication.getInstance().getRequestQueue().cancelAll("query");
        ExampleApplication.getInstance().getRequestQueue().cancelAll("getBooks");
        ExampleApplication.getInstance().getRequestQueue().cancelAll("getShelf");
        ExampleApplication.getInstance().getRequestQueue().cancelAll("update");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //menu.clear();
        //change actionBar item here.
        if (!menuSearch)
            menu.findItem(R.id.menu_search).setVisible(false);
        else {
            menu.findItem(R.id.menu_search).setVisible(true);
            menu.findItem(R.id.menu_search).expandActionView();
        }
        if (!menuGo)
            menu.findItem(R.id.menu_go).setVisible(false);
        else {
            menu.findItem(R.id.menu_go).setVisible(true);
        }


        return super.onPrepareOptionsMenu(menu);
    }


    private long firstTime = 0;

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(this, "再按退出", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
            } else {
                finish();
                System.exit(0);
            }


        } else {
            getFragmentManager().popBackStack();
        }
    }

    //following method is for sharePreference.
    // sharePreference is useful to save data in collectionFragment
    static public boolean saveSharePreference(String fileName, item mItem) {
        boolean flag = false;
        SharedPreferences preferences = context.getSharedPreferences(
                fileName, Context.MODE_APPEND);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(mItem.getId() + "Collected", mItem.isCollected());
        editor.putString(mItem.getId() + "Author", mItem.getAuthor());
        editor.putString(mItem.getId() + "ThumbnailUrl", mItem.getThumbnailUrl());
        editor.putString(mItem.getId() + "Title", mItem.getTitle());
        editor.putString(mItem.getId() + "Location", mItem.getLocation());
        editor.putString(mItem.getId() + "id", mItem.getId());

        flag = editor.commit();
        return flag;
    }

    static public boolean updateSharePreferenceById(String fileName, item mItem) {
        boolean flag = false;
        SharedPreferences preferences = context.getSharedPreferences(
                fileName, Context.MODE_APPEND);
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove(mItem.getId() + "Location");
        editor.putString(mItem.getId() + "Location", mItem.getLocation());

        flag = editor.commit();
        return flag;
    }

    static public Map<String, ?> getSharePreference(String fileName) {
        Map<String, ?> map = null;
        SharedPreferences preferences = context.getSharedPreferences(
                fileName, Context.MODE_PRIVATE);
        map = preferences.getAll();
        return map;
    }

    static public boolean CollectedSharePreference(String fileName, String id) {
        boolean map = false;
        SharedPreferences preferences = context.getSharedPreferences(
                fileName, Context.MODE_APPEND);
        map = preferences.getBoolean(id + "Collected", false);
        return map;
    }

    static public boolean removeSharePreference(String fileName, item mItem) {
        boolean flag = false;
        SharedPreferences preferences = context.getSharedPreferences(
                fileName, Context.MODE_APPEND);
        SharedPreferences.Editor editor = preferences.edit();

        editor.remove(mItem.getId() + "Collected");
        editor.remove(mItem.getId() + "Author");
        editor.remove(mItem.getId() + "ThumbnailUrl");
        editor.remove(mItem.getId() + "Title");
        editor.remove(mItem.getId() + "Location");
        editor.remove(mItem.getId() + "id");

        flag = editor.commit();
        return flag;
    }


    //following method is for 2D ma(二维码 0。0).When scan finished it will be called
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                String scanContent = result.getContents();
                Toast.makeText(this, scanContent, Toast.LENGTH_SHORT).show();
                //code below is to make one post action,even if scan twice
                if (scanContent.contains("-")) {
                    try {
                        jsonObject.put("location", scanContent);
                        readyLocation = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        jsonObject.put("id", scanContent);
                        readyId = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            if (readyId && readyLocation) {
                scanContentReady = jsonObject.toString();
                    Log.i("-json->","my "+ scanContentReady);
                postData(url, scanContentReady, "return");
                readyId = false;
                readyLocation = false;
                jsonObject=null;
                scanContentReady="";
            }

            //  Toast.makeText(this, "Scanned: " + scanContent, Toast.LENGTH_LONG).show();

        }
        getFragmentManager().findFragmentByTag("returnFragment")
                .onActivityResult(requestCode, resultCode, data);
    }
        // This is important, otherwise the result will not be passed to the fragment


    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            // pDialog = null;
            //don't set pDialog=null !!!!!!!!!!!!!!!!
            //pDialog has just one instance but used for several times
            //several hours on this bug...

        }
    }

    private void dialogGo() {
        final String items[] = {"B2", "B3", "B4"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // builder.setTitle("go to");
        //  builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //  Toast.makeText(MainActivity.this, items[which], Toast.LENGTH_SHORT).show();
                android.support.v7.widget.Toolbar toolbar
                        = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
                toolbar.setTitle("Welcome to " + items[which]);
            }
        });
        builder.create().show();

        //following is a pickerView But it crashes on my phone..
//         final ArrayList<String> options1Items = new ArrayList<String>();
//         final ArrayList<ArrayList<String>> options2Items = new ArrayList<ArrayList<String>>();
//        OptionsPopupWindow pwOptions;
//        pwOptions = new OptionsPopupWindow(this);
//        options1Items.add("1");
//        options1Items.add("2");
//        options1Items.add("3");
//        ArrayList<String> options2Items_01=new ArrayList<String>();
//        options2Items_01.add("A");
//        options2Items_01.add("B");
//        options2Items_01.add("C");
//        ArrayList<String> options2Items_02=new ArrayList<String>();
//        options2Items_02.add("A");
//        options2Items_02.add("B");
//        options2Items_02.add("C");
//        options2Items.add(options2Items_01);
//        options2Items.add(options2Items_02);
//        //maybe don't need to add two item here
//        //but I'm too lazy to change it..
//        pwOptions.setPicker(options1Items, options2Items,true);
//        pwOptions.setLabels("楼", "室");

//        pwOptions.setOnoptionsSelectListener(new OptionsPopupWindow.OnOptionsSelectListener() {
//
//            @Override
//            public void onOptionsSelect(int options1, int option2) {
//                String tx = options1Items.get(options1)
//                        + options2Items.get(options1).get(option2);
//                android.support.v7.widget.Toolbar toolbar
//                        = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
//                toolbar.setTitle("Welcome to " + tx);
//            }
//        });
//        android.support.v7.widget.Toolbar toolbar
//                = (android.support.v7.widget.Toolbar) findViewById(R.id.tool_bar);
//        pwOptions.showAsDropDown(toolbar);
    }
}
