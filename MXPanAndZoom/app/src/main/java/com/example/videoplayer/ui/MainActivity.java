package com.example.videoplayer.ui;


import android.os.Bundle;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.videoplayer.R;
import com.example.videoplayer.base.BaseActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

//    private ActivityMainBinding binding;
    private List<Fragment> fragments= new ArrayList<>();
    private ViewPager2 vp2;
    private BottomNavigationView bottomNav;


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        fragments = new ArrayList<>();
//        fragments.add(new MovieFoldersListFragment());
//        fragments.add(new SettingFragment());
//
//        initView();
//    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    public void initView() {
        vp2 = findViewById(R.id.vp2);
        bottomNav = findViewById(R.id.bottom_nav);

        fragments.add(new MovieFoldersListFragment());
        fragments.add(new SettingFragment());
        vp2.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragments.get(position);
            }

            @Override
            public int getItemCount() {
                return fragments.size();
            }
        });

        vp2.setUserInputEnabled(false);

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            vp2.setCurrentItem(item.getOrder(), true);
            return true;
        });
    }

    private long exitTime = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            ToastUtils.showShort("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            ActivityUtils.finishAllActivities(true);
        }
    }
}


