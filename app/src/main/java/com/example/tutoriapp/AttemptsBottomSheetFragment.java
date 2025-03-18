package com.example.tutoriapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.Serializable;
import java.util.List;

public class AttemptsBottomSheetFragment extends BottomSheetDialogFragment implements AttemptsAdapter.OnAttemptClickListener{

    private static final String ARG_ATTEMPT_LIST = "attempt_list";
    private RecyclerView attemptListRecycler;
    private AttemptsAdapter attemptsAdapter;

    private OnDataChangedListener listener;
    private int selectedIndex = -1;

    public interface OnDataChangedListener {
        void onDataChanged(int selectedIndex);
    }
    public static AttemptsBottomSheetFragment newInstance(List<AttemptDoc> attemptList) {
        AttemptsBottomSheetFragment fragment = new AttemptsBottomSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ATTEMPT_LIST, (Serializable) attemptList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_attempthistory, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve data
        List<AttemptDoc> attemptList = (List<AttemptDoc>) getArguments().getSerializable(ARG_ATTEMPT_LIST);
        attemptListRecycler = view.findViewById(R.id.attemptRecycler);
        attemptListRecycler.post(() -> {
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            int maxHeight = (int) (screenHeight * 0.25);

            ViewGroup.LayoutParams params = attemptListRecycler.getLayoutParams();
            params.height = maxHeight;
            attemptListRecycler.setLayoutParams(params);
        });
        attemptsAdapter = new AttemptsAdapter(attemptList, this);
        attemptListRecycler.setAdapter(attemptsAdapter);
    }


    public void setOnDataChangedListener(OnDataChangedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onAttemptSelected(int index) {
        if (listener != null) {
            Log.d("Paolo", "is not null2");
            listener.onDataChanged(index);
        }
        dismiss();
    }
}
