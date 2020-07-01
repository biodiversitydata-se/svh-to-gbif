package se.nrm.svh.data.process.logic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import javax.json.Json;
import javax.json.JsonArray;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author idali
 */
@Slf4j
public class MappingFile implements Serializable {
  
  public MappingFile() {
    
  }
  
  public JsonArray read(String mappingFilePath) {
    InputStream fis = null;
    try {
      fis = new FileInputStream(mappingFilePath); 
      return Json.createReader(fis).readArray();   
    } catch (FileNotFoundException ex) {
      log.error(ex.getMessage()); 
    } finally {
      try {
        if(fis != null) {
           fis.close();
        } 
      } catch (IOException ex) {
        log.error(ex.getMessage());
      }
    }
    return null;
  }
}
