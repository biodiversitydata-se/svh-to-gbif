package se.nrm.svh.data.process.logic.csv;

import java.io.File; 
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException; 
import java.io.Reader;
import java.io.Serializable;   
import java.util.List; 
import java.util.stream.StreamSupport; 
import lombok.extern.slf4j.Slf4j; 
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
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
    
  private Iterable<CSVRecord> records;
  private CSVFormat csvFileFormat;
  private CSVParser csvParser;

  

  public CsvFileProcessor() {

  }
  
  public List<CSVRecord> readCsv(File file) { 
    csvFileFormat = CSVFormat.newFormat(tabSepartor)
            .withFirstRecordAsHeader() 
            .withQuoteMode(QuoteMode.ALL);
     
    Reader reader = null;
    try {
      reader = new FileReader(file);  
      csvParser = csvFileFormat.parse(reader);  
      return csvParser.getRecords(); 
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
 
  public boolean isLastPage(String filePath) {
  
    csvFileFormat = CSVFormat.DEFAULT.withQuote(null);      
    Reader in = null;
    try {
      in = new FileReader(filePath); 
      records = csvFileFormat.parse(in); 
      return StreamSupport.stream(records.spliterator(), false).count() < maxRows;  
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
