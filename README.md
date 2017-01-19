## GuideView
新手引导视图，初次打开页面时显示的引导性图文

##功能简介
/GuideView/能通过获取targetView的坐标和大小，动态的绘图，来高亮显示targetView 。targetView可以用圆形或者矩形圈出，
其他区域设置自定义的背景色。确定targetView之后，可以在targetView的上下左右以及左上、左下、右上、右下等八个区域添加一个说明性的
tipsView，可以是带有引导语的ImageView，也可以是带文字的TextView等。/GuideView/同样可以用于引导ListView里的某一项。
    注意，showOnce()会将targetView的id存入SP，用户需保证targetView的id的唯一性。如果targetView没有ID，可以用setTag替代。
    如过targetView为ListView的某一item，需用户自行保证GuideView只被显示一次。因为item可能没有ID，setTag方法会和Adapter.getView里的convertView的
    setTag()冲突。
    另外，同一个项目里，两个View，如果设置的id重名了，比如两个不相关的Activity里的TextView都设置了android:id="@+id/tv_title"，那他们的ID也是一样的。
    如果他们同时都需要使用GuideView引导，而且，都设置了显示一次showOnce，那么其中一个显示过之后，另一个就不在显示。

![image](https://github.com/laxian/GuideView/blob/develop/sample/app.gif)


##使用图片


```
        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.img_new_task_guide);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        iv.setLayoutParams(params);
```

![image](https://github.com/laxian/GuideView/blob/develop/sample/snapshot1.jpeg)

##使用文字

```
        TextView iv = new TextView(this);
        iv.setText("欢迎使用");
        iv.setTextColor(getResources().getColor(R.color.white));
```

![image](https://github.com/laxian/GuideView/blob/develop/app/snapshot2.jpeg)

##使用矩形效果

![image](https://github.com/laxian/GuideView/blob/develop/sample/snapshot3.png)

##显示GuideView

```
        new GuideView.Builder(this)
                .setTargetView(view)    // 必须调用，设置需要Guide的View
                .setCustomTipsView(iv)  // 必须调用，设置GuideView，可以使任意View的实例，比如ImageView 或者TextView
                .setDirction(GuideView.Direction.LEFT_BOTTOM)   // 设置GuideView 相对于TargetView的位置，有八种，不设置则默认在屏幕左上角
                .setBackGround(getResources().getColor(R.color.shadow)) // 设置背景颜色，默认透明
                .setExitOnclick(null)   // 设置点击消失，可以传入一个Callback，执行被点击后的操作
                .setRadius(32)          // 设置圆形透明区域半径，默认是targetView的显示矩形的半径
                .setCenter(300, 300)    // 设置圆心，默认是targetView的中心
                .drawRec()              // 设置画矩形，默认是圆形
                .setOffset(200, 60)     // 设置偏移，一般用于微调GuideView的位置
                .showOnce()             // 设置首次显示，设置后，显示一次后，不再显示
                .build()                // 必须调用，Buider模式，返回GuideView实例
                .show();                // 必须调用，显示GuideView
```


