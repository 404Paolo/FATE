package com.example.tutoriapp;

import com.google.gson.annotations.SerializedName;
import java.util.Map;
import java.util.List;

public class PhenomeData {
    @SerializedName("phonemes")
    private Map<String, Phoneme> phonemes;

    @SerializedName("errors")
    private Map<String, ErrorInfo> errors;

    @SerializedName("constraints")
    private Map<String, Constraint> constraints;

    public Map<String, Phoneme> getPhonemes() {
        return phonemes;
    }

    public Map<String, ErrorInfo> getErrors() {
        return errors;
    }

    public Map<String, Constraint> getConstraints() {
        return constraints;
    }
}

class Phoneme {
    @SerializedName("errorId")
    private String errorId;

    @SerializedName("constraintId")
    private String constraintId;

    public String getErrorId() {
        return errorId;
    }

    public String getConstraintId() {
        return constraintId;
    }
}

class ErrorInfo {
    @SerializedName("error")
    private String error;

    @SerializedName("meaning")
    private String meaning;

    @SerializedName("constraint")
    private List<String> constraints;

    public String getError() {
        return error;
    }

    public String getMeaning() {
        return meaning;
    }

    public List<String> getConstraints() {
        return constraints;
    }
}

class Constraint {
    @SerializedName("phoneme")
    private String phoneme;

    @SerializedName("feedback")
    private String feedback;

    public String getPhoneme() {
        return phoneme;
    }

    public String getFeedback() {
        return feedback;
    }
}
