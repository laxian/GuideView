package com.laxian.guideview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageButton menu;
    private Button btnTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu = (ImageButton) findViewById(R.id.ib_menu);
        btnTest = (Button) findViewById(R.id.btn_test);

    }

    private void setGuideView(View view) {

//        ImageView iv = new ImageView(this);
//        iv.setImageResource(R.drawable.img_new_task_guide);
//
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        iv.setLayoutParams(params);

        TextView iv = new TextView(this);
        iv.setText("欢迎使用");
        iv.setTextColor(getResources().getColor(R.color.white));
        iv.setTextSize(30);
        iv.setGravity(Gravity.CENTER);


        GuideView.Builder
                .newInstance(this)
                .setTargetView(view)
                .setCustomTipsView(iv)
                .setDirction(GuideView.Direction.LEFT_BOTTOM)
                .setBackGround(getResources().getColor(R.color.shadow))
                .setOnclickExit(null)
                .setRadius(48)
//                .setCenter(300, 300)
//                .setOffset(0, 60)
//                .showOnce()
                .build()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setGuideView(menu);
    }
}
