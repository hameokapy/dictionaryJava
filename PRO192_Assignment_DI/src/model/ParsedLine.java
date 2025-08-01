package model;

public class ParsedLine {
    private final String word;
    private final IndexEntry indexEntry;

    public ParsedLine(String word, IndexEntry indexEntry) {
        this.word = word;
        this.indexEntry = indexEntry;
    }

    public String getWord() {
        return word;
    }

    public IndexEntry getIndexEntry() {
        return indexEntry;
    }
}
