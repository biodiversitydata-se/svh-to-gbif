package se.nrm.bas.svh.gbif.process.logic.json;

import java.math.BigDecimal;
import javax.json.JsonObjectBuilder;
import org.apache.commons.lang3.StringUtils;
import se.nrm.bas.svh.gbif.process.logic.util.Util;

/**
 *
 * @author idali
 */
public class JsonHelper {
  private static JsonHelper instance = null;
  
  private String idKey = "id";

  public static JsonHelper getInstance() {
    synchronized (JsonHelper.class) {
      if (instance == null) {
        instance = new JsonHelper();
      }
    }
    return instance;
  }
  
  public void addIdAttribute(JsonObjectBuilder attBuilder, String value) {
    attBuilder.add(idKey, value);
  }

  public void addAttributes(JsonObjectBuilder attBuilder, String key, String type, String value) { 
    
    if (value != null && !StringUtils.isEmpty(value)) {
      try {
        switch (type) {
          case "String":
            attBuilder.add(key, value.trim());
            break;
          case "date":
            attBuilder.add(key, Util.getInstance().validateDate(value.trim()));
//            attBuilder.add(key, value.trim());
            break;
          case "int":
            attBuilder.add(key, Integer.parseInt(value));
            break;
          case "Short":
            attBuilder.add(key, Short.valueOf(value));
            break;
          case "BigDecimal":
            attBuilder.add(key, new BigDecimal(value)); 
            break;
          case "boolean":
            attBuilder.add(key, Boolean.valueOf(value));
            break;
          case "double":
            attBuilder.add(key, Double.parseDouble(value));
            break;
          case "float":
            attBuilder.add(key, Float.parseFloat(value));
            break;
          case "long":
            attBuilder.add(key, Long.parseLong(value));
            break;
          default:
            attBuilder.add(key, value.trim());
            break;
        }
      } catch (NumberFormatException e) {

      } 
    }
  }
}
