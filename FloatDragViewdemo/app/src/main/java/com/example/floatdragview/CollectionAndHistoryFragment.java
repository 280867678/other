package com.example.floatdragview;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CollectionAndHistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CollectionAndHistoryFragment extends DownloadCenterTabBaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CollectionAndHistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CollectionAndHistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CollectionAndHistoryFragment newInstance(String param1, String param2) {
        CollectionAndHistoryFragment fragment = new CollectionAndHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collection_and_history, container, false);
    }


    public static CollectionAndHistoryFragment m1739a(int i) {
        CollectionAndHistoryFragment collectionAndHistoryFragment = new CollectionAndHistoryFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt("page_type", i);
        collectionAndHistoryFragment.setArguments(bundle);
        return collectionAndHistoryFragment;
    }


    @Override
    public void mo1740a() {

    }

    @Override
    public void mo1735b() {

    }
}