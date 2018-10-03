package com.example.nimei1.ranba;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.nimei1.ranba.fragment.LoginFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        SharedPreferences userSettings= getActivity().getSharedPreferences("setting", 0);
        String userName = userSettings.getString("USER_NAME","***");

        if (!userName.equals("***")) {
            // 设置用户名
            TextView textView = rootView.findViewById(R.id.usreName);
            textView.setText(userName);

            // 隐藏登录按钮
            Button button = rootView.findViewById(R.id.loginBtn);
            button.setVisibility(View.INVISIBLE);
        }

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        Button btnLogin = (Button) getActivity().findViewById(R.id.loginBtn);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                FragmentTransaction  fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.main_frame,new LoginFragment());
                fragmentTransaction.commit();
            }
        });
    }

}
