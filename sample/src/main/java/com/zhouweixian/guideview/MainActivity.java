package com.zhouweixian.guideview;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhouweixian.GuideView;

public class MainActivity extends AppCompatActivity {

    private ImageButton menu;
    private Button btnTest;
    private Button btnTest2;
    private GuideView guideView;
    private GuideView guideView3;
    private GuideView guideView2;
    private boolean initGuideView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        menu = (ImageButton) findViewById(R.id.ib_menu);
        btnTest = (Button) findViewById(R.id.btn_test);
        btnTest2 = (Button) findViewById(R.id.btn_test2);

    }

    private void setGuideView() {

        guideView = new GuideView.Builder(this)
                .target(menu)
                .guide(R.layout.guide_iv)
                .direction(GuideView.Direction.LEFT_BOTTOM)
                .bgcolor(ContextCompat.getColor(this, R.color.shadow))
                .build();

        guideView2 = new GuideView.Builder(this)
                .target(btnTest)
                .guide(R.layout.guide_tv)
                .direction(GuideView.Direction.LEFT_BOTTOM)
                .bgcolor(ContextCompat.getColor(this, R.color.shadow))
                .drawRect()
                .after(guideView)
                .build();

        guideView3 = new GuideView.Builder(this)
                .target(btnTest2)
                .guide(R.layout.guide_custom)
                .direction(GuideView.Direction.LEFT)
                .offset(-10, 0)
                .bgcolor(ContextCompat.getColor(this, R.color.shadow))
                .after(guideView2)
                .exitOnClick(true)
                .build();
        guideView.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!initGuideView) {
            initGuideView = true;
            setGuideView();
        }
    }
}
