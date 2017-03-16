package com.kenneth.record;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class recordService extends Service {

    /**
     * Delete floating window。
     */
    private Handler handler = new Handler();
    /**
     * Timer
     */
    private Timer timer;


    private TelephonyManager tm;
    // Listener target
    private MyListener listener;
    // RecordMic
    private MediaRecorder mediaRecorder;
    private String phoneNumber;
    private boolean phoneRing;
    private int audioCount = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 0.5 slot
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new RefreshTask(), 0, 500);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Timer end when Service Destroy
        timer.cancel();
        timer = null;
    }

    private class MyListener extends PhoneStateListener {
        // When Phone change ... doThing
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            try {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE://空闲状态。
                        phoneRing = false;
                        closeRecord();
                        break;
                    case TelephonyManager.CALL_STATE_RINGING://零响状态。
                        phoneRing = true;
                        phoneNumber = incomingNumber;
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK://通话状态
                        phoneRing = true;
                        break;
                    default:
                        phoneRing = false;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    public void setMediaRecorder() {
//        try {
//
//            if (Environment.getExternalStorageState()//確定SD卡可讀寫
//                    .equals(Environment.MEDIA_MOUNTED)) {
//
//                File sdFile = android.os.Environment.getExternalStorageDirectory();
//                String path = sdFile.getPath() + File.separator + "RecordMonitor";
//
//                File dirFile = new File(path);
//
//                if (!dirFile.exists()) {//如果資料夾不存在
//                    dirFile.mkdir();//建立資料夾
//                } else {
//                    //开始录音
//                    audioCount++;
//                    //1.实例化一个录音机
//                    mediaRecorder = new MediaRecorder();
//                    //2.指定录音机的声音源
//                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                    //3.设置录制的文件输出的格式
//                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//                    //4.指定录音文件的名称
//                    File file = new File(path, phoneNumber + "_" + audioCount + ".aac");
//                    mediaRecorder.setOutputFile(file.getAbsolutePath());
//                    //5.设置音频的编码
//                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//                    //6.准备开始录音
//                    mediaRecorder.prepare();
//                    //7.开始录音
//                    mediaRecorder.start();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
    public void closeRecord() {
        if (mediaRecorder != null) {
            //  Stop catch
            mediaRecorder.stop();
            //  Release
            mediaRecorder.release();
            mediaRecorder = null;
            //TODO video upload to dir
            Log.i("SystemService", "Voice down");
        }else {
            Log.i("SystemService", "closeRecord error");
        }
    }

    class RefreshTask extends TimerTask {
        @Override
        public void run() {
            // 当前界面是Ring create window。
            if (phoneRing && !windowManager.isWindowShowing()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        windowManager.createSmallWindow(getApplicationContext());
                    }
                });
            }
            // 当前界面不是Ring delete window。
            else if (!phoneRing && windowManager.isWindowShowing()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        windowManager.removeSmallWindow(getApplicationContext());
                        windowManager.removeBigWindow(getApplicationContext());
                    }
                });
            }

        }
    }

    /**
     * desktop
     */
    private boolean isHome() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        return getHomes().contains(rti.get(0).topActivity.getPackageName());
    }

    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }
}
