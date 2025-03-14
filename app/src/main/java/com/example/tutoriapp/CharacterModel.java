package com.example.tutoriapp;

public class CharacterModel {
    private String character;
    private String type;

    public CharacterModel(String character, String type) {
        this.character = character;
        this.type = type;
    }

    public String getCharacter() { return character; }
    public String getType() { return type; }
}

