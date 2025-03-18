package com.example.tutoriapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PerformanceFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_performancemodules, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.moduleDocsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<ModuleDoc> moduleDocs = FirebaseUtil.loadPerformanceData(getContext());
        TextView prompt = view.findViewById(R.id.performancePrompt);

        if(moduleDocs.size() > 0) {
            PerformanceCardAdapter adapter = new PerformanceCardAdapter(moduleDocs, getContext());
            recyclerView.setAdapter(adapter);
            prompt.setVisibility(View.GONE);
        }

        return view;
    }
}