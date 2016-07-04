//This class is for showing functions of this app
//At beginning I want to use cardView but I find a great library.
//com.beardedhen:androidbootstrap
//however finally I just use button..

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

import com.meiqua.lostlibrary.MainActivity;
import com.meiqua.lostlibrary.R;

public class CardViewFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        MainActivity.menuSearch=false;
        MainActivity.menuGo=false;
        android.support.v7.widget.Toolbar toolbar=
                (android.support.v7.widget.Toolbar)getActivity().findViewById(R.id.tool_bar);
        toolbar.setTitle("Welcome");
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getActivity().invalidateOptionsMenu();
          //initialize toolbar.



        View view=inflater.inflate(R.layout.card_view_fragment, container, false);
        view.findViewById(R.id.main_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set event here

                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.fragment_container));

                RecyclerViewFragment recyclerViewFragment=new RecyclerViewFragment();
                fragmentTransaction.add(R.id.fragment_container,recyclerViewFragment,"fragmentRecyclerView");
                //should set tag here. So it will be find in new intent method in MainActivity
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
               // getFragmentManager().executePendingTransactions();

                //can't getActivity().invalidateOptionsMenu(); here
                //because cardViewFragment has been destroyed.

            }
        });
        view.findViewById(R.id.main_my).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set event here
                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.fragment_container));

                CollectionFragment collectionFragment=new CollectionFragment();
                fragmentTransaction.add(R.id.fragment_container, collectionFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });

        view.findViewById(R.id.main_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set event here

                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.fragment_container));

               // Log.i("-->","returnClicked");
                ScanForReturnFragment scanForReturnFragment=new ScanForReturnFragment();
                fragmentTransaction.add(R.id.fragment_container,scanForReturnFragment,"returnFragment");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        view.findViewById(R.id.main_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set event here

                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.fragment_container));

                MainActivity mainActivity=(MainActivity)getActivity();
                MapFragment mapFragment=mainActivity.getMapFragment();
                mapFragment.produceHotSpotInfoList();
                fragmentTransaction.add(R.id.fragment_container,mapFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                // getFragmentManager().executePendingTransactions();

                //can't getActivity().invalidateOptionsMenu(); here
                //because cardViewFragment has been destroyed.

            }
        });
        return view;
    }
}
