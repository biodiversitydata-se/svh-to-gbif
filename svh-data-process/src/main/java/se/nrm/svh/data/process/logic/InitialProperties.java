package se.nrm.svh.data.process.logic;

import java.io.Serializable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.wildfly.swarm.spi.runtime.annotations.ConfigurationValue;

/**
 *
 * @author idali
 */
@ApplicationScoped
@Slf4j
public class InitialProperties implements Serializable {
  
  private final static String CONFIG_INITIALLISING_ERROR = "Property not initialized";
  
  private String sourceExportBaseUrl;
  private String csvFilePath;
  private String dataMappingFile;

  public InitialProperties() {
  }

  @Inject
  public InitialProperties(@ConfigurationValue("swarm.source.export.baseUrl") 
          String sourceExportBaseUrl, 
          @ConfigurationValue("swarm.csv.file.path") String csvFilePath,
          @ConfigurationValue("swarm.mapping.file") String dataMappingFile) { 
    this.sourceExportBaseUrl = sourceExportBaseUrl;
    this.csvFilePath = csvFilePath;
    this.dataMappingFile = dataMappingFile;
    log.info("test injection : {} ", sourceExportBaseUrl);
  }
  
  public String getSourceExportBaseUrl() {
    if (sourceExportBaseUrl == null) {
      throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
    }
    return sourceExportBaseUrl;
  }  
    
  public String getCsvFilePath() {
    if (csvFilePath == null) {
      throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
    }
    return csvFilePath;
  }  
      
  public String getDataMappingFile() {
    if (dataMappingFile == null) {
      throw new RuntimeException(CONFIG_INITIALLISING_ERROR);
    }
    return dataMappingFile;
  }  
}
