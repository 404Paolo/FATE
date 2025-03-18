package com.example.tutoriapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PracticeFragment extends Fragment {
    private CharacterAdapter characterAdapter;
    private List<CharacterModel> characterList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_practice, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewCharacters);
        int spanCount = 3; // Number of columns
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

        // Define spacing values
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing); // Spacing between items
        int edgeSpacing = getResources().getDimensionPixelSize(R.dimen.grid_edge_spacing); // Spacing at the edges
        boolean includeEdge = true; // Whether to include edge spacing

        // Add the item decoration to the RecyclerView
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, edgeSpacing, includeEdge));

        loadCharacters();

        characterAdapter = new CharacterAdapter(getContext(), characterList);
        recyclerView.setAdapter(characterAdapter);

        return view;
    }

    private void loadCharacters() {
        String[] initials = {"b", "p", "m", "f", "d", "t", "n", "l", "g", "k", "h", "j", "q", "x", "z", "c", "s", "zh", "ch", "sh", "r", "w", "y"};

        for (String initial : initials) {
            characterList.add(new CharacterModel(initial, "initial"));
        }
        String[] finals = {"a", "o", "e", "ai", "ei", "ao", "ou", "an", "ang", "en", "eng", "ong", "er", "u", "ua", "uo", "uai", "ui", "uan", "uang"};
        for (String finalChar : finals) {
            characterList.add(new CharacterModel(finalChar, "final"));
        }
        String[] tones = {"mā", "má", "mǎ", "mà", "ma"};
        for (String tone : tones) {
            characterList.add(new CharacterModel(tone, "tone"));
        }
    }
}




