/*
 * Copyright (c) 2018. LayoutXML
 * Created by LayoutXML.
 *
 */

package com.layoutxml.twelveish.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.layoutxml.twelveish.R;
import com.layoutxml.twelveish.objects.Color;

import java.util.ArrayList;
import java.util.List;

public class TextColorOptionsActivity extends Activity{

    private static final String TAG = "MainColorOptionsActivit";
    private List<Color> values = new ArrayList<>();
    private ColorsAdapter mAdapter;
    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wearablerecyclerview_activity);

        prefs = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        WearableRecyclerView mWearableRecyclerView = findViewById(R.id.wearable_recycler_view);
        mWearableRecyclerView.setLayoutManager(new WearableLinearLayoutManager(this));
        mWearableRecyclerView.setEdgeItemsCenteringEnabled(true);

        mAdapter = new ColorsAdapter();
        mWearableRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mWearableRecyclerView.setAdapter(mAdapter);
        generateValues();
    }

    private void generateValues(){
        values.add(new Color("White", android.graphics.Color.parseColor("#ffffff")));
        values.add(new Color("Gray 400", android.graphics.Color.parseColor("#bdbdbd")));
        values.add(new Color("Gray 500", android.graphics.Color.parseColor("#9e9e9e")));
        values.add(new Color("Gray 600", android.graphics.Color.parseColor("#757575")));
        values.add(new Color("Gray 700", android.graphics.Color.parseColor("#616161")));
        values.add(new Color("Gray 800", android.graphics.Color.parseColor("#424242")));
        values.add(new Color("Gray 900", android.graphics.Color.parseColor("#212121")));
        values.add(new Color("Black", android.graphics.Color.parseColor("#000000")));
        values.add(new Color("Red", android.graphics.Color.parseColor("#ff0000")));
        values.add(new Color("Magenta", android.graphics.Color.parseColor("#ff00ff")));
        values.add(new Color("Yellow", android.graphics.Color.parseColor("#ffff00")));
        values.add(new Color("Green", android.graphics.Color.parseColor("#00ff00")));
        values.add(new Color("Cyan", android.graphics.Color.parseColor("#00ffff")));
        values.add(new Color("Blue", android.graphics.Color.parseColor("#0000ff")));
        values.add(new Color("Material Red", android.graphics.Color.parseColor("#A62C23")));
        values.add(new Color("Material Pink", android.graphics.Color.parseColor("#A61646")));
        values.add(new Color("Material Purple", android.graphics.Color.parseColor("#9224A6")));
        values.add(new Color("Material Deep Purple", android.graphics.Color.parseColor("#5E35A6")));
        values.add(new Color("Material Indigo", android.graphics.Color.parseColor("#3A4AA6")));
        values.add(new Color("Material Blue", android.graphics.Color.parseColor("#1766A6")));
        values.add(new Color("Material Light Blue", android.graphics.Color.parseColor("#0272A6")));
        values.add(new Color("Material Cyan", android.graphics.Color.parseColor("#0092A6")));
        values.add(new Color("Material Teal", android.graphics.Color.parseColor("#00A695")));
        values.add(new Color("Material Green", android.graphics.Color.parseColor("#47A64A")));
        values.add(new Color("Material Light Green", android.graphics.Color.parseColor("#76A63F")));
        values.add(new Color("Material Lime", android.graphics.Color.parseColor("#99A62B")));
        values.add(new Color("Material Yellow", android.graphics.Color.parseColor("#A69926")));
        values.add(new Color("Material Amber", android.graphics.Color.parseColor("#A67E05")));
        values.add(new Color("Material Orange", android.graphics.Color.parseColor("#A66300")));
        values.add(new Color("Material Deep Orange", android.graphics.Color.parseColor("#A63716")));
        values.add(new Color("Material Brown", android.graphics.Color.parseColor("#A67563")));
        values.add(new Color("Material Gray", android.graphics.Color.parseColor("#676767")));
        values.add(new Color("Material Blue Gray", android.graphics.Color.parseColor("#7295A6")));
        values.add(new Color("Gold", android.graphics.Color.parseColor("#FFD700")));
        values.add(new Color("Sunset", android.graphics.Color.parseColor("#F8B195")));
        values.add(new Color("Fog", android.graphics.Color.parseColor("#A8A7A7")));
        values.add(new Color("Summer Red", android.graphics.Color.parseColor("#fe4a49")));
        values.add(new Color("Aqua", android.graphics.Color.parseColor("#2ab7ca")));
        values.add(new Color("Sun", android.graphics.Color.parseColor("#fed766")));
        values.add(new Color("Dawn", android.graphics.Color.parseColor("#451e3e")));
        mAdapter.notifyDataSetChanged();
    }

    public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.MyViewHolder>{

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView name;
            ImageView icon;

            MyViewHolder(View view) {
                super(view);
                Log.d(TAG,"MyViewHolder");
                name = view.findViewById(R.id.settingsListTextView);
                icon = view.findViewById(R.id.settingsListImagetView);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition(); // gets item position
                        Color selectedMenuItem = values.get(position);
                        prefs.edit().putInt(getIntent().getStringExtra("SettingsValue"),selectedMenuItem.getColorcode()).apply();
                        Toast.makeText(getApplicationContext(), "\""+selectedMenuItem.getName()+"\" set", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }

        @NonNull
        @Override
        public ColorsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG,"MyViewHolder onCreateViewHolder");
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.imageview_and_textview_item,parent,false);
            return new ColorsAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ColorsAdapter.MyViewHolder holder, int position) {
            Log.d(TAG,"MyViewHolder onBindViewHolder");
            Color color = values.get(position);
            holder.name.setText(color.getName());
            holder.icon.setColorFilter(color.getColorcode(), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        @Override
        public int getItemCount() {
            return values.size();
        }
    }

}
