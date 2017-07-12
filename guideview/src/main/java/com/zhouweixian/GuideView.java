package com.zhouweixian;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.zhouweixian.library.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouweixian on 2016/1/23
 */
public class GuideView extends RelativeLayout implements ViewTreeObserver.OnGlobalLayoutListener {

    private final String TAG = getClass().getSimpleName();

    private Context mContent;
    private boolean first = true;

    /**
     * targetView前缀。SHOW_GUIDE_PREFIX + targetView.getId()作为保存在SP文件的key。
     */
    private static final String SHOW_GUIDE_PREFIX = "show_guide_on_view_";
    /**
     * GuideView 偏移量
     */
    private int offsetX, offsetY;
    /**
     * targetView 的外切圆半径
     */
    private int radius;
    /**
     * 需要显示提示信息的View
     */
    private View targetView;
    /**
     * 自定义View
     */
    private View customGuideView;
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
    private boolean onClickExit;
    private List<OnClickCallback> onclickListeners = new ArrayList<>();
    private boolean mShowOnce;
    private android.graphics.Rect mTargetViewRect;
    private boolean mDrawRect;
    private Paint mRectPaint;

    public void restoreState() {
        Log.v(TAG, "restoreState");
        offsetX = offsetY = 0;
        radius = 0;
        mCirclePaint = null;
        isMeasured = false;
        center = null;
        porterDuffXfermode = null;
        bitmap = null;
        needDraw = true;
//        backgroundColor = Color.parseColor("#00000000");
        temp = null;
//        direction = null;

    }

    public int[] getLocation() {
        return location;
    }

    public void setLocation(int[] location) {
        this.location = location;
    }

    public GuideView(Context context) {
        super(context);
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

    public void setCustomGuideView(View customGuideView) {
        this.customGuideView = customGuideView;
        if (!first) {
            restoreState();
        }
    }

    public void setBgColor(int background_color) {
        this.backgroundColor = background_color;
    }

    public View getTargetView() {
        return targetView;
    }

    public void setTargetView(View targetView) {
        this.targetView = targetView;
//        restoreState();
        if (!first) {
//            guideViewLayout.removeAllViews();
        }
    }

    private void init() {
        mTargetViewRect = new Rect();
    }

    public void showOnce() {
        if (targetView != null) {
            mShowOnce = true;
        }
    }

    private boolean hasShown() {
        if (targetView == null) return true;
        return mContent.getSharedPreferences(TAG, Context.MODE_PRIVATE).getBoolean(generateUniqId(targetView), false);
    }

    private String generateUniqId(View v) {
        if (v.getId() > 0)
            return SHOW_GUIDE_PREFIX + mContent.getClass().getSimpleName() + "_" + v.getId();
        else if (v.getTag() != null)
            return SHOW_GUIDE_PREFIX + v.getTag();
        else
            return SHOW_GUIDE_PREFIX + mContent.getClass().getSimpleName();
    }

    public int[] getCenter() {
        return center;
    }

    public void setCenter(int[] center) {
        this.center = center;
    }

    public void hide() {
        Log.v(TAG, "hide");
        if (customGuideView != null) {
            targetView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            this.removeAllViews();
            ((FrameLayout) ((Activity) mContent).getWindow().getDecorView()).removeView(this);
            restoreState();
        }
    }

    public void show() {
        Log.v(TAG, "show");
        if (hasShown()) return;

        if (mShowOnce) {
            mContent.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit().putBoolean(generateUniqId(targetView), true).apply();
        }

        if (targetView != null) {
            targetView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        }

        this.setBackgroundResource(R.color.transparent);

        ((FrameLayout) ((Activity) mContent).getWindow().getDecorView()).addView(this);
        first = false;
    }

    /**
     * 添加提示文字，位置在targetView的下边
     * 在屏幕窗口，添加蒙层，蒙层绘制总背景和透明圆形，圆形下边绘制说明文字
     */
    private void createGuideView() {
        Log.v(TAG, "createGuideView");

        // 引导图布局参数
        LayoutParams guideViewParams;
        guideViewParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        if (customGuideView != null) {

            if (direction != null) {
                int width = this.getWidth();
                int height = this.getHeight();

                int left;
                int right;
                int top;
                int bottom;
                if (!mDrawRect) {
                    left = center[0] - radius;
                    right = center[0] + radius;
                    top = center[1] - radius;
                    bottom = center[1] + radius;
                } else {
                    left = mTargetViewRect.left;
                    right = mTargetViewRect.right;
                    top = mTargetViewRect.top;
                    bottom = mTargetViewRect.bottom;
                }
                switch (direction) {
                    case TOP:
                        this.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
                        guideViewParams.setMargins(offsetX, offsetY - height + top, -offsetX, height - top - offsetY);
                        break;
                    case LEFT:
                        this.setGravity(Gravity.RIGHT);
                        guideViewParams.setMargins(offsetX - width + left, top + offsetY, width - left - offsetX, -top - offsetY);
                        break;
                    case BOTTOM:
                        this.setGravity(Gravity.CENTER_HORIZONTAL);
                        guideViewParams.setMargins(offsetX, bottom + offsetY, -offsetX, -bottom - offsetY);
                        break;
                    case RIGHT:
                        guideViewParams.setMargins(right + offsetX, top + offsetY, -right - offsetX, -top - offsetY);
                        break;
                    case LEFT_TOP:
                        this.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                        guideViewParams.setMargins(offsetX - width + left, offsetY - height + top, width - left - offsetX, height - top - offsetY);
                        break;
                    case LEFT_BOTTOM:
                        this.setGravity(Gravity.RIGHT);
                        guideViewParams.setMargins(offsetX - width + left, bottom + offsetY, width - left - offsetX, -bottom - offsetY);
                        break;
                    case RIGHT_TOP:
                        this.setGravity(Gravity.BOTTOM);
                        guideViewParams.setMargins(right + offsetX, offsetY - height + top, -right - offsetX, height - top - offsetY);
                        break;
                    case RIGHT_BOTTOM:
                        guideViewParams.setMargins(right + offsetX, bottom + offsetY, -right - offsetX, -top - offsetY);
                        break;
                }
            } else {
                guideViewParams.setMargins(offsetX, offsetY, -offsetX, -offsetY);
            }

            this.addView(customGuideView, guideViewParams);
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

    boolean needDraw = true;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.v(TAG, "onDraw");

        if (!isMeasured) return;

        if (targetView == null) return;

//        if (!needDraw) return;

        drawBackground(canvas);

    }

    private void drawBackground(Canvas canvas) {
        Log.v(TAG, "drawBackground");
        needDraw = false;
        // 先绘制bitmap，再将bitmap绘制到屏幕
        bitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
        temp = new Canvas(bitmap);

        // 背景画笔
        Paint bgPaint = new Paint();
        if (backgroundColor != 0)
            bgPaint.setColor(backgroundColor);
        else
            bgPaint.setColor(getResources().getColor(R.color.shadow));

        // 绘制屏幕背景
        temp.drawRect(0, 0, temp.getWidth(), temp.getHeight(), bgPaint);

        if (!mDrawRect) {
            // targetView 的透明圆形画笔
            if (mCirclePaint == null) mCirclePaint = new Paint();
            porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
            mCirclePaint.setXfermode(porterDuffXfermode);
            mCirclePaint.setAntiAlias(true);
            temp.drawCircle(center[0], center[1], radius, mCirclePaint);
        } else {
            // targetView 的透明矩形画笔
            if (mRectPaint == null) mRectPaint = new Paint();
            porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
            mRectPaint.setXfermode(porterDuffXfermode);
            mRectPaint.setAntiAlias(true);
            temp.drawRect(mTargetViewRect, mRectPaint);
        }

        // 绘制到屏幕
        canvas.drawBitmap(bitmap, 0, 0, bgPaint);
        bitmap.recycle();
    }

    public void setOnClickExit(boolean onClickExit) {
        this.onClickExit = onClickExit;
    }

    public void addOnclickListener(OnClickCallback onclickListener) {
        this.onclickListeners.add(onclickListener);
    }

    private void setClickInfo() {
        final boolean exit = onClickExit;
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onclickListeners != null && onclickListeners.size() > 0) {
                    for (OnClickCallback listener : onclickListeners) {
                        listener.onClicked();
                    }
                }
                if (exit) {
                    hide();
                }
            }
        });
    }

    @Override
    public void onGlobalLayout() {
        if (isMeasured)
            return;
        if (targetView.getHeight() > 0 && targetView.getWidth() > 0) {
            isMeasured = true;
        }

        // 获取targetView的中心坐标
        if (center == null) {
            // 获取右上角坐标
            location = new int[2];
            targetView.getLocationInWindow(location);
            targetView.getGlobalVisibleRect(mTargetViewRect);
            center = new int[2];
            // 获取中心坐标
            center[0] = location[0] + targetView.getWidth() / 2;
            center[1] = location[1] + targetView.getHeight() / 2;
        }
        // 获取targetView外切圆半径
        if (radius == 0) {
            radius = getTargetViewRadius();
        }
        // 添加GuideView
        createGuideView();
    }

    private void setDrawRect() {
        mDrawRect = true;
    }

    /**
     * 定义GuideView相对于targetView的方位，共八种。不设置则默认在targetView下方
     */
    public enum Direction {
        LEFT, TOP, RIGHT, BOTTOM,
        LEFT_TOP, LEFT_BOTTOM,
        RIGHT_TOP, RIGHT_BOTTOM
    }

    /**
     * GuideView点击Callback
     */
    public interface OnClickCallback {
        void onClicked();
    }

    public static class Builder {
        GuideView guideView;
        Context mContext;

        public Builder(Context ctx) {
            mContext = ctx;
            guideView = new GuideView(mContext);
        }

        public Builder target(View target) {
            guideView.setTargetView(target);
            return this;
        }

        public Builder guide(View view) {
            guideView.setCustomGuideView(view);
            return this;
        }

        public Builder guide(int viewId) {
            View view = LayoutInflater.from(mContext).inflate(viewId, null);
            guideView.setCustomGuideView(view);
            return this;
        }

        public Builder bgcolor(int color) {
            guideView.setBgColor(color);
            return this;
        }

        public Builder direction(Direction dir) {
            guideView.setDirection(dir);
            return this;
        }

        public Builder drawRect() {
            guideView.setDrawRect();
            return this;
        }

        public Builder offset(int x, int y) {
            guideView.setOffsetX(x);
            guideView.setOffsetY(y);
            return this;
        }

        public Builder radius(int radius) {
            guideView.setRadius(radius);
            return this;
        }

        public Builder center(int X, int Y) {
            guideView.setCenter(new int[]{X, Y});
            return this;
        }

        public Builder showOnce() {
            guideView.showOnce();
            return this;
        }

        public GuideView build() {
            guideView.setClickInfo();
            return guideView;
        }

        public Builder exitOnClick(boolean onclickExit) {
            guideView.setOnClickExit(onclickExit);
            return this;
        }

        public Builder onclick(final OnClickCallback callback) {
            guideView.addOnclickListener(callback);
            return this;
        }

        public Builder after(final GuideView preView) {
            preView.addOnclickListener(new OnClickCallback() {
                @Override
                public void onClicked() {
                    preView.hide();
                    guideView.show();
                }
            });
            return this;
        }
    }
}

