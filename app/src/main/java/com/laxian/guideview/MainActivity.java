package com.laxian.guideview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends AppCompatActivity {

    private ImageButton menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu = (ImageButton) findViewById(R.id.ib_menu);

    }

    private void setGuideView(View view) {

        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.img_new_task_guide);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        iv.setLayoutParams(params);


        GuideView.Builder
                .newInstance(this)
                .setTargetView(view)
                .setCustomTipsView(iv)
                .setDirction(GuideView.Direction.LEFT_BOTTOM)
                .setBackGround(getResources().getColor(R.color.shadow))
                .setOnclickExit(null)
                .build()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setGuideView(menu);
    }
}
