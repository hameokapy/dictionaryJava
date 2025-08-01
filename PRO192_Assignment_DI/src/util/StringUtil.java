package util;

import java.util.logging.Logger;

public class StringUtil {
    private static final String WORD_REGEX = "^[a-zA-Z]+(([\\s]?[\\-']?|[\\-']?[\\s]?)[a-zA-Z]+)*$";
    private static final String MEANING_REGEX = "^[\\p{L}]+(([\\s]?[\\-']?|[\\-']?[\\s]?)[\\p{L}]+)*$";
    private static final Logger logger = LoggerUtil.getLogger(StringUtil.class);

    public static String normalizeString(String str){
        return str.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    public static boolean validateWord(String str){
        if(str == null || str.isEmpty()){
            logger.warning("Error: Word cannot be empty.");
            return false;
        }
        if(!str.matches(WORD_REGEX)){
            logger.warning("Error: Word can contain only letters (English), "
                    + "spaces, hyphens, or apostrophes (Ex: rock 'n' roll).");
            return false;
        }
        return true;
    }

    public static boolean validateMeaning(String str){
        if(str == null || str.isEmpty()){
            logger.warning("Error: Meaning cannot be empty.");
            return false;
        }
        if(!str.matches(MEANING_REGEX)){
            logger.warning("Error: Meaning can contain only letters (incl. Vietnamese), " +
                    "spaces, hyphens, or apostrophes (Ex: rốc 'n' roll).");
            return false;
        }
        return true;
    }
}
