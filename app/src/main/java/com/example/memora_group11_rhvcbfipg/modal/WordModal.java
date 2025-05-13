package com.example.memora_group11_rhvcbfipg.modal;

public class WordModal {

    private int folderId;
    private int wordId;
    private String word;
    private String meaning;



    public WordModal(int folderId, int wordId, String word, String meaning) {
        this.folderId = folderId;
        this.wordId = wordId;
        this.word = word;
        this.meaning = meaning;
    }

    public String getWord() {
        return word;
    }

    public String getMeaning() {
        return meaning;
    }

}
