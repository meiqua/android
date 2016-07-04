package com.meiqua.Fragments;
                                            //for bookShelf,use tileView and recyclerView 

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.meiqua.lostlibrary.ExampleApplication;
import com.meiqua.lostlibrary.MainActivity;
import com.meiqua.lostlibrary.R;
import com.meiqua.model.HotSpotInfo;
import com.meiqua.model.item;
import com.qozix.tileview.TileView;
import com.qozix.tileview.hotspots.HotSpot;
import com.qozix.tileview.hotspots.HotSpotEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookShelfFragment extends Fragment {
    private TileView tileView;
    private final int tileX = 1362;
    private final int tileY = 617;
    private List<HotSpotInfo> hotSpotInfoList = new ArrayList<HotSpotInfo>();

    private Boolean addPinFlag = false;
    private int listIterator;
    private String iLocation;
    public String getiLocation(){return iLocation;}

    private List<item> itemList = new ArrayList<item>();

    public List<item> getItemList() {
        return itemList;
    }

    private RecyclerAdapterForBookShelf adapter;

    public RecyclerAdapterForBookShelf getAdapter() {
        return adapter;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new RecyclerAdapterForBookShelf(itemList);
    //    produceHotSpotInfoList();
        //called in mapFragment

        //data for debug
//        String url;
//        url = "http://f.hiphotos.baidu.com/image/w%3D310/sign=345cefa4d343ad4ba62e40c1b2035a89/8601a18b87d6277f734020392a381f30e924fc65.jpg"
//        ;
//        for (int i = 0; i < 30; i++)
//            itemList.add(new item("title" + i, "涛涛sb涛涛sb涛涛sb涛涛sb涛涛sb涛涛sb涛涛sb涛涛sb", url, "B2-6-1-1-1-2-3", "id" + i));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.menuSearch = false;
        MainActivity.menuGo = false;
        android.support.v7.widget.Toolbar toolbar =
                (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.tool_bar);
        toolbar.setTitle("BookShelf");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().invalidateOptionsMenu();

        View view = inflater.inflate(R.layout.book_shelf_fragment, container, false);
        tileView = new TileView(getActivity());
        tileView.setSize(tileX, tileY);

        tileView.addDetailLevel(0.125f, "tilesShelf/125_%col%_%row%.png", "downsamplesShelf/map.png");
        tileView.addDetailLevel(0.25f, "tilesShelf/250_%col%_%row%.png", "downsamplesShelf/map.png");
        tileView.addDetailLevel(0.5f, "tilesShelf/500_%col%_%row%.png", "downsamplesShelf/map.png");
        tileView.addDetailLevel(1f, "tilesShelf/1000_%col%_%row%.png", "downsamplesShelf/map.png");

        tileView.defineRelativeBounds(0, 0, tileX, tileY);
        tileView.setMarkerAnchorPoints(-0.5f, -1.0f);
        tileView.setTransitionsEnabled(false);
        tileView.setScale(1);
        frameTo(tileX / 2, tileY);
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.bookShelfContainer);
        frameLayout.addView(tileView);
        addHotSpot();

        if (addPinFlag) {
           // addPinFlag = false;
            Log.i("-bookShelf->","hotSpot tag  "+getHotSpotList().get(listIterator).getTag());
            int x = getHotSpotList().get(listIterator).getMarkerX();
            int y = (getHotSpotList().get(listIterator).getTop()
                    +getHotSpotList().get(listIterator).getBottom())/2;
            //this kind of y  can make the marker be in the shelf
            addPin(x, y, "marker");
            //set tag as "marker" so it can be remove in hotSpotListener
            TapSpotEvent(iLocation);

            //query bookShelf
            tileView.slideToAndCenter(x, y);
        }


        android.support.v7.widget.RecyclerView mRecyclerView;
        mRecyclerView = (android.support.v7.widget.RecyclerView) view
                .findViewById(R.id.recycler_view_for_book_shelf);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.scrollToPosition(0);

        LinearLayoutManager linearLayoutManager=
                (LinearLayoutManager)mRecyclerView.getLayoutManager();
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

  //      adapter = new RecyclerAdapterForBookShelf(itemList);
        //can't new adapter here,because adapter was used before on create view
        mRecyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        tileView.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        tileView.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tileView.destroy();
        tileView = null;
    }

    public TileView getTileView() {
        return tileView;
    }

    public void frameTo(final double x, final double y) {
        getTileView().post(new Runnable() {
                               @Override
                               public void run() {
                                   getTileView().moveToAndCenter(x, y);
                               }
                           }
        );
    }

    public void addPin(double x, double y, String itemLocation) {
        ImageView imageView = new ImageView(getActivity());
        imageView.setBackgroundResource(R.mipmap.dark_blue_location);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                TapMarkEvent((String) v.getTag());
//            }
//        });
        //don't set click event.Marker here don't carry message of location
        //it's just a mark

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                tileView.removeMarker(v);
                return true;
            }
        });
        imageView.setTag(itemLocation);

        getTileView().addMarker(imageView, x, y);
    }

    private void addHotSpot() {
        HotSpotEventListener hotSpotEventListener = new HotSpotEventListener() {
            @Override
            public void onHotSpotTap(HotSpot hotSpot, int i, int i1) {

                tileView.removeMarker(tileView.findViewWithTag("marker"));
                addPin(hotSpot.getBounds().centerX(), hotSpot.getBounds().centerY(), "marker");
                iLocation=(String) hotSpot.getTag();
                TapSpotEvent(iLocation);

            }
        };

        for (int i = 0; i < hotSpotInfoList.size(); i++) {
            getTileView().addHotSpot(produceHotSpot(hotSpotInfoList.get(i), hotSpotEventListener
            ));
        }

    }

    private HotSpot produceHotSpot(HotSpotInfo hotSpotInfo, HotSpotEventListener hotSpotEventListener) {
        int x1 = hotSpotInfo.getLeft();
        int y1 = hotSpotInfo.getTop();
        int x2 = hotSpotInfo.getRight();
        int y2 = hotSpotInfo.getBottom();
        String tag = hotSpotInfo.getTag();
        HotSpot hotSpot = new HotSpot(x1, y1, x2, y2);
        hotSpot.setTag(tag);
        //  Log.i("-spot->", hotSpot.getTag() + "tag is" + tag);
        hotSpot.setHotSpotEventListener(hotSpotEventListener);
        return hotSpot;
    }

    public void produceHotSpotInfoList() {
        //tag must be added in order
        hotSpotInfoList.add(new HotSpotInfo(76, 149, 371, 220, "B2-6-1-1-1-1"));
        hotSpotInfoList.add(new HotSpotInfo(76, 220, 371, 294, "B2-6-1-1-1-2"));
        hotSpotInfoList.add(new HotSpotInfo(76, 294, 371, 369, "B2-6-1-1-1-3"));
        hotSpotInfoList.add(new HotSpotInfo(76, 369, 371, 440, "B2-6-1-1-1-4"));
        hotSpotInfoList.add(new HotSpotInfo(371, 149, 665, 220, "B2-6-1-2-1-1"));
        hotSpotInfoList.add(new HotSpotInfo(371, 220, 665, 294, "B2-6-1-2-1-2"));
        hotSpotInfoList.add(new HotSpotInfo(371, 294, 665, 369, "B2-6-1-2-1-3"));
        hotSpotInfoList.add(new HotSpotInfo(371, 369, 665, 440, "B2-6-1-2-1-4"));
        hotSpotInfoList.add(new HotSpotInfo(665, 149, 960, 220, "B2-6-1-3-1-1"));
        hotSpotInfoList.add(new HotSpotInfo(665, 220, 960, 294, "B2-6-1-3-1-2"));
        hotSpotInfoList.add(new HotSpotInfo(665, 294, 960, 369, "B2-6-1-3-1-3"));
        hotSpotInfoList.add(new HotSpotInfo(665, 369, 960, 440, "B2-6-1-3-1-4"));
        hotSpotInfoList.add(new HotSpotInfo(960, 149, 1255, 220, "B2-6-1-4-1-1"));
        hotSpotInfoList.add(new HotSpotInfo(960, 220, 1255, 294, "B2-6-1-4-1-2"));
        hotSpotInfoList.add(new HotSpotInfo(960, 294, 1255, 369, "B2-6-1-4-1-3"));
        hotSpotInfoList.add(new HotSpotInfo(960, 369, 1255, 440, "B2-6-1-4-1-4"));
    }


    public List<HotSpotInfo> getHotSpotList() {
        return hotSpotInfoList;
    }

    public void setMarker(String itemLocation) {
        String roomExampleLocation = "B2-6-1-1-1-2";
        Log.i("-bookShelf->", "the tag is" + itemLocation);
        if (itemLocation.length() > roomExampleLocation.length()) {
            //if tag is longer than roomExample,then the tag comes from item's mapBtn clicked
            String roomLocation = itemLocation.substring(0, roomExampleLocation.length());
            Log.i("-bookShelf->","the roomLocation is"+roomLocation);
            Log.i("-bookShelf->","the hotSpotInfoList.size() is"+hotSpotInfoList.size());
            for (int i = 0; i < hotSpotInfoList.size(); i++) {
                // Log.i("-marker->","roomTag"+getHotSpotList().get(i).getTag());
                if (hotSpotInfoList.get(i).getTag().equals(roomLocation)) {
                    Log.i("-bookShelf->","get");
                    listIterator = i;
                    Log.i("-bookShelf->","listIterator  "+i);
                    addPinFlag = true;
                    iLocation = itemLocation;
                }
            }
        }
    }

    private void TapSpotEvent(String tag) {
        //add tap event here
        itemList.clear();
        String roomExampleLocation = "B2-6-1-1-1-2";
        String shelfLocation=tag.substring(0,roomExampleLocation.length());
       Log.i("-shelfLocation->",shelfLocation);
        MainActivity mainActivity=(MainActivity)getActivity();
        mainActivity.postData(mainActivity.getUrl(), shelfLocation,"shelfQuery");

      //   Toast.makeText(getActivity(), tag, Toast.LENGTH_SHORT).show();
    }

//    private void TapMarkEvent(String tag) {
//        //add tap event here
//        TapSpotEvent(tag);
//        // Toast.makeText(getActivity(), tag, Toast.LENGTH_SHORT).show();
//    }
    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        ExampleApplication.getInstance().getRequestQueue().cancelAll("getShelf");
        //if fragment is destroyed ,the request will be cancelled.
        //maybe write them in onDestroy method better?
    }
}
