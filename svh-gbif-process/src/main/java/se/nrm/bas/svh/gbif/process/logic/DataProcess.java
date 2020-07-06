package se.nrm.bas.svh.gbif.process.logic;

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
import se.nrm.bas.svh.gbif.datamodel.SimpleDwc;
import se.nrm.bas.svh.gbif.process.logic.config.InitialProperties;
import se.nrm.bas.svh.gbif.process.logic.config.MappingFile;
import se.nrm.bas.svh.gbif.process.logic.csv.CsvFileDownloader;
import se.nrm.bas.svh.gbif.process.logic.csv.CsvFileProcessor;
import se.nrm.bas.svh.gbif.process.logic.jpa.GbifDao;
import se.nrm.bas.svh.gbif.process.logic.json.JsonConverter;

/**
 *
 * @author idali
 */
@Slf4j
public class DataProcess implements Serializable {
    
  @Inject
  private InitialProperties propeties; 
  @Inject
  private MappingFile mappingFile; 
  @Inject
  private CsvFileDownloader downloader; 
  @Inject 
  private CsvFileProcessor fileProcessor;
  @Inject
  private JsonConverter converter;
  @Inject
  private GbifDao dao;

  private String dataSourcePath; 
  private String csvFilePath;
  private String savedFilePath;
  private boolean isDone;
  private int pageCount;
  
  private Map<String, JsonObject> map;
  private JsonArray mappingArray;
  private String institutionCode;
  private JsonObject mappingJson;
  private List<CSVRecord> records;
  
  private final String institutionKey = "institution";
  private final String mappingKey = "mapping";
  private final String underscore = "_";
  private final String csvFileExtension = ".csv";
  
  public DataProcess() {
    
  }
  
  @PostConstruct
  public void init() {
    log.info("init");
    
    dataSourcePath = propeties.getSourceExportBaseUrl(); 
    csvFilePath = propeties.getCsvFilePath();  
    mappingArray = mappingFile.read(propeties.getDataMappingFile());
  }

  public void run() {
    log.info("run");

    map = new HashMap<>();
    mappingArray.stream()  
            .forEach(a -> {
              mappingJson = a.asJsonObject();
              institutionCode = mappingJson.getString(institutionKey);
              map.put(institutionCode, mappingJson.getJsonObject(mappingKey));

              isDone = false;
              pageCount = 1;
              while (!isDone) {
                savedFilePath = downloader.download(dataSourcePath, institutionCode, csvFilePath, pageCount);
                isDone = fileProcessor.isLastPage(savedFilePath);
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
              records = fileProcessor.readCsv(f);
              if(records != null) { 
                String institution = StringUtils.substringBefore(f.getName(), underscore);
                JsonObject json = map.get(institution);
                
                List<JsonArray> array = converter.convertCsvToJson(records, json, institution);
                log.info("array size : {} -- {}", f.getName(), array.size()); 
                
                array.stream()
                        .forEach(subArray -> {
                          List<SimpleDwc> beans = converter.mapEntities(subArray); 
                          log.info("beans : {}", beans.size());
                          dao.merge(beans, institution);
                        }); 
              }  
            });
  }
  
}
