package se.nrm.bas.svh.gbif.process.logic.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable; 
import java.util.Arrays;
import java.util.List;  
import java.util.stream.StreamSupport;
import javax.inject.Inject;
import javax.json.JsonArray;  
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat; 
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;

/**
 *
 * @author idali
 */
@Slf4j
public class CsvFileProcessor implements Serializable {

  private final int maxRows = 100001;
  private final char tabSepartor = '\t';
  private final String institutionKey = "institution";
  private final String downloadKey = "download";
 
  private CSVFormat csvFileFormat; 

  @Inject
  private CsvFileDownloader downloader;

  public CsvFileProcessor() {

  }

  public void processCsvFile(JsonArray mappingArray, String dataSourcePath, String csvFilePath) {

    mappingArray.stream().parallel()
            .filter(a -> a.asJsonObject().getBoolean(downloadKey))
            .forEach(a -> {
              String institutionCode = a.asJsonObject().getString(institutionKey);   
              boolean isDone = false;
              int pageCount = 1;

              while (!isDone) { 
                isDone = isLastPage(downloader.download(dataSourcePath, institutionCode, csvFilePath, pageCount));
                log.info("isDone : {}", isDone);
                pageCount++;
              }
            });
  }
  
  public List<CSVRecord> readCsv(File file) { 
    csvFileFormat = CSVFormat.newFormat(tabSepartor)
            .withFirstRecordAsHeader() 
            .withQuoteMode(QuoteMode.ALL);
     
    Reader reader = null;
    try {
      reader = new FileReader(file);   
      return csvFileFormat.parse(reader).getRecords(); 
    } catch (FileNotFoundException ex) {
      log.error(ex.getMessage());
    } catch (IOException ex) {
      log.error(ex.getMessage()); 
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException ex) {
        log.error(ex.getMessage());
      }
    }  
    return null;
  }
 
  public void removeCsvFiles(File[] files) {
    Arrays.asList(files).stream()
            .forEach(f -> { 
              f.deleteOnExit();
            });
  }
  
  private boolean isLastPage(String filePath) {
  
    csvFileFormat = CSVFormat.DEFAULT.withQuote(null);      
    Reader in = null;
    try {
      in = new FileReader(filePath);  
      return StreamSupport.stream(csvFileFormat.parse(in).spliterator(), false).count() < maxRows;  
    } catch (FileNotFoundException ex) {
      log.error(ex.getMessage());
    } catch (IOException ex) {
      log.error(ex.getMessage()); 
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException ex) {
        log.error(ex.getMessage());
      }
    }
    return true;
  }
}
