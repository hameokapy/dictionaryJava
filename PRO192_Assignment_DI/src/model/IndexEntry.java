package model;

public class IndexEntry {
    private final long offset;
    private final int length;
    private final int valid;

    public IndexEntry(long offset, int length, int valid) {
        this.offset = offset;
        this.length = length;
        this.valid = valid;
    }

    public long getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public int getValid() {return valid;}

    public static boolean isValidBit(int bit) {
        return bit == 0 || bit == 1;
    }
}
