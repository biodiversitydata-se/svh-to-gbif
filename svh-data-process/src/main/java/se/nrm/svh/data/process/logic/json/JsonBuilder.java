package se.nrm.svh.data.process.logic.json;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;

/**
 *
 * @author idali
 */
@Slf4j
public class JsonBuilder implements Serializable {

  private JsonArrayBuilder arrayBuilder;
  private JsonObjectBuilder builder;

  private final String valueKey = "value";
  private final String typeKey = "type";
  private Map<String, String> map;

  public JsonBuilder() {
  }

  public JsonArray convertCsvToJson(List<CSVRecord> records, JsonObject mappingJson) {
    builder = Json.createObjectBuilder();
    arrayBuilder = Json.createArrayBuilder();
    records.stream()
            .filter(record -> record.isConsistent())
            .forEach(record -> { 
              map = record.toMap();
              mappingJson.keySet()
                      .stream()
                      .forEach(key -> {
                        JsonHelper.getInstance().addAttributes(builder,
                                mappingJson.getJsonObject(key).getString(valueKey).trim(),
                                mappingJson.getJsonObject(key).getString(typeKey), map.get(key));
                      });
              arrayBuilder.add(builder); 
            });
    return arrayBuilder.build();
  }

}
