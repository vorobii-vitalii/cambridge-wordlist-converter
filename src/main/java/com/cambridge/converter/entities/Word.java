package com.cambridge.converter.entities;

import java.util.Objects;

public class Word {

    private String name;
    private String type;
    private String meaning;
    private String translation;

    public Word() {
    }

    public Word(String name, String type, String meaning, String translation) {
        this.name = name;
        this.type = type;
        this.meaning = meaning;
        this.translation = translation;
    }

    @Override
    public String toString() {
        return "Word{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", meaning='" + meaning + '\'' +
                ", translation='" + translation + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, meaning, translation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word = (Word) o;
        return Objects.equals(name, word.name) &&
                Objects.equals(type, word.type) &&
                Objects.equals(meaning, word.meaning) &&
                Objects.equals(translation, word.translation);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }
}
