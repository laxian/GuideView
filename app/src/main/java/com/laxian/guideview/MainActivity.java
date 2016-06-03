package com.laxian.guideview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private ImageButton menu;
    private Button btnTest;
    private Button btnTest2;
    private GuideView guideView;
    private GuideView guideView3;
    private GuideView guideView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu = (ImageButton) findViewById(R.id.ib_menu);
        btnTest = (Button) findViewById(R.id.btn_test);
        btnTest2 = (Button) findViewById(R.id.btn_test2);

    }

    private void setGuideView() {

        // 使用图片
        final ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.img_new_task_guide);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        iv.setLayoutParams(params);

        // 使用文字
        TextView tv = new TextView(this);
        tv.setText("欢迎使用");
        tv.setTextColor(getResources().getColor(R.color.white));
        tv.setTextSize(30);
        tv.setGravity(Gravity.CENTER);

        // 使用文字
        final TextView tv2 = new TextView(this);
        tv2.setText("欢迎使用2");
        tv2.setTextColor(getResources().getColor(R.color.white));
        tv2.setTextSize(30);
        tv2.setGravity(Gravity.CENTER);


        guideView = GuideView.Builder
                .newInstance(this)
                .setTargetView(menu)
                .setCustomGuideView(iv)
                .setDirction(GuideView.Direction.LEFT_BOTTOM)
                .setBgColor(getResources().getColor(R.color.shadow))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideView.hide();
                        guideView2.show();
                    }
                })
                .build();


        guideView2 = GuideView.Builder
                .newInstance(this)
                .setTargetView(btnTest)
                .setCustomGuideView(tv)
                .setDirction(GuideView.Direction.LEFT_BOTTOM)
                .setBgColor(getResources().getColor(R.color.shadow))
                .setDrawRec()
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideView2.hide();
                        guideView3.show();
                    }
                })
                .build();


        guideView3 = GuideView.Builder
                .newInstance(this)
                .setTargetView(btnTest2)
                .setCustomGuideView(tv2)
                .setDirction(GuideView.Direction.LEFT_BOTTOM)
                .setBgColor(getResources().getColor(R.color.shadow))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideView3.hide();
                        guideView.show();
                    }
                })
                .build();
        guideView.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setGuideView();
    }
}
