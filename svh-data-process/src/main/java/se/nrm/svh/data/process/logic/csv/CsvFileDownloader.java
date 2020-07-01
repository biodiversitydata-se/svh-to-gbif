package se.nrm.svh.data.process.logic.csv;
 
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption; 
import lombok.extern.slf4j.Slf4j; 
import se.nrm.svh.data.process.logic.util.Util;

/**
 *
 * @author idali
 */
@Slf4j
public class CsvFileDownloader implements Serializable { 
  
  public CsvFileDownloader() {

  } 

  public String download(String dataSourcePath, String institution, String csvFile, int numOfPage) {
    InputStream in = null;
    String path = Util.getInstance().buildCsvDownloadUrl(dataSourcePath, institution, numOfPage);
    String csvFilePath = Util.getInstance().buildCsvFilePath(csvFile, institution, numOfPage);
    log.info("path : {} --- {}", path, csvFilePath);
 
    try {
      in = new URL(path).openStream();
      Files.copy(in, Paths.get(csvFilePath), StandardCopyOption.REPLACE_EXISTING); 
    } catch (IOException ex) {
      log.error(ex.getMessage());
    } finally {
      try {
        if(in != null) {
           in.close();
        } 
      } catch (IOException ex) {
        log.error(ex.getMessage());
      }
    }
    return csvFilePath;
  }
  
}
