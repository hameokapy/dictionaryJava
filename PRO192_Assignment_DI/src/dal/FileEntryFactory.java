package dal;

import model.IndexEntry;
import model.ParsedLine;
import util.LoggerUtil;
import util.diutils.Component;

import java.util.logging.*;

@Component
public class FileEntryFactory {
    private static final Logger logger = LoggerUtil.getLogger(FileEntryFactory.class);

    //DI Manager dung default constructor, o day ko co constructor co tham so
    // thi ko can vi JVM tu tao cho

    public static String formatLine(String word, long offset, int length, int valid){
        return word + "," + offset + "," + length + "," + valid;
    }

    public static ParsedLine parseRawLines(String line, int lineNumber){
        if(line.trim().isEmpty()){
            logger.warning("Skipping empty line, at line no:" + lineNumber);
            return null;
        }
        String[] parts = line.split(",", 4);
        if(parts.length != 4){
            logger.warning("Skipping malformed line, at line no:" + lineNumber);
            return null;
        }
        try {
            String rawWord = parts[0].trim();
            long offset = Long.parseLong(parts[1].trim());
            int length = Integer.parseInt(parts[2].trim());
            int valid = Integer.parseInt(parts[3].trim());
            IndexEntry entry = new IndexEntry(offset, length, valid);
            return new ParsedLine(rawWord, entry);
        } catch(NumberFormatException e){
            logger.warning("Invalid number format for offset/length/valid, at line no." + lineNumber);
            return null;
        }
    }
}
