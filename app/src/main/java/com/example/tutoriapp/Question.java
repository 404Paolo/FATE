package com.example.tutoriapp;

public class Question {
    private String questionText;
    private String characterToPronounce;
    private String correctAnswer;
    private String[] choices;
    private String audioFile;
    private String questionType;

    public Question(String questionText, String characterToPronounce, String correctAnswer, String[] choices, String audioFile, String questionType) {
        this.questionText = questionText;
        this.characterToPronounce = characterToPronounce;
        this.correctAnswer = correctAnswer;
        this.choices = choices;
        this.audioFile = audioFile;
        this.questionType = questionType;
    }

    public String getQuestionText() { return questionText; }
    public String getCharacterToPronounce() {return characterToPronounce; }
    public String getCorrectAnswer() { return correctAnswer; }
    public String[] getChoices() { return choices; }
    public String getAudioFile() { return audioFile; }
    public String getQuestionType() { return questionType; }
}