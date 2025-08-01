package service;

import dal.FileDataSource;
import dal.FileEntryFactory;
import model.IndexEntry;
import model.ParsedLine;
import model.Word;
import util.LoggerUtil;
import util.StringUtil;
import util.diutils.AfterCreation;
import util.diutils.Component;
import util.diutils.Injected;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Component
public class DictionaryService {
    private static final Logger logger = LoggerUtil.getLogger(DictionaryService.class);
    @Injected
    private FileDataSource fileDataSource;
    private HashMap<String, IndexEntry> indexPair;
    private int fragmentCounter;
    private static final int fragmentMaxLimit = 40;

//    public DictionaryService() {
    // empty constructor cho DI Manager
    // de the nay neu co constructor co tham so o duoi
    // ko thi ko can vi JVM tu tao default constructor
//    }

    @AfterCreation
//    can cai nay vi mot so cai not injected trong constructor ko bo dc
    public void init() {
        this.indexPair = new HashMap<>();
        loadValidEntries();
        fragmentCounter = 0;
        logger.info("DictionaryService initialized!");
    }

    //    DUNG CHO WITHOUT DI MANAGER!
//    public DictionaryService(FileDataSource fileDataSource){
//        this.fileDataSource = fileDataSource;
//        this.indexPair = new HashMap<>();
//        loadValidEntries();
//        fragmentCounter = 0;
//    }

    private void loadValidEntries(){
        List<String> rawLines = fileDataSource.loadRawLines();
        int lineNumber = 0;
        for (String line : rawLines){
            lineNumber++;
            ParsedLine parsedLine = FileEntryFactory.parseRawLines(line, lineNumber);
            if(parsedLine == null)
                continue;

            String word = StringUtil.normalizeString(parsedLine.getWord());
            IndexEntry entry = parsedLine.getIndexEntry();

            if(!StringUtil.validateWord(word) || !IndexEntry.isValidBit(entry.getValid())
                    || ((entry.getOffset()<0 || entry.getLength() <=0) && entry.getValid()==1)){
                logger.warning("Skipping malformed word/offset/length/valid, at line no." + lineNumber);
                continue;
            }
            if(entry.getValid()==1){
                indexPair.put(word, entry);
                // tu dong update nhung word bi modified
            } else {
                indexPair.remove(word);
            }
        }
    }

    public String addWord(Word word){
        String aWord = StringUtil.normalizeString(word.getWord());
        String meaning = StringUtil.normalizeString(word.getMeaning());
        if(!StringUtil.validateWord(aWord))
            return "Invalid word format!";
        if(!StringUtil.validateMeaning(meaning))
            return "Invalid meaning format!";
        if(indexPair.containsKey(aWord)){
            return "Word already existed.\nWord: " + aWord + "\nMeaning: "
                    + searchMeaning(aWord);
        }
        IndexEntry entry = fileDataSource.saveIndexLinesAndMeaning(aWord, meaning, 1);
        if(entry == null)
            return "Error saving word!";
        indexPair.put(aWord, entry);
        return "Word saved successfully!";
    }

    public List<String> displayAllForDebugging(){
        List<String> lines = new ArrayList<>();
        int i=1;
        for (Map.Entry<String, IndexEntry> entry : indexPair.entrySet()) {
            String word = entry.getKey();
            IndexEntry index = entry.getValue();
            String meaning = fileDataSource.readMeaning(index);
            lines.add(i++ + ". " + word + " = " + (meaning != null ? meaning : "[CORRUPTED]"));
        }
        return lines;
    }

    public String searchMeaning(String word){
        word = StringUtil.normalizeString(word);
        if(!indexPair.containsKey(word))
            return "Word not found!";
        String meaning = fileDataSource.readMeaning(indexPair.get(word));
        if(meaning == null)
            return "'" + word + "' has corrupted meaning!";
        return meaning;
    }

    public String modifyMeaning(String word, String newMeaning){
        word = StringUtil.normalizeString(word);
        if(!indexPair.containsKey(word))
            return "Error: Word not found!";
        newMeaning = StringUtil.normalizeString(newMeaning);
        if(!StringUtil.validateMeaning(newMeaning))
            return "Invalid meaning format!";
        IndexEntry entry = fileDataSource.saveIndexLinesAndMeaning(word, newMeaning, 1);
        if(entry == null)
            return "Failed to update meaning!";
        indexPair.put(word, entry);
        //ko can remove, append index.txt va indexPair.put de replace la dc
        fragmentCounter++;
        maybeCleanIndexFile();
        return "Word modified successfully!";
    }

    public String removeWord(String word){
        word = StringUtil.normalizeString(word);
        if(!indexPair.containsKey(word))
            return "Error: Word not found!";
        fileDataSource.saveIndexLinesAndMeaning(word, null, 0);
        indexPair.remove(word);
        fragmentCounter++;
        maybeCleanIndexFile();
        return "Word removed successfully!";
    }

    private void maybeCleanIndexFile(){
        if(fragmentCounter>fragmentMaxLimit){
            String s = cleanIndexFile();
            logger.info(s);
            fragmentCounter = 0;
        }
    }

    public String cleanIndexFile(){
        List<String> validLines = new ArrayList<>();
        for(Map.Entry<String, IndexEntry> entry : indexPair.entrySet()){
            String word = entry.getKey();
            long offset =  entry.getValue().getOffset();
            int length = entry.getValue().getLength();
            int valid = entry.getValue().getValid();
            if(entry.getValue().getValid()==1){
                String line = FileEntryFactory.formatLine(word, offset, length, valid);
                validLines.add(line);
            }
        }
        fileDataSource.writeIndexFile(validLines);
        return "Index file cleaned successfully!";
    }

    public String cleanDataFile(){
        HashMap<String, IndexEntry> newIndexPair = new HashMap<>();
        for(Map.Entry<String, IndexEntry> entry : indexPair.entrySet()) {
            if (entry.getValue().getValid()!=1)
                continue;
            String word = entry.getKey();
            String meaning = fileDataSource.readMeaning(entry.getValue());
            if (meaning == null) {
                logger.warning("'" + word + "' has corrupted meaning!");
                continue;
            }
            IndexEntry newIndexEntry = fileDataSource.writeToTempDataFile(meaning);
            if(newIndexEntry == null){
                logger.warning("Failed to write meaning for word: " + word);
                continue;
            }
            newIndexPair.put(word, newIndexEntry);
        }
        boolean success = fileDataSource.replaceDataFile();
        if (success) {
            indexPair = newIndexPair;
            //phai de sau khi rename file tmp.txt thanh data.txt, ko se bi lech offset
            cleanIndexFile();
            return "Data file cleaned successfully!";
        } else {
            return "Failed to clean data file.";
        }
    }
}
