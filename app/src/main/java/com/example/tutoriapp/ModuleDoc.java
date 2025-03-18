package com.example.tutoriapp;

import com.google.firebase.Timestamp;
import java.util.List;
import java.util.Map;

public class ModuleDoc {
    public String moduleName;
    public int latestAttemptId;
    public int highestScoredAttemptId;
    public float latestScore;
    public float averageScore;
    public int totalAttempts;

    public float highestScore;

    public List<AttemptDoc> attempts;
}