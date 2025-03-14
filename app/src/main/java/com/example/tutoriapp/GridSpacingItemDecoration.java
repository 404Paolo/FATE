package com.example.tutoriapp;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int spanCount; // Number of columns
    private int spacing; // Spacing between items
    private int edgeSpacing; // Spacing at the edges
    private boolean includeEdge; // Whether to include edge spacing

    public GridSpacingItemDecoration(int spanCount, int spacing, int edgeSpacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.edgeSpacing = edgeSpacing;
        this.includeEdge = includeEdge;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // Item position
        int column = position % spanCount; // Item column

        if (includeEdge) {
            // Apply edge spacing
            outRect.left = edgeSpacing - column * spacing / spanCount;
            outRect.right = (column + 1) * spacing / spanCount;

            // Apply top edge spacing to the first row
            if (position < spanCount) {
                outRect.top = edgeSpacing;
            }
            outRect.bottom = edgeSpacing; // Apply bottom edge spacing
        } else {
            // Apply spacing only between items (no edge spacing)
            outRect.left = column * spacing / spanCount;
            outRect.right = spacing - (column + 1) * spacing / spanCount;
            if (position >= spanCount) {
                outRect.top = spacing; // Apply top spacing for rows after the first
            }
        }
    }
}
