package se.nrm.svh.data.process.logic;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject; 
import javax.json.JsonArray; 
import javax.json.JsonObject; 
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import se.nrm.svh.data.process.logic.csv.CsvFileDownloader;
import se.nrm.svh.data.process.logic.csv.CsvFileProcessor;
import se.nrm.svh.data.process.logic.json.JsonBuilder;

/**
 *
 * @author idali
 */
@Slf4j
public class DataProcessLogic implements Serializable {

  @Inject
  private InitialProperties propeties;

  @Inject
  private CsvFileDownloader downloader;
  
  @Inject
  private MappingFile mappingFile;
  
  @Inject 
  private CsvFileProcessor processor;
  
  @Inject
  private JsonBuilder jsonBuilder;

  private String dataSourcePath;
  private String mappingFilePath;
  private String csvFilePath;
  
  private String savedFilePath;
  private Map<String, JsonObject> map;
  private List<CSVRecord> records;
  
  private final String institutionKey = "institution";
  private final String mappingKey = "mapping";
  private final String underscore = "_";
  private final String csvFileExtension = ".csv";
   
  public DataProcessLogic() {

  }

  /**
   * Initialize data after class constructed
   */
  @PostConstruct
  public void init() {
    log.info("init");
    dataSourcePath = propeties.getSourceExportBaseUrl();
    mappingFilePath = propeties.getDataMappingFile();
    csvFilePath = propeties.getCsvFilePath();
    log.info("sourceExportBaseUrl : {}", dataSourcePath);
  }

  public void run() {
    map = new HashMap<>();
    JsonArray mappingArray = mappingFile.read(mappingFilePath);
    mappingArray.stream().forEach(a -> {
      JsonObject json = a.asJsonObject();
      
      String institutionCode = json.getString(institutionKey); 
      map.put(institutionCode, json.getJsonObject(mappingKey));
      
      boolean isDone = false;
      int pageCount = 1;
      while(!isDone) {
        savedFilePath = downloader.download(dataSourcePath, institutionCode, csvFilePath, pageCount);
        isDone = processor.isLastPage(savedFilePath);
        log.info("isDone : {}", isDone);
        pageCount++; 
      }  
    }); 
    
    File directory = new File(csvFilePath); 
    File[] files = directory.listFiles();
    log.info("files size : {}", files.length);
    
    Arrays.asList(files).stream()
            .filter(f -> f.getName().contains(csvFileExtension))
            .forEach(f -> { 
         
              records = processor.readCsv(f);
              if(records != null) {
                String institutionCode = StringUtils.substringBefore(f.getName(), underscore); 
                JsonObject json = map.get(institutionCode);
                
                JsonArray array = jsonBuilder.convertCsvToJson(records, json);
                log.info("array size : {} -- {}", f.getName(), array.size()); 
              }  
            });
  }
}
