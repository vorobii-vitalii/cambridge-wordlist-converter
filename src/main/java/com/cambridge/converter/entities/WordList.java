package com.cambridge.converter.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WordList {

    private String name;
    private List<Word> words;

    public WordList() {
        words = new ArrayList<>();
    }

    public WordList(String name, List<Word> words) {
        this.name = name;
        this.words = words;
    }

    public void addWord(Word word) {
        words.add(word);
    }

    @Override
    public String toString() {
        return "WordList{" +
                "name='" + name + '\'' +
                ", words=" + words +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordList wordList = (WordList) o;
        return Objects.equals(name, wordList.name) &&
                Objects.equals(words, wordList.words);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, words);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }
}
