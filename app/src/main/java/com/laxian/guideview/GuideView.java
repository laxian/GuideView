package com.laxian.guideview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;

/**
 * Created by zhouweixian on 2016/1/23.
 */
public class GuideView extends RelativeLayout {
    private final String TAG = getClass().getSimpleName();
    private int offsetX, offsetY;
    private Context mContent;
    /**
     * targetView 的外切圆半径
     */
    private int radius;
    /**
     * 提示语
     */
    private String tips;
    /**
     * 需要显示提示信息的View
     */
    private View targetView;
    /**
     * 自定义View
     */
    private View customTipsView;
    /**
     * 透明圆形画笔
     */
    private Paint mCirclePaint;
    /**
     * 背景色画笔
     */
    private Paint mBackgroundPaint;
    /**
     * targetView是否已测量
     */
    private boolean isMeasured;
    /**
     * targetView圆心
     */
    private int[] center;
    /**
     * 绘图层叠模式
     */
    private PorterDuffXfermode porterDuffXfermode;
    /**
     * 绘制前景bitmap
     */
    private Bitmap bitmap;
    /**
     * 背景色和透明度，格式 #aarrggbb
     */
    private int backgroundColor;
    /**
     * Canvas,绘制bitmap
     */
    private Canvas temp;
    /**
     * 相对于targetView的位置
     */
    private Direction direction;
    /**
     * targetView左上角坐标
     */
    private int[] location;

    public GuideView(Context context) {
        this(context, null);
    }

    public GuideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContent = context;
        init();
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setCustomTipsView(View customTipsView) {
        this.customTipsView = customTipsView;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = Color.parseColor(backgroundColor);
    }

    public void setBackground_color(int background_color) {
        this.backgroundColor = background_color;
    }

    public View getTargetView() {
        return targetView;
    }

    public void setTargetView(View targetView) {
        this.targetView = targetView;
    }

    public PorterDuffXfermode getPorterDuffXfermode() {
        return porterDuffXfermode;
    }

    public void setPorterDuffXfermode(PorterDuffXfermode porterDuffXfermode) {
        this.porterDuffXfermode = porterDuffXfermode;
    }

    private void init() {
    }

    public void showOnce() {
        mContent.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit().putBoolean(generateUniqId(targetView), true).commit();
    }

    private boolean hasShown() {
        return mContent.getSharedPreferences(TAG, Context.MODE_PRIVATE).getBoolean(generateUniqId(targetView), false);
    }

    private String generateUniqId(View v) {
        return "show_guide_on_target_view_" + v.getId();
    }

    public void show() {
        if (hasShown()) return;
        if (targetView != null) {
            targetView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (isMeasured)
                        return;
                    if (targetView.getHeight() > 0 && targetView.getWidth() > 0) {
                        isMeasured = true;
                    }

                    // 获取右上角坐标
                    location = new int[2];
                    targetView.getLocationInWindow(location);
                    // 获取中心坐标
                    center = new int[2];
                    center[0] = location[0] + targetView.getWidth() / 2;
                    center[1] = location[1] + targetView.getHeight() / 2;
                    // 获取外切圆半径
                    if (radius == 0) {
                        radius = getTargetViewRadius();
                    }

                    createView();
                }
            });
        }

        this.setBackgroundResource(R.color.transparent);
        ((FrameLayout) ((Activity) mContent).getWindow().getDecorView()).addView(this);
    }

    /**
     * 添加提示文字，位置在targetView的下边
     * 在屏幕窗口，添加蒙层，蒙层绘制总背景和透明圆形，圆形下边绘制说明文字
     */
    private void createView() {

        // 添加到蒙层
        RelativeLayout layout = new RelativeLayout(mContent);

        // Tips布局参数
        LayoutParams tipsViewParams;
        tipsViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tipsViewParams.setMargins(0, center[1] + radius + 10, 0, 0);
        if (customTipsView != null) {

//            LayoutParams tipsViewParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (direction != null) {
                int width = this.getWidth();
                int height = this.getHeight();

                int left = center[0] - radius;
                int right = center[0] + radius;
                int top = center[1] - radius;
                int bottom = center[1] + radius;
                switch (direction) {
                    case TOP:
                        this.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                        tipsViewParams.setMargins(offsetX, offsetY, 0 - offsetX, height - top - offsetY);
                        Log.e("xxxx", height - top + "");
                        break;
                    case LEFT:
                        this.setGravity(Gravity.RIGHT);
                        tipsViewParams.setMargins(offsetX, top + offsetY, width - left - offsetX, -offsetY);
                        Log.e("xxxx", width - left + "");
                        break;
                    case BOTTOM:
                        this.setGravity(Gravity.CENTER_HORIZONTAL);
                        tipsViewParams.setMargins(offsetX, bottom + offsetY, -offsetX, -offsetY);
                        break;
                    case RIGHT:
                        tipsViewParams.setMargins(right + offsetX, top + offsetY, 0 - offsetX, 0 - offsetY);
                        break;
                    case LEFT_TOP:
                        this.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                        tipsViewParams.setMargins(offsetX, offsetY, width - left - offsetX, height - top - offsetY);
                        break;
                    case LEFT_BOTTOM:
                        this.setGravity(Gravity.RIGHT);
                        tipsViewParams.setMargins(offsetX, bottom + offsetY, width - left - offsetX, -offsetY);
                        break;
                    case RIGHT_TOP:
                        this.setGravity(Gravity.BOTTOM);
                        tipsViewParams.setMargins(right + offsetX, offsetY, -offsetX, height - top - offsetY);
                        break;
                    case RIGHT_BOTTOM:
                        tipsViewParams.setMargins(right + offsetX, bottom + offsetY, 0, 0);
                        break;
                }
            } else {
                tipsViewParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }

            layout.setLayoutParams(tipsViewParams);
            layout.addView(customTipsView);

            this.addView(layout);
        }
    }

    /**
     * 获得targetView 的宽高，如果未测量，返回｛-1， -1｝
     *
     * @return
     */
    private int[] getTargetViewSize() {
        int[] location = {-1, -1};
        if (isMeasured) {
            location[0] = targetView.getWidth();
            location[1] = targetView.getHeight();
        }
        return location;
    }

    /**
     * 获得targetView 的半径
     *
     * @return
     */
    private int getTargetViewRadius() {
        if (isMeasured) {
            int[] size = getTargetViewSize();
            int x = size[0];
            int y = size[1];

            return (int) (Math.sqrt(x * x + y * y) / 2);
        }
        return -1;
    }

    private int getStatuBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (targetView == null) return;

        // 先绘制bitmap，再将bitmap绘制到屏幕
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
            temp = new Canvas(bitmap);
        }

        // 背景画笔
        Paint bgPaint = new Paint();
        if (backgroundColor != 0)
            bgPaint.setColor(backgroundColor);
        else
            bgPaint.setColor(getResources().getColor(R.color.shadow));

        // 绘制屏幕背景
        temp.drawRect(0, 0, temp.getWidth(), temp.getHeight(), bgPaint);

        // targetView 的透明圆形画笔
        if (mCirclePaint == null) mCirclePaint = new Paint();
        porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
        mCirclePaint.setXfermode(porterDuffXfermode);
        mCirclePaint.setColor(getResources().getColor(R.color.Red_800));
        mCirclePaint.setAntiAlias(true);// 设置画笔的锯齿效果。 true是去除，大家一看效果就明白了
        temp.drawCircle(center[0], center[1], radius, mCirclePaint);

        // 绘制到屏幕
        canvas.drawBitmap(bitmap, 0, 0, bgPaint);
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    enum Direction {
        LEFT, TOP, RIGHT, BOTTOM,
        LEFT_TOP, LEFT_BOTTOM,
        RIGHT_TOP, RIGHT_BOTTOM
    }

    interface OnClickCallback {
        void onClickedGuideView();
    }

    public static class Builder {
        static GuideView guiderView;
        static Builder instance = new Builder();
        Context mContext;

        private Builder() {
        }

        public Builder(Context ctx) {
            mContext = ctx;
        }

        public static Builder newInstance(Context ctx) {
            guiderView = new GuideView(ctx);
            return instance;
        }

        public static Builder setTargetView(View target) {
            guiderView.setTargetView(target);
            return instance;
        }

        public static Builder setBackGround(int color) {
            guiderView.setBackground_color(color);
            return instance;
        }

        public static Builder setTips(String tips) {
            guiderView.setTips(tips);
            return instance;
        }

        public static Builder setDirction(Direction dir) {
            guiderView.setDirection(dir);
            return instance;
        }

        public static Builder setOffset(int x, int y) {
            guiderView.setOffsetX(x);
            guiderView.setOffsetY(y);
            return instance;
        }

        public static Builder setRadius(int radius) {
            guiderView.setRadius(radius);
            return instance;
        }

        public static Builder setCustomTipsView(View view) {
            guiderView.setCustomTipsView(view);
            return instance;
        }

        public static Builder showOnce() {
            guiderView.showOnce();
            return instance;
        }

        public static GuideView build() {
            return guiderView;
        }

        public static Builder setOnclickExit(final OnClickCallback callback) {
            guiderView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    guiderView.setVisibility(GONE);
                    if (callback != null) {
                        callback.onClickedGuideView();
                    }
                }
            });
            return instance;
        }
    }
}
