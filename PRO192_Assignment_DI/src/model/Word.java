package model;

public class Word {
    private final String word;
    private final String meaning;

    public Word(String word, String meaning){
        this.word = word;
        this.meaning = meaning;
    }

    public String getWord() { return word; }

    public String getMeaning() {
        return meaning;
    }

}
