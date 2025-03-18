package com.example.tutoriapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ModulePageFragment extends Fragment {
    private static final String ARG_PAGEID = "pageId";
    private static final String ARG_PAGETYPE = "pageType";
    private static final String ARG_SUBTOPIC= "subtopic";
    private static final String ARG_SUBTOPICTITLE = "subtopicTitle";
    private static final String ARG_CONTENTBLOCKS = "contentBlocks";
    private String pageId, pageType, subtopic, subtopicTitle;
    private ArrayList<ContentBlock> contentBlocks;

    public static ModulePageFragment newInstance(String pageId, String pageType, String subtopic, String subtopicTitle, ArrayList<ContentBlock> contentBlocks){
        ModulePageFragment fragment = new ModulePageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PAGEID, pageId);
        args.putString(ARG_PAGETYPE, pageType);
        args.putString(ARG_SUBTOPIC, subtopic);
        args.putString(ARG_SUBTOPICTITLE, subtopicTitle);
        args.putParcelableArrayList(ARG_CONTENTBLOCKS, contentBlocks);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageId = getArguments().getString(ARG_PAGEID);
            pageType = getArguments().getString(ARG_PAGETYPE);
            subtopic = getArguments().getString(ARG_SUBTOPIC);
            subtopicTitle = getArguments().getString(ARG_SUBTOPICTITLE);
            contentBlocks = getArguments().getParcelableArrayList(ARG_CONTENTBLOCKS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modulepage, container, false);
        RecyclerView contentBlocksRecyclerView = view.findViewById(R.id.contentblock_recycler);
        TextView subtopicTitle = view.findViewById(R.id.subtopicTitle);
        subtopicTitle.setText(this.subtopicTitle);

        if(pageType.equals("interactive")){
            contentBlocksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            contentBlocksRecyclerView.setAdapter(new InteractiveContentBlocksAdapter(contentBlocks, getContext()));
        }

        else{
            contentBlocksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            contentBlocksRecyclerView.setAdapter(new TextContentBlocksAdapter(contentBlocks));
        }

        return view;
    }
}
