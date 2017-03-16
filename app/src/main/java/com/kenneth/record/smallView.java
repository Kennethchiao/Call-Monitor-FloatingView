package com.kenneth.record;

import java.io.File;
import java.lang.reflect.Field;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;


public class smallView extends LinearLayout implements View.OnLongClickListener {

    public static int viewWidth;
    public static int viewHeight;
    private static int statusBarHeight;
    private WindowManager windowManager;
    private WindowManager.LayoutParams mParams;
    private float xInScreen;
    private float yInScreen;
    private float xDownInScreen;
    private float yDownInScreen;
    private float xInView;
    private float yInView;

    private View view;
    private recordService service;
    private MediaRecorder mediaRecorder;
    private String phoneNumber;
    private boolean phoneRing;
    private int audioCount;

    public smallView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.window_record, this);
        view = findViewById(R.id.small_window_layout);
        view.setOnLongClickListener(this);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        view.setVisibility(INVISIBLE);
        service = new recordService();
        //TextView percentView = (TextView) findViewById(R.id.percent);
        //percentView.setText(windowManager.getUsedPercentValue(context));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();

                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:

                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
                    //openBigWindow();
                    setMediaRecorder();
                    view.setVisibility(VISIBLE);
                    view.setBackgroundResource(R.drawable.recording);
                }
                break;
            default:
                break;
        }
        return true;
    }

    public void setMediaRecorder() {
        try {

            if (Environment.getExternalStorageState()//確定SD卡可讀寫
                    .equals(Environment.MEDIA_MOUNTED)) {

                File sdFile = android.os.Environment.getExternalStorageDirectory();
                String path = sdFile.getPath() + File.separator + "RecordMonitor";

                File dirFile = new File(path);

                if (!dirFile.exists()) {//如果資料夾不存在
                    dirFile.mkdir();//建立資料夾
                } else {
                    audioCount++;
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    File file = new File(path, "Rec" + "_" + audioCount + ".3gp");
                    mediaRecorder.setOutputFile(file.getAbsolutePath());
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * Update small window address
     */
    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        windowManager.updateViewLayout(this, mParams);
    }

    private void openBigWindow() {
        com.kenneth.record.windowManager.createBigWindow(getContext());
        com.kenneth.record.windowManager.removeSmallWindow(getContext());
    }

    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    @Override
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.small_window_layout) {
            openBigWindow();
        }
        return false;
    }
}
