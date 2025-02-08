package com.example.tutoriapp;

import java.util.ArrayList;
import java.util.List;

public class QuestionManager {
    private List<Question> questions;
    private int currentIndex = 0;

    public QuestionManager() {
        questions = new ArrayList<>();
        loadQuestions();
    }

    private void loadQuestions() {
        questions.add(new Question("Pinyin is a language", null, "False", new String[]{"True", "False", "Maybe"}, null, "conceptual"));
        questions.add(new Question("A romanization system allows us to change writing from a script, such as Chinese or Russian, into the Latin (Roman) script", null, "True", new String[]{"True", "False", "Maybe"}, null, "conceptual"));
        questions.add(new Question("Initials are vowels", null, "False", new String[]{"True", "False", "Maybe"}, null, "conceptual"));
        questions.add(new Question("Tones are placed on top of the finals", null, "False", new String[]{"False", "True", "Maybe"}, null, "conceptual"));
        questions.add(new Question("When people refer to the “Chinese” language, they are usually referring to Mandarin", null, "True", new String[]{"True", "False", "Maybe"}, null, "conceptual"));
        questions.add(new Question("Identify this sound", null, "Dog", new String[]{"Cat", "Dog", "Bird"}, "mao", "listening"));
        questions.add(new Question("Pronounce the character shown below", "zh", "zh", null, null, "pronunciation"));
        questions.add(new Question("Pronounce the character shown below", "ch", "ch", null, null, "pronunciation"));
    }

    public Question getCurrentQuestion() {
        return questions.get(currentIndex);
    }

    public Question getNextQuestion() {
        if (currentIndex < questions.size() - 1) {
            currentIndex++;
        }
        return questions.get(currentIndex);
    }

    public Question getPreviousQuestion() {
        if (currentIndex > 0) {
            currentIndex--;
        }
        return questions.get(currentIndex);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
