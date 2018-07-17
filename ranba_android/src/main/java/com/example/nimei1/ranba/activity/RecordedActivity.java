package com.example.nimei1.ranba.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.nimei1.ranba.Constants;
import com.example.nimei1.ranba.R;
import com.example.nimei1.ranba.camera.SensorControler;
import com.example.nimei1.ranba.gpufilter.SlideGpuFilterGroup;
import com.example.nimei1.ranba.gpufilter.helper.MagicFilterType;
import com.example.nimei1.ranba.widget.CameraView;
import com.example.nimei1.ranba.widget.CircularProgressView;
import com.example.nimei1.ranba.widget.FocusImageView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by qy on 2018/7/25.
 * desc 视频录制
 * 主要包括 音视频录制、断点续录、对焦等功能
 */

public class RecordedActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener, SensorControler.CameraFocusListener, SlideGpuFilterGroup.OnFilterChangeListener {

    private CameraView mCameraView;
    private CircularProgressView mCapture;
    private FocusImageView mFocus;
    private ImageView mBeautyBtn;
    private ImageView mFilterBtn;
    private ImageView mCameraChange;
    private static final int maxTime = 20000;//最长录制20s
    private boolean pausing = false;
    private boolean recordFlag = false;//是否正在录制

    private int WIDTH = 720,HEIGHT = 1280;

    private long timeStep = 50;//进度条刷新的时间
    long timeCount = 0;//用于记录录制时间
    private boolean autoPausing = false;
    ExecutorService executorService;
    private SensorControler mSensorControler;

    private MediaScannerConnection mMediaScannerConnection;
    // 存储权限code
    public static final int EXTERNAL_STORAGE_REQ_CODE = 10 ;
    // 相机权限code
    private final int CAMERA_REQUEST_CODE = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorde);
        executorService = Executors.newSingleThreadExecutor();
        mSensorControler = SensorControler.getInstance();
        mSensorControler.setCameraFocusListener(this);

        // 创建MediaScannerConnection
        mMediaScannerConnection = new MediaScannerConnection(this, null);
        // 调用connect
        mMediaScannerConnection.connect();

        initView();
    }

    private void initView() {
        mCameraView = (CameraView) findViewById(R.id.camera_view);
        mCapture = (CircularProgressView) findViewById(R.id.mCapture);
        mFocus = (FocusImageView) findViewById(R.id.focusImageView);
        mBeautyBtn = (ImageView) findViewById(R.id.btn_camera_beauty);
        mFilterBtn = (ImageView) findViewById(R.id.btn_camera_filter);
        mCameraChange = (ImageView) findViewById(R.id.btn_camera_switch);

        mBeautyBtn.setOnClickListener(this);
        mCameraView.setOnTouchListener(this);
        mCameraView.setOnFilterChangeListener(this);
        mCameraChange.setOnClickListener(this);
        mCapture.setTotal(maxTime);
        mCapture.setOnClickListener(this);
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        mCameraView.onTouch(event);
        if (mCameraView.getCameraId() == 1) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                float sRawX = event.getRawX();
                float sRawY = event.getRawY();
                float rawY = sRawY * Constants.screenWidth / Constants.screenHeight;
                float temp = sRawX;
                float rawX = rawY;
                rawY = (Constants.screenWidth - temp) * Constants.screenHeight / Constants.screenWidth;

                Point point = new Point((int) rawX, (int) rawY);
                mCameraView.onFocus(point, callback);
                mFocus.startFocus(new Point((int) sRawX, (int) sRawY));
        }
        return true;
    }
    Camera.AutoFocusCallback callback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            //聚焦之后根据结果修改图片
            Log.e("hero","----onAutoFocus===="+success);
            if (success) {
                mFocus.onFocusSuccess();
            } else {
                //聚焦失败显示的图片
                mFocus.onFocusFailed();

            }
        }
    };
    @Override
    public void onFocus() {
        if (mCameraView.getCameraId() == 1) {
            return;
        }
        Point point = new Point(Constants.screenWidth / 2, Constants.screenHeight / 2);
        mCameraView.onFocus(point, callback);
    }
    @Override
    public void onBackPressed() {
        if (recordFlag) {
            recordFlag = false;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCameraView.onResume();
        Toast.makeText(this,R.string.change_filter,Toast.LENGTH_SHORT).show();
        if (recordFlag && autoPausing) {
            mCameraView.resume(true);
            autoPausing = false;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (recordFlag && !pausing) {
            mCameraView.pause(true);
            autoPausing = true;
        }
        mCameraView.onPause();
    }
    @Override
    public void onFilterChange(final MagicFilterType type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (type == MagicFilterType.NONE){
                    Toast.makeText(RecordedActivity.this,"当前没有设置滤镜--"+type,Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(RecordedActivity.this,"当前滤镜切换为--"+type,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_camera_switch:
                mCameraView.switchCamera();
                if (mCameraView.getCameraId() == 1){
                    //前置摄像头 使用美颜
                    mCameraView.changeBeautyLevel(3);
                }else {
                    //后置摄像头不使用美颜
                    mCameraView.changeBeautyLevel(0);
                }
                break;
            case R.id.mCapture:
                if (!recordFlag) {
                    // 请求权限
                    requestPermission();
                } else if (!pausing) {
                    mCameraView.pause(false);
                    pausing = true;
                } else {
                    mCameraView.resume(false);
                    pausing = false;
                }
                break;
            case R.id.btn_camera_beauty:

                if (mCameraView.getCameraId() == 0){
                    Toast.makeText(this, "后置摄像头 不使用美白磨皮功能", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(RecordedActivity.this)
                        .setSingleChoiceItems(new String[]{"关闭", "1", "2", "3", "4", "5"}, mCameraView.getBeautyLevel(),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        mCameraView.changeBeautyLevel(which);
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton("取消", null)
                        .show();
                break;
        }
    }
    Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            recordFlag = true;
            pausing = false;
            autoPausing = false;
            timeCount = 0;
            long time = System.currentTimeMillis();
            String savePath = Constants.getPath("record/", time + ".mp4");

            try {
                mCameraView.setSavePath(savePath);
                mCameraView.startRecord();
                while (timeCount <= maxTime && recordFlag) {
                    if (pausing || autoPausing) {
                        continue;
                    }
                    mCapture.setProcess((int) timeCount);
                    Thread.sleep(timeStep);
                    timeCount += timeStep;
                }
                recordFlag = false;
                mCameraView.stopRecord();
                if (timeCount < 2000) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RecordedActivity.this, "录像时间太短", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    recordComplete(savePath);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private void recordComplete(final String path) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCapture.setProcess(0);
                scanFile(path,true);
                Toast.makeText(RecordedActivity.this, "文件保存路径：" + path, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * @param filePath 保存到本地的文件路径
     * @param isVideo  是否是视频
     */
    private void scanFile(String filePath, boolean isVideo) {
        if (mMediaScannerConnection != null) {
            if (isVideo) {
                //主动扫描视频
                mMediaScannerConnection.scanFile(filePath, "video/mp4");
            } else {
                //主动扫描图片
                mMediaScannerConnection.scanFile(filePath, "image/jpeg");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaScannerConnection != null) {
            //释放连接
            mMediaScannerConnection.disconnect();
        }
    }

    // 权限申请
    public void requestPermission(){
        //判断当前Activity是否已经获得了该权限
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            //如果App的权限申请曾经被用户拒绝过，就需要在这里跟用户做出解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this,"please give me the permission",Toast.LENGTH_SHORT).show();
            } else {
                //进行存储权限请求
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        EXTERNAL_STORAGE_REQ_CODE);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // 第一次请求权限时，用户如果拒绝，下一次请求shouldShowRequestPermissionRationale()返回true
            // 向用户解释为什么需要这个权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                new AlertDialog.Builder(this)
                        .setMessage("申请相机权限")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //申请相机权限
                                ActivityCompat.requestPermissions(RecordedActivity.this,
                                        new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                            }
                        })
                        .show();
            } else {
                //申请相机权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
            }
        }

        // 已经申请直接录制
        executorService.execute(recordRunnable);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_STORAGE_REQ_CODE: {
                // 如果请求被拒绝，那么通常grantResults数组为空
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //申请成功，进行录制
                    executorService.execute(recordRunnable);
                } else {
                    //申请失败，可以继续向用户解释。
                }
                return;
            }
        }
    }
}
