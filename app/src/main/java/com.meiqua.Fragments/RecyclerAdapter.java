//This class is a custom adapter for RecyclerView
//the adapter is used in recyclerViewFragment and CollectFragment

package com.meiqua.Fragments;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.renderscript.RenderScript;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.meiqua.lostlibrary.ExampleApplication;
import com.meiqua.lostlibrary.MainActivity;
import com.meiqua.lostlibrary.R;
import com.android.volley.toolbox.NetworkImageView;
import com.meiqua.model.item;

import java.util.List;

import static com.meiqua.lostlibrary.ExampleApplication.mInstance;
import static com.meiqua.lostlibrary.MainActivity.mainActivityInstance;

public class RecyclerAdapter extends android.support.v7.widget.RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    //pay attention to android.support.v7.widget.RecyclerView.Adapter<RecyclerAdapter.ViewHolder>
    // it must have android.support.v7.widget.
    // and <RecyclerAdapter.ViewHolder> must be YourCustomClass.ViewHolder
    //some time on this bug..
    private final String TAG = "RecyclerAdapter";

    public String getTAG() {
        return this.TAG;
    }

    private List<item> mDataset;

    public List<item> getmDataset() {
        return this.mDataset;
    }

    public static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {
        //ViewHolder is for each item.
        private TextView textViewTitle;
        private NetworkImageView thumbNail;
        private TextView textViewAuthor;
        private Button colBtn;
        private Button mapBtn;


        public ViewHolder(View v, final List<item> dataset) {
            super(v);
            textViewTitle = (TextView) v.findViewById(R.id.textViewTitle);
            thumbNail = (NetworkImageView) v.findViewById(R.id.imageView);
            textViewAuthor = (TextView) v.findViewById(R.id.textViewAuthor);
            colBtn = (Button) v.findViewById(R.id.star);
            mapBtn = (Button) v.findViewById(R.id.map);

            //set onClickListener here for each item
            mapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final MainActivity mainActivity=(MainActivity)mainActivityInstance;
                    //though it's  not recommended. I can't find a better way to change fragment.

                    FragmentManager fragmentManager =mainActivity.getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.fragment_container));

                    MapFragment mapFragment=mainActivity.getMapFragment();
                    mapFragment.setMarker(dataset.get(getAdapterPosition()).getLocation());
                    if(dataset.get(getAdapterPosition()).getState()<=0) {
                        Toast.makeText(v.getContext(),"book has been fetched",Toast.LENGTH_LONG).show();
                    }
                    fragmentTransaction.add(R.id.fragment_container,mapFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
            colBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item itemTemp = dataset.get(getAdapterPosition());
                    Log.i("-->", "onClick " + getAdapterPosition());
                    if (MainActivity.CollectedSharePreference(MainActivity.file, itemTemp.getId())) {
//
                        MainActivity.removeSharePreference(MainActivity.file, itemTemp);
                        Log.i("-->", ">remove< " +
                                MainActivity.removeSharePreference(MainActivity.file, itemTemp));
                        itemTemp.setCollected(false);
                        v.setBackgroundResource(R.mipmap.blue_star);
                    } else {

                        Log.i("-->", ">save< " +
                                MainActivity.saveSharePreference(MainActivity.file, itemTemp));
                        itemTemp.setCollected(true);
                        MainActivity.saveSharePreference(MainActivity.file, itemTemp);
                        //Main.. must be after itemTemp.setCollected(true);
                        v.setBackgroundResource(R.mipmap.blue_full_star);
                    }
                }
            });
        }

        public Button getColBtn() {
            return colBtn;
        }

        public Button getMapBtn() {
            return mapBtn;
        }

        public TextView getTextView() {
            return textViewTitle;
        }

        public NetworkImageView getImageView() {
            return thumbNail;
        }

        public TextView getTextViewAuthor() {
            return textViewAuthor;
        }
    }


    public RecyclerAdapter(List<item> dataSet) {
        mDataset = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Bind your view for each item to ViewHolder here
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.image_text_item, viewGroup, false);

        return new ViewHolder(v, mDataset);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //called when item go into the list
        item object = mDataset.get(position);
        holder.getTextView().setText(object.getTitle());
        Log.i(TAG, position + "onBindViewHolder" + object.getTitle());
        holder.getTextViewAuthor().setText(object.getAuthor());
        Log.i(TAG, position + "onBindViewHolder" +object.getAuthor());
        holder.getImageView().setImageUrl(object.getThumbnailUrl(),
                ExampleApplication.getInstance().getImageLoader());

       if (MainActivity.CollectedSharePreference(MainActivity.file, object.getId()))
           object.setCollected(true);

        Log.i(TAG, position +"onBindViewHolder"+ object.isCollected());

        //if don't write follow,the star will change in unexpected positions.
        //item must update it's state in  onBindViewHolder
        if ( object.isCollected()) {
            holder.getColBtn().setBackgroundResource(R.mipmap.blue_full_star);
        }
        else {
            holder.getColBtn().setBackgroundResource(R.mipmap.blue_star);
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
