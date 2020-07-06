package se.nrm.bas.svh.gbif.process.logic.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;  
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;  
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder; 
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import se.nrm.bas.svh.gbif.datamodel.SimpleDwc;
import se.nrm.bas.svh.gbif.process.logic.util.Util;

/**
 *
 * @author idali
 */
@Slf4j
public class JsonConverter implements Serializable {

  public static Object getInstance() {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  private JsonArrayBuilder arrayBuilder;
  private JsonObjectBuilder builder;

  private final String valueKey = "value";
  private final String typeKey = "type";
  private final String catalogNumberKey = "CatalogNumber";
  private final int batchSize = 5000;
  
  private Map<String, String> map;
  
  private int count;

  public JsonConverter() {
  }

  public List<JsonArray> convertCsvToJson(List<CSVRecord> records,
          JsonObject mappingJson, String institutionCode) {
    builder = Json.createObjectBuilder();
    count = 0;
    List<JsonArray> list = new ArrayList<>();
    arrayBuilder = Json.createArrayBuilder();
    records.stream()
            .filter(record -> record.isConsistent())
            .forEach(record -> { 
              count++; 
              map = record.toMap(); 
              
              JsonHelper.getInstance().addIdAttribute(builder, 
                      Util.getInstance().buildId(map.get(catalogNumberKey), institutionCode)); 
              mappingJson.keySet()
                      .stream()
                      .forEach(key -> {
                        JsonHelper.getInstance().addAttributes(builder,
                                mappingJson.getJsonObject(key).getString(valueKey).trim(),
                                mappingJson.getJsonObject(key).getString(typeKey), map.get(key));
                      });
              arrayBuilder.add(builder);
              
              if(count % batchSize == 0) { 
                JsonArray subArray = arrayBuilder.build(); 
                list.add(subArray);
              }
            });
    return list;
  }

  public List<SimpleDwc> mapEntities(JsonArray array) {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    try { 
      return objectMapper.readValue(array.toString(), new TypeReference<List<SimpleDwc>>() {});
    } catch (JsonProcessingException ex) {
      log.error(ex.getMessage());
    }
    return null;
  }

}
