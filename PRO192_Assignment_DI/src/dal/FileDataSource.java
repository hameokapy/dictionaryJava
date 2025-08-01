package dal;

import model.IndexEntry;
import util.LoggerUtil;
import util.diutils.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static dal.FileConstants.*;

@Component
public class FileDataSource {
    private static final Logger logger = LoggerUtil.getLogger(FileDataSource.class);

    //DI Manager dung default constructor, o day ko co constructor co tham so
    // thi ko can vi JVM tu tao cho

    //chi vao DISK lay khi can (not in RAM like index data)
    public String readMeaning(IndexEntry entry){
        try(RandomAccessFile meaningRead = new RandomAccessFile(DATA_FILE, "r")){
            meaningRead.seek(entry.getOffset());
            byte[] meaningBytes = new byte[entry.getLength()];
            int byteRead = meaningRead.read(meaningBytes);
            if(byteRead != entry.getLength())
                return null;
            return new String(meaningBytes, "UTF-8");
        } catch(IOException e){
            return null;
        }
    }

    public IndexEntry saveIndexLinesAndMeaning(String word, String meaning, int valid){
        File indexFile = new File(INDEX_FILE);
        File parentDir = indexFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(INDEX_FILE, true))) {
            long offset = writeMeaning(meaning);
            if(offset == -1 && valid==1){
                    logger.warning("Error writing meaning to data file");
                    return null;
            }
            int length = offset==-1 ? 0 : meaning.getBytes("UTF-8").length;

            String line = FileEntryFactory.formatLine(word, offset, length, valid);
            writer.write(line);
            writer.newLine();
            return new IndexEntry(offset, length, valid);
        } catch (IOException e){
            logger.warning("Error saving to file: " + e.getMessage());
            return null;
        }
    }
    private long writeMeaning(String meaning){
        try(RandomAccessFile meaningRecord = new RandomAccessFile(DATA_FILE, "rw")){
            long offset = meaningRecord.length();
            meaningRecord.seek(offset);
            byte[] bytes = meaning.getBytes("UTF-8");
            meaningRecord.write(bytes);
            return offset;
        } catch(IOException | NullPointerException e){
            return -1;
        }
    }

    public List<String> loadRawLines(){
        List<String> rawLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(INDEX_FILE))) {
            String line;
            while((line=reader.readLine()) != null){
                rawLines.add(line);
            }
        } catch (FileNotFoundException e) {
            logger.warning("Error: Index file not found, now starting with empty dictionary.");
        } catch (IOException e){
            logger.warning("Error loading from file: " + e.getMessage());
        }
        return rawLines;
    }

    public void writeIndexFile(List<String> validLines){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(INDEX_FILE))){
            for(String line : validLines){
                writer.write(line);
                //ko lo modified word xuat hien nhieu lan trong index file vi cai nay dua vao indexPair list trong RAM
                writer.newLine();
            }
        } catch(FileNotFoundException e){
            logger.warning("Error: Index file not found.");
        } catch(IOException e){
            logger.warning("Error cleaning index file: " + e.getMessage());
        }
    }

    public IndexEntry writeToTempDataFile(String meaning){
        File tempDataFile = new File(TEMP_DATA_FILE);
        File parentDir = tempDataFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        try(RandomAccessFile dataWriter = new RandomAccessFile(TEMP_DATA_FILE, "rw")){
            long offset = dataWriter.length();
            byte[] bytes = meaning.getBytes("UTF-8");
            dataWriter.seek(offset);
            dataWriter.write(bytes);
            return new IndexEntry(offset, bytes.length, 1);
        } catch(IOException e){
            logger.warning("Error cleaning data file: " + e.getMessage());
            return null;
        }
    }

    public boolean replaceDataFile(){
        File oldDataFile = new File(DATA_FILE);
        File tempDataFile = new File(TEMP_DATA_FILE);
        if (!oldDataFile.delete()) {
            logger.warning("Could not delete old data file.");
            return false;
        }
        if (!tempDataFile.renameTo(oldDataFile)) {
        //ko de o trong try-catch vi file tempDataFile not yet closed
            logger.warning("Could not rename temp data file.");
            return false;
        }
        return true;
    }
}
