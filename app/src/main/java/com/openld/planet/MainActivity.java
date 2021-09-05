package com.openld.planet;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.openld.planet.planet.TagPlanetView;

public class MainActivity extends AppCompatActivity {
    private TagPlanetView mTagPlanetView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTagPlanetView = findViewById(R.id.planet);

        String[] array = new String[]{
                 "1",  "2",  "3",  "4",  "5",  "6",  "7",  "8",  "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30",
                "31", "32", "33", "34", "35", "36", "37", "38", "39", "40"
        };

        TextTagsAdapter adapter = new TextTagsAdapter(array);
        mTagPlanetView.setAdapter(adapter);
    }
}