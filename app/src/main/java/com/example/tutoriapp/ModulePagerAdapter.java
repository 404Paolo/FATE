package com.example.tutoriapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ModulePagerAdapter extends FragmentStateAdapter {
    private final List<ModulePageFragment> modulePageFragments = new ArrayList<>();
    public ModulePagerAdapter(FragmentActivity activity, List<Page> pages){
        super(activity);
        for(Page p : pages){
            modulePageFragments.add(ModulePageFragment.newInstance(
                    p.pageId,
                    p.pageType,
                    p.subtopic,
                    p.subtopicTitle,
                    p.contentBlocks
            ));
        }
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return modulePageFragments.get(position);
    }

    @Override
    public int getItemCount(){
        return modulePageFragments.size();
    }
}
