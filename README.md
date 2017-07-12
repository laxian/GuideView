# GuideView
新手引导视图，初次打开页面时显示的引导性图文

# 使用方式

1. 添加依赖

    `compile 'com.zhouweixian.guideview:guideview:1.0.1'`
   
2. 

```
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
```

# 效果预览

![image](https://github.com/laxian/GuideView/blob/develop/sample/app.gif)