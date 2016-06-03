# GuideView
新手引导视图，初次打开页面时显示。
很多应用都有引导图，为了不每次重复劳动，写了个工具GuideView。

*功能简介
    GuideView能通过获取 需要引导的view（targetView） 的坐标和大小，动态的绘图，来显示引导图。targetView可以用 圆形或者矩 形圈出，
其他区域设置不同的背景色（比如半透明灰色）。圈出targetView之后，可以在targetView的上下左右以及左上、左下、右上、右下等八个区域添，
加一个说明性的view，可以是带有引导语的ImageView，也可以是TextView等。GuideView同样可以用于显示ListView里的某一项。
    注意，showOnce()会将targetView的id存入SP，用户需保证targetView的id的唯一性。如过targetView为ListView的某一item，
    可以通过targetView.setTag(Object tag)为其设置唯一的tag。


*使用图片


        ImageView iv = new ImageView(this);
        iv.setImageResource(R.drawable.img_new_task_guide);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        iv.setLayoutParams(params);

![image](https://github.com/laxian/GuideView/blob/develop/app/snapshot1.jpeg)

*使用文字

        TextView iv = new TextView(this);
        iv.setText("欢迎使用");
        iv.setTextColor(getResources().getColor(R.color.white));
        
![image](https://github.com/laxian/GuideView/blob/develop/app/snapshot2.jpeg)

*使用矩形效果

![image](https://github.com/laxian/GuideView/blob/develop/app/snapshot3.png)

*显示GuideView

        GuideView.Builder
                .newInstance(this)      // 必须调用
                .setTargetView(view)    // 必须调用，设置需要Guide的View
                .setCustomTipsView(iv)  // 必须调用，设置GuideView，可以使任意View的实例，比如ImageView 或者TextView
                .setDirction(GuideView.Direction.LEFT_BOTTOM)   // 设置GuideView 相对于TargetView的位置，有八种，不设置则默认在屏幕左上角
                .setBackGround(getResources().getColor(R.color.shadow)) // 设置背景颜色，默认透明
                .setOnclickExit(null)   // 设置点击消失，可以传入一个Callback，执行被点击后的操作
                .setRadius(32)          // 设置圆形透明区域半径，默认是targetView的显示矩形的半径
                .setCenter(300, 300)    // 设置圆心，默认是targetView的中心
//              .drawRec()              // 设置画矩形，默认是圆形
                .setOffset(200, 60)     // 设置偏移，一般用于微调GuideView的位置
                .showOnce()             // 设置首次显示，设置后，显示一次后，不再显示
                .build()                // 必须调用，Buider模式，返回GuideView实例
                .show();                // 必须调用，显示GuideView
