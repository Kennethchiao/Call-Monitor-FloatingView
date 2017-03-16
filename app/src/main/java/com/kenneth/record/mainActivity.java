package com.kenneth.record;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class mainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //自動開啟 Service
        Intent intent = new Intent(mainActivity.this, recordService.class);
        startService(intent);
        finish();
    }
}
