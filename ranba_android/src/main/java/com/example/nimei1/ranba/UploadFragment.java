package com.example.nimei1.ranba;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.nimei1.ranba.activity.AudioEditorActivity;
import com.example.nimei1.ranba.activity.RecordedActivity;
import com.example.nimei1.ranba.activity.VideoConnectActivity;
import com.example.nimei1.ranba.activity.VideoSelectActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class UploadFragment extends Fragment implements View.OnClickListener{

    public UploadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        Button recordBtn = (Button)view.findViewById(R.id.record_activity);
        Button selectBtn = (Button) view.findViewById(R.id.select_activity);
        Button audioBtn = (Button) view.findViewById(R.id.audio_activity);
        Button videoBtn = (Button) view.findViewById(R.id.video_connect);

        recordBtn.setOnClickListener(this);
        selectBtn.setOnClickListener(this);
        audioBtn.setOnClickListener(this);
        videoBtn.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.record_activity:
                startActivity(new Intent(getActivity(),RecordedActivity.class));
                break;
            case R.id.select_activity:
                VideoSelectActivity.openActivity(getActivity());
                break;
            case R.id.audio_activity:
                startActivity(new Intent(getActivity() , AudioEditorActivity.class));
                break;
            case R.id.video_connect:
//                Toast.makeText(this,"该功能还未完成！！！",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity() , VideoConnectActivity.class));
                break;
        }
    }
}
