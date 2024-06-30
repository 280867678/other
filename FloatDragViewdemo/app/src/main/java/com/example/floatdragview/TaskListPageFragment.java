package com.example.floatdragview;

import android.app.TaskInfo;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.floatdragview.bin.DownloadTaskInfo;
import com.example.floatdragview.util.DownloadCenterControl;


public class TaskListPageFragment extends DownloadCenterTabBaseFragment {

    public TaskListPageFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
Button button;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_task_list_page, container, false);
        button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadCenterControl.m10710a(view.getContext(), new DownloadTaskInfo(), "dlcenter_total_bar");
            }
        });

        return view;
    }


    public static TaskListPageFragment m9055a(int i) {
        TaskListPageFragment taskListPageFragment = new TaskListPageFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt("page_index", i);
        taskListPageFragment.setArguments(bundle);
        return taskListPageFragment;
    }

    public final void m9050a(boolean z) {
//        if (this.f20273c != null && this.f20293w) {
//            this.f20273c.setLoadingMoreEnabled(!z);
//        }
//        if (this.f20294x != z) {
//            this.f20294x = z;
//            if (this.f20274d != null) {
//                this.f20274d.m9012a(z);
//            }
//        }
    }



    public final boolean m9040g() {
//        if (this.f20274d != null) {
//            TaskListItemArray taskListItemArray = this.f20274d.f20312a;
//            if (taskListItemArray.f20613a == null || taskListItemArray.f20613a.size() == 0) {
//                return false;
//            }
//            return taskListItemArray.f20613a.size() != 1 || taskListItemArray.f20613a.get(0).f20351a == 0;
//        }
        return false;
    }


    @Override
    public void mo1740a() {

    }

    @Override
    public void mo1735b() {

    }









}