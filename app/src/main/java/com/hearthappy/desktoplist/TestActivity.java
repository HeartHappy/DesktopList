package com.hearthappy.desktoplist;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hearthappy.desktoplist.desktopview.DesktopListView;

/**
 * Created Date 2021/1/4.
 *
 * @author ChenRui
 * ClassDescription:
 */
public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DesktopListView desktopListView = findViewById(R.id.dlv);
        desktopListView.init(3, 15, null, null);
    }
}
