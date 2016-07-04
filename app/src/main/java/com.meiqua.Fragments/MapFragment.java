//this fragment is for B2 room

package com.meiqua.Fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.meiqua.model.HotSpotInfo;
import com.meiqua.lostlibrary.MainActivity;
import com.meiqua.lostlibrary.R;

import com.qozix.tileview.TileView;
import com.qozix.tileview.hotspots.HotSpot;
import com.qozix.tileview.hotspots.HotSpotEventListener;
import com.qozix.tileview.hotspots.HotSpotManager;
import com.qozix.tileview.markers.MarkerEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapFragment extends Fragment {
    private TileView tileView;
    private final int tileX=2440;
    private final int tileY=3416;
    //size of tileView
    private List<HotSpotInfo> hotSpotInfoList = new ArrayList<HotSpotInfo>();
    //for hotSpot

    private Boolean addPinFlag = false;
    private int listIterator;
    private String iLocation;
    //for marker
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //produceHotSpotInfoList();
        //called when  when  new MapFragment()

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.menuSearch = false;
        MainActivity.menuGo=true;
        android.support.v7.widget.Toolbar toolbar =
                (android.support.v7.widget.Toolbar) getActivity().findViewById(R.id.tool_bar);
        toolbar.setTitle("Welcome to B2");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getActivity().invalidateOptionsMenu();

        tileView = new TileView(getActivity());
        tileView.setSize(tileX, tileY);

        tileView.addDetailLevel(0.125f, "tiles/125_%col%_%row%.png", "downsamples/map.png");
        tileView.addDetailLevel(0.25f, "tiles/250_%col%_%row%.png", "downsamples/map.png");
        tileView.addDetailLevel(0.5f, "tiles/500_%col%_%row%.png", "downsamples/map.png");
        tileView.addDetailLevel(1f, "tiles/1000_%col%_%row%.png", "downsamples/map.png");

        tileView.defineRelativeBounds(0, 0, tileX, tileY);
        tileView.setMarkerAnchorPoints(-0.5f, -1.0f);
        //offset of Marker
        tileView.setTransitionsEnabled(false);
        tileView.setScaleLimits(0,2);
        //set limits so it can be 放大 to proper scale.
        tileView.setScale(0.5);
        frameTo(tileX/2, tileY);

//        tileView.addMarkerEventListener(new MarkerEventListener() {
//            @Override
//            public void onMarkerTap(View view, int i, int i1) {
//                TapMarkEvent((String) view.getTag());
//            }
//        });
        //better set onClickedListener in addPin ,because it can intersect tap event
        //if don't intersect, hotSpot will receive the tap event of marker.

        addHotSpot();

        if (addPinFlag) {
            //addPinFlag = false;
            int x=getHotSpotList().get(listIterator).getMarkerX();
            int y=getHotSpotList().get(listIterator).getMarkerY();
            addPin(x,y,iLocation);
            tileView.slideToAndCenter(x,y);
        }

        return tileView;
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
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TapMarkEvent((String)v.getTag());
            }
        });

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
                //add tap event here;
                //Log.i("-spot->", "location x" + i + " y" + i1);
                // Log.i("-spot->","SpotTag"+(String)object);
                //    Log.i("-spot->","tag"+(String)hotSpot.getTag());


                TapSpotEvent((String) hotSpot.getTag());


                //when I try to use tags,I can't get correct tags.Maybe the tileViews has some errors
                //so I try to use locations to solve this.
                //at last I think the problem is using methods don't belong to this 匿名内部类.
                //but finally I find I initialize the HotSpotInfo in wrong order
                //one day on this bug....

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

    public void produceHotSpotInfoList() { //better initialize it in MainActivity
        //Attention!!!!!!!!!!!
        //the order of parameters is left top right bottom!!!!!
        //the smaller the bug is,the harder finding it is..one day on this bug...


        //tag must be added in order
        hotSpotInfoList.add(new HotSpotInfo(1783, 1910, 1829, 1941, "B2-6-1-1-1"));
        hotSpotInfoList.add(new HotSpotInfo(1829, 1910, 1880, 1941, "B2-6-1-2-1"));
        hotSpotInfoList.add(new HotSpotInfo(1880, 1910, 1930, 1941, "B2-6-1-3-1"));
        hotSpotInfoList.add(new HotSpotInfo(1930, 1910, 1979, 1941, "B2-6-1-4-1"));
        hotSpotInfoList.add(new HotSpotInfo(1736, 1844, 1783, 1873, "B2-6-2-1-1"));
        hotSpotInfoList.add(new HotSpotInfo(1783, 1844, 1830, 1873, "B2-6-2-2-1"));
        hotSpotInfoList.add(new HotSpotInfo(1830, 1844, 1877, 1873, "B2-6-2-3-1"));
        hotSpotInfoList.add(new HotSpotInfo(1877, 1844, 1928, 1873, "B2-6-2-4-1"));
        hotSpotInfoList.add(new HotSpotInfo(1928, 1844, 1977, 1873, "B2-6-2-5-1"));
//        Log.i("-spot->", "size" + hotSpotInfoList.size());
//        for (int count = 0; count < hotSpotInfoList.size(); count++) {
//            Log.i("-spot->", "check  " + hotSpotInfoList.get(count).getTag());
//            Log.i("-spot->", "check  " + hotSpotInfoList.get(count).getLeft());
//            Log.i("-spot->", "check  " + hotSpotInfoList.get(count).getRight());
//            Log.i("-spot->", "check  " + hotSpotInfoList.get(count).getTop());
//            Log.i("-spot->", "check  " + hotSpotInfoList.get(count).getBottom());
//        }
    }




    public List<HotSpotInfo> getHotSpotList() {
        return hotSpotInfoList;
    }

    public void setMarker(String itemLocation) {
        String roomExampleLocation = "B2-6-1-1-1";
        String roomLocation = itemLocation.substring(0, roomExampleLocation.length());
        Log.i("-marker->", "markerCalled" + "true");
        Log.i("-marker->", "roomLocation" + roomLocation);
        Log.i("-marker->", "getHotSpotList().size()" + getHotSpotList().size());
        for (int i = 0; i < hotSpotInfoList.size(); i++) {
            // Log.i("-marker->","roomTag"+getHotSpotList().get(i).getTag());
            if (hotSpotInfoList.get(i).getTag().equals(roomLocation)) {
                listIterator = i;
                addPinFlag = true;
                iLocation = itemLocation;
                //    addPin(getHotSpotList().get(i).getMarkerX(), getHotSpotList().get(i).getMarkerY(), itemLocation);
                 //can't addPin here because addPin must be in onCreatView
            }
        }
    }

    private void TapSpotEvent(String tag) {
        //add tap event here
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.fragment_container));

        BookShelfFragment bookShelfFragment=new BookShelfFragment();
        bookShelfFragment.produceHotSpotInfoList();
        //initialize the hotSpot of bookShelf
        bookShelfFragment.setMarker(tag);
        //there need a method to bring tag
        fragmentTransaction.add(R.id.fragment_container, bookShelfFragment,"bookShelfFragment");

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
       // Toast.makeText(getActivity(), tag, Toast.LENGTH_SHORT).show();
    }

    private void TapMarkEvent(String tag) {
        //add tap event here
        TapSpotEvent(tag);
        //tag need to be jie xi in BookShelfFragment

      //  Toast.makeText(getActivity(), tag, Toast.LENGTH_SHORT).show();
    }

}
