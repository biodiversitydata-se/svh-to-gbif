package se.nrm.bas.svh.gbif.process.logic;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.json.JsonArray;
import javax.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.wildfly.swarm.Swarm;
import se.nrm.bas.svh.gbif.datamodel.Logs;
import se.nrm.bas.svh.gbif.datamodel.SimpleDwc;
import se.nrm.bas.svh.gbif.process.logic.config.InitialProperties;
import se.nrm.bas.svh.gbif.process.logic.config.MappingFile;
import se.nrm.bas.svh.gbif.process.logic.csv.CsvFileProcessor;
import se.nrm.bas.svh.gbif.process.logic.jpa.GbifDao;
import se.nrm.bas.svh.gbif.process.logic.json.JsonConverter;
import se.nrm.bas.svh.gbif.process.logic.util.Util;

/**
 *
 * @author idali
 */
@Slf4j
public class DataProcessor implements Serializable {

  @Inject
  private InitialProperties propeties;
  @Inject
  private MappingFile mappingFile;

  @Inject
  private CsvFileProcessor fileProcessor;
  @Inject
  private JsonConverter converter;
  @Inject
  private GbifDao dao;

  private String dataSourcePath;
  private String csvFilePath;
 
  private Map<String, JsonObject> map;
  private JsonArray mappingArray;

  private final String underscore = "_";
  private final String csvFileExtension = ".csv";

  public DataProcessor() {

  }

  @PostConstruct
  public void init() {
    log.info("init");

    dataSourcePath = propeties.getSourceExportBaseUrl();
    csvFilePath = propeties.getCsvFilePath();
    mappingArray = mappingFile.read(propeties.getDataMappingFile());
    map = Util.getInstance().buildJsonMap(mappingArray);
  }

  public void run() {
    log.info("run");

    fileProcessor.processCsvFile(mappingArray, dataSourcePath, csvFilePath);
    log.info("CSV download done ... ");

    File directory = new File(csvFilePath); 
    File[] files = directory.listFiles();

    Map<String, Integer> countMap = new HashMap();
    Arrays.asList(files)
            .stream()
            .filter(f -> f.getName().contains(csvFileExtension)) 
            .forEach(f -> {
              List<CSVRecord> records = fileProcessor.readCsv(f);
              if (records != null) {
                String institution = StringUtils.substringBefore(f.getName(), underscore);
                JsonObject json = map.get(institution);

                List<JsonArray> array = converter.convertCsvToJson(records, json, institution);
                log.info("array size : {} -- {}", f.getName(), array.size());

                array.stream()
                        .filter(a -> a.size() > 0)
                        .forEach(subArray -> {
                          List<SimpleDwc> beans = converter.mapEntities(subArray);
                          log.info("beans : {} -- {}", beans.size(), institution);
                          dao.merge(beans, institution); 
                          int total = beans.size();
                          if (countMap.containsKey(institution)) {
                            total = countMap.get(institution) + total;
                          }
                          countMap.put(institution, total);
                        });
              }
            }); 
    log.info("countMap : {}", countMap);

    countMap.keySet().stream()
            .forEach(key -> {
              Logs logs = new Logs();
              logs.setTotalRecords(countMap.get(key));
              logs.setTimestamp(new Date());
              dao.createLogs(logs, key);
            });
    fileProcessor.removeCsvFiles(files);
    stopServer();
  }
   
  private void stopServer() {
    try {
      TimeUnit.SECONDS.sleep(20);
      Thread.currentThread().interrupt();
      Swarm.stopMain(); 
    } catch (InterruptedException ex) {
      log.error(ex.getMessage());
    } catch (Exception ex) {
      log.error(ex.getMessage());
    }
  } 
}
