package com.amplifire.traves.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuizItemDao {
    public String answer;
    public String question;
    public Map<String, String> choices;

    public String getAnswer() {
        return answer;
    }

    public String getQuestion() {
        return question;
    }

    public Map<String, String> getChoices() {
        return choices;
    }
}
