package com.example.tutoriapp;

import java.io.Serializable;
import java.util.Map;

public class AttemptDoc implements Serializable {
    public String attemptId;
    public Map<String, Boolean> questionMap;
    public float percentage;
    public long timestamp;
}

class Answer {
    public int accuracy;
    public boolean isCorrect;
    public String answer;
    public String questionText;
    public String questionType;
}

