package com.kenneth.record;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;


public class bigView extends LinearLayout {

    /**
     * 记录大悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录大悬浮窗的高度
     */
    public static int viewHeight;

    public bigView(final Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.window_recording, this);
        View view = findViewById(R.id.big_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
//        Button close = (Button) findViewById(R.id.close);
//        Button back = (Button) findViewById(R.id.back);
//        close.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                windowManager.removeBigWindow(context);
//                windowManager.removeSmallWindow(context);
//                Intent intent = new Intent(getContext(), recordService.class);
//                context.stopService(intent);
//            }
//        });
//        back.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                windowManager.removeBigWindow(context);
//                windowManager.createSmallWindow(context);
//            }
//        });
    }
}
