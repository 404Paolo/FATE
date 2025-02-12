package com.example.tutoriapp;


import java.util.List;
import java.util.Map;

class Question {
    String questionId;
    String questionText;
    String questionType;
    List<String> options;
    String correctAnswer;
    String audio;
    float scoreThreshold;
}

class Module {
    List<Question> conceptual;
    List<Question> listening;
    List<Question> speaking;
}

class ModulesContainer {
    Map<String, Module> modules;
}