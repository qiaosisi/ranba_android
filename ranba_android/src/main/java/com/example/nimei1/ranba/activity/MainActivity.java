package com.example.nimei1.ranba.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.nimei1.ranba.AccountFragment;
import com.example.nimei1.ranba.BottomNavigationViewHelper;
import com.example.nimei1.ranba.GroupFragment;
import com.example.nimei1.ranba.HomeFragment;
import com.example.nimei1.ranba.R;
import com.example.nimei1.ranba.UploadFragment;
import com.example.nimei1.ranba.fragment.LoginFragment;

public class MainActivity extends BaseActivity {

    private BottomNavigationView mMainNav;
    private FrameLayout mMainFrame;

    private HomeFragment homeFragment;
    private UploadFragment uploadFragment;
    private AccountFragment accountFragment;
    private GroupFragment groupFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainFrame = (FrameLayout) findViewById(R.id.main_frame);
        mMainNav = (BottomNavigationView) findViewById(R.id.main_nav);
        BottomNavigationViewHelper.disableShiftMode(mMainNav);

        homeFragment = new HomeFragment();
        uploadFragment = new UploadFragment();
        accountFragment = new AccountFragment();
        groupFragment = new GroupFragment();

        setFragment(homeFragment);

        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        mMainNav.setItemBackgroundResource(R.color.bg_toolbar);
                        setFragment(homeFragment);
                        return true;
                    case R.id.nav_group:
                        mMainNav.setItemBackgroundResource(R.color.bg_toolbar);
                        setFragment(groupFragment);
                        return true;
                    case R.id.nav_upload:
                        mMainNav.setItemBackgroundResource(R.color.bg_toolbar);
                        setFragment(uploadFragment);
                        return true;
                    case R.id.nav_account:
                        mMainNav.setItemBackgroundResource(R.color.bg_toolbar);
                        setFragment(accountFragment);
                        return true;
                        default:
                            return false;
                }
            }

        });

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction  fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();
    }

}
