package com.example.nimei1.ranba.activity;

import android.content.Intent;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nimei1.ranba.Constants;
import com.example.nimei1.ranba.R;
import com.example.nimei1.ranba.utils.HttpUtil;
import com.example.nimei1.ranba.utils.ProgressListener;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PublishActivity extends BaseActivity implements View.OnClickListener{

    public static final String TAG = PublishActivity.class.getName();
    private ProgressBar post_progress;
    private TextView post_text;

    // 视频路径
    private String mPath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        initView();
        initData();

        post_progress = (ProgressBar) findViewById(R.id.post_progress);
        post_text = (TextView) findViewById(R.id.post_text);

        // 设置进度条背景色
        setColors(post_progress,0xFFFFFFFF,0xFF3CB371);
    }

    private void initView() {
        Button btnPublish = findViewById(R.id.btnPublish);

        btnPublish.setOnClickListener(this);
        setLoadingCancelable(true);

    }
    private void initData() {
        Intent intent = getIntent();
        //选择的视频的本地播放地址
        mPath = intent.getStringExtra("path");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(!isLoading()){
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnPublish:

                File file = new File(mPath);
                String postUrl = Constants.WEB_URL +"api/upload";

                HttpUtil.postFile(postUrl, new ProgressListener() {
                    @Override
                    public void onProgress(long currentBytes, long contentLength, boolean done) {
                        Log.i(TAG, "currentBytes==" + currentBytes + "==contentLength==" + contentLength + "==done==" + done);
                        int progress = (int) (currentBytes * 100 / contentLength);
                        post_progress.setProgress(progress);
                        post_text.setText(progress + "%");
                    }
                }, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Toast.makeText(PublishActivity.this,"失败",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response != null) {
                            String result = response.body().string();
                            Log.i(TAG, "result===" + result);

                            Toast.makeText(PublishActivity.this,result,Toast.LENGTH_SHORT).show();
                        }
                    }
                }, file);
                break;
        }
    }

    public void setColors(ProgressBar progressBar, int backgroundColor, int progressColor) {
        //Background
        ClipDrawable bgClipDrawable = new ClipDrawable(new ColorDrawable(backgroundColor), Gravity.LEFT, ClipDrawable.HORIZONTAL);
        bgClipDrawable.setLevel(10000);
        //Progress
        ClipDrawable progressClip = new ClipDrawable(new ColorDrawable(progressColor), Gravity.LEFT, ClipDrawable.HORIZONTAL);
        //Setup LayerDrawable and assign to progressBar
        Drawable[] progressDrawables = {bgClipDrawable, progressClip/*second*/, progressClip};
        LayerDrawable progressLayerDrawable = new LayerDrawable(progressDrawables);
        progressLayerDrawable.setId(0, android.R.id.background);
        progressLayerDrawable.setId(1, android.R.id.secondaryProgress);
        progressLayerDrawable.setId(2, android.R.id.progress);

        progressBar.setProgressDrawable(progressLayerDrawable);
    }
}
