package se.nrm.bas.svh.gbif.process.logic.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;  
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;   
import java.util.concurrent.atomic.AtomicInteger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder; 
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;
import se.nrm.bas.svh.gbif.datamodel.SimpleDwc; 

/**
 *
 * @author idali
 */
@Slf4j
public class JsonConverter implements Serializable {

  
  private final String valueKey = "value";
  private final String typeKey = "type";
  private final String catalogNumberKey = "CatalogNumber";
  private final String wgs84n = "WGS84N";
  private final String wgs84s = "WGS84S";
  private final String cSource = "CSource";
  private final String none = "None";
  private final int batchSize = 200;
    
  public JsonConverter() {
  }

  public List<JsonArray> convertCsvToJson(List<CSVRecord> records, JsonObject mappingJson, String institutionCode) {
    JsonObjectBuilder builder = Json.createObjectBuilder();
  
    AtomicInteger counter = new AtomicInteger(0);
    List<JsonArray> list = new ArrayList<>();
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

    records.stream()
            .filter(record -> record.isConsistent())
            .forEach(record -> {
              Map<String, String> map = record.toMap();

//              JsonHelper.getInstance().addIdAttribute(builder, 
//                      Util.getInstance().buildId(map.get(catalogNumberKey), institutionCode));  
              if (map.get(catalogNumberKey) != null && !map.get(catalogNumberKey).isEmpty()) {
                JsonHelper.getInstance().addIdAttribute(builder, map.get(catalogNumberKey));
                mappingJson.keySet()
                        .stream()
                        .forEach(key -> {
                          if(key.equals(wgs84n) || key.equals(wgs84s)) {
                            String cValue = map.get(cSource);
                            if(!map.get(cSource).equals(none)) {
                              addValueToJson(builder,  mappingJson.getJsonObject(key), map.get(key));
                            }
                          } else {
                            addValueToJson(builder,  mappingJson.getJsonObject(key), map.get(key));
                          } 
                        });
                arrayBuilder.add(builder);

                counter.getAndIncrement();
                if (counter.get() % batchSize == 0) {
                  list.add(arrayBuilder.build());
                }
              }

            });
    list.add(arrayBuilder.build());
    return list;
  }

  private void addValueToJson(JsonObjectBuilder builder, JsonObject json, String value) { 
    JsonHelper.getInstance().addAttribute(builder,
            json.getString(valueKey).trim(),
            json.getString(typeKey), value); 
  }
 

  public List<SimpleDwc> mapEntities(JsonArray array) {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    try {
      return objectMapper.readValue(array.toString(), new TypeReference<List<SimpleDwc>>() {
      });
    } catch (JsonProcessingException ex) {
      log.error(ex.getMessage());
    }
    return null;
  } 
}
