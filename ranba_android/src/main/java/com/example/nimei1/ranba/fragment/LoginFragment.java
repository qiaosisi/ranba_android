package com.example.nimei1.ranba.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.nimei1.ranba.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private int countSeconds = 60;//倒计时秒数
    private EditText mobile_login, yanzhengma;
    private Button getMsgCode, login_btn;
    private Context mContext;
    private String usersuccess;
    private String userinfomsg;
    // Http请求
    OkHttpClient client = new OkHttpClient();
    String token;
    String userId;

    private Handler mCountHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (countSeconds > 0) {
                --countSeconds;
                getMsgCode.setText("(" + countSeconds + ")后获取验证码");
                mCountHandler.sendEmptyMessageDelayed(0, 1000);
            } else {
                countSeconds = 60;
                getMsgCode.setText("请重新获取验证码");
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        mobile_login = (EditText) getActivity().findViewById(R.id.mobile_login);
        getMsgCode = (Button) getActivity().findViewById(R.id.getMsgCode);
        yanzhengma = (EditText) getActivity().findViewById(R.id.yanzhengma);
        login_btn = (Button) getActivity().findViewById(R.id.login_btn);

        getMsgCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (countSeconds == 60) {
                    String mobile = mobile_login.getText().toString();
                    Log.e("tag", "mobile==" + mobile);
                    getMobiile(mobile);
                } else {
                    Toast.makeText(getActivity(), "不能重复发送验证码", Toast.LENGTH_SHORT).show();
                }
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        super.onActivityCreated(savedInstanceState);
    }

    //获取信息进行登录
    public void login() {
        String mobile = mobile_login.getText().toString().trim();
        String verifyCode = yanzhengma.getText().toString().trim();
        Request request = new Request.Builder().get().url("这里换成你的请求登录的接口").build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "Get 失败",Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String responseStr = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObject = new JSONObject(responseStr);
                            Log.e("tag", "登陆的result=" + jsonObject);
                            String success = jsonObject.optString("success");
                            String data = jsonObject.optString("data");
                            String msg=jsonObject.optString("msg");
                            if ("true".equals(success)) {
                                Log.e("tag","登陆的data="+data);
                                JSONObject json = new JSONObject(data);
                                token = json.optString("token");
                                userId = json.optString("userId");
                            }else{
                                Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    //获取验证码信息，判断是否有手机号码
    private void getMobiile(String mobile) {
        if ("".equals(mobile)) {
            Log.e("tag", "mobile=" + mobile);
            new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("手机号码不能为空").setCancelable(true).show();
        } else if (isMobileNO(mobile) == false) {
            new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("请输入正确的手机号码").setCancelable(true).show();
        } else {
            Log.e("tag", "输入了正确的手机号");
            requestVerifyCode(mobile);
        }
    }
    // 获取验证码信息，进行验证码请求
    private void requestVerifyCode(String mobile) {
        FormBody formBody = new FormBody
                .Builder()
                .add("mobile", mobile)
                .build();

        Request  request =  new Request.Builder()
                .url("这里是你请求的验证码接口，让后台给你，参数什么的加在后面")
                .post(formBody)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "Post Parameter 失败",Toast.LENGTH_SHORT);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                        JSONObject jsonObject2 = new JSONObject(responseStr);
                        Log.e("tag", "jsonObject2" + jsonObject2);
                        String state = jsonObject2.getString("success");
                        String verifyCode = jsonObject2.getString("msg");
                        Log.e("tag", "获取验证码==" + verifyCode);
                        if ("true".equals(state)) {
                            Toast.makeText(getActivity(), verifyCode, Toast.LENGTH_SHORT).show();
                            startCountBack();//这里是用来进行请求参数的
                        } else {
                            Toast.makeText(getActivity(), verifyCode, Toast.LENGTH_SHORT).show();
                        }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
    //使用正则表达式判断电话号码
    public static boolean isMobileNO(String tel) {
        Pattern p = Pattern.compile("^(13[0-9]|15([0-3]|[5-9])|14[5,7,9]|17[1,3,5,6,7,8]|18[0-9])\\d{8}$");
        Matcher m = p.matcher(tel);
        System.out.println(m.matches() + "---");
        return m.matches();
    }
    //获取验证码信息,进行计时操作
    private void startCountBack() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getMsgCode.setText(countSeconds + "");
                mCountHandler.sendEmptyMessage(0);
            }
        });
    }
}
