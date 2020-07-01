package se.nrm.svh.data.process.logic.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author idali
 */
public class Util {

  private StringBuilder dataSourceSb;
  private StringBuilder csvFileSb;
  private final String dataSourceInstitution = "export/CSV.php?InstitutionCode=";
  private final String and = "&";
  private final String page = "Page";
  private final String equalSign = "=";
  private final String underscore = "_";
  private final String csv = ".csv";
  
  private final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  
  
  private static Util instance = null;
   
  public static Util getInstance() {
    synchronized (Util.class) {
      if (instance == null) {
        instance = new Util();
      } 
    }
    return instance;
  }
  
  public Object parseValue(String value, String type) {
    switch (type) {
      case "String":
        return value;
      case "int":
        return parseInt(value);
      case "Short":
        return parseShort(value);
      case "BigDecimal":
        return parseBigDecimal((String) value);
      case "boolean":
        return Boolean.valueOf(value);
      case "double":
        return parseDouble(value);
      case "float":
        return parseFloat(value);
      case "long":
        return parseLong(value);
      default:
        return value;
    }
  }

  public int parseInt(String value) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      return 0;
    } 
  } 
  
  public short parseShort(String value) {
    try {
      return Short.valueOf(value);
    } catch (NumberFormatException e) {
      return 0;
    } 
  }
  
  public float parseFloat(String value) {
    try {
      return Float.parseFloat(value);
    } catch (NumberFormatException e) {
      return 0;
    } 
  }
    
  public BigDecimal parseBigDecimal(String value) {
    try {
      return new BigDecimal(value);
    } catch (NumberFormatException e) {
      return null;
    } 
  }
  
  public double parseDouble(String value)  { 
    try {
      return Double.parseDouble(value);  
    } catch (NumberFormatException e) {
      return 0;
    } 
  }
  
  public long parseLong(String value)  { 
    try {
      return Long.parseLong(value);  
    } catch (NumberFormatException e) {
      return 0;
    }

  }
  public Date stringToDate(String strDate) { 
    if(strDate == null) {
      return null;
    } 
    try {
      return dateFormat.parse(strDate);
    } catch (ParseException e) { 
      return null;
    }
  }
   
  public String buildCsvDownloadUrl(String dataSourcePath, String institution, int numOfPage) {
    dataSourceSb = new StringBuilder();
    dataSourceSb.append(dataSourcePath);
    dataSourceSb.append(dataSourceInstitution);
    dataSourceSb.append(institution);
    dataSourceSb.append(and);
    dataSourceSb.append(page);
    dataSourceSb.append(equalSign);
    dataSourceSb.append(numOfPage);
    
    return dataSourceSb.toString().trim();
  }
  
  public String buildCsvFilePath(String csvFile, String institution, int numOfPage) {
    csvFileSb = new StringBuilder();
    csvFileSb.append(csvFile);
    csvFileSb.append(institution);
    csvFileSb.append(underscore);
    csvFileSb.append(page);
    csvFileSb.append(numOfPage);
    csvFileSb.append(csv);
    return csvFileSb.toString().trim();
  }
  

}
