package com.wei.music.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.wei.music.R;
import com.wei.music.service.MusicService;
import com.wei.music.utils.ToolUtil;
import com.wei.music.view.EqualizerView;

import androidx.viewpager2.widget.ViewPager2;

import java.util.List;

import androidx.fragment.app.Fragment;

import com.wei.music.fragment.MoreFragment;
import com.wei.music.fragment.AboutFragment;

import java.util.ArrayList;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.tabs.TabLayout.Tab;

public class EqualizerActivity extends AppCompatActivity {

    private EqualizerView mEqualizerView;
    private int audioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equalizer);
        SwitchMaterial equalizerSwitch = findViewById(R.id.activityequalizerButton1);
        mEqualizerView = findViewById(R.id.equalizerview);
//        audioId = ToolUtil.readInt("AudioId");

        equalizerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEqualizerView.open();
                } else {
                    mEqualizerView.close();
                }
            }
        });

        /**
         * 直接在onCreate当中init会出现onMeasure没有执行就计算均衡器视图点位导致绘制错误。
         */
        mEqualizerView.post(new Runnable() {
            @Override
            public void run() {
                mEqualizerView.initAudioSessionId(MusicService.FIXED_AUDIO_SESSION_ID);
            }
        });
    }

}
