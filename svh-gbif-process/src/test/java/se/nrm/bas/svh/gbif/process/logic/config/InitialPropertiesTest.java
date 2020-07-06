package se.nrm.bas.svh.gbif.process.logic.config;

import org.junit.After; 
import org.junit.Before; 
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author idali
 */
public class InitialPropertiesTest {
  
  private InitialProperties instance;
  
  private String sourceExportBaseUrl;
  private String csvFilePath;
  private String dataMappingFile;
  
  public InitialPropertiesTest() {
  }
 
  @Before
  public void setUp() {
    sourceExportBaseUrl = "http://herbarium.emg.umu.se/";
    csvFilePath = "/temp/csv";
    dataMappingFile = "/temp/mapping";
  }
  
  @After
  public void tearDown() {
    instance = null;
  }
  
  @Test
  public void testDefaultConstructor() {
    instance = new InitialProperties();
    assertNotNull(instance); 
  }
  
  @Test(expected = RuntimeException.class)
  public void testException1() {
    instance = new InitialProperties();
    
    instance.getSourceExportBaseUrl();
  }
  
  @Test(expected = RuntimeException.class)
  public void testException2() {
    instance = new InitialProperties();
    
    instance.getCsvFilePath();
  }
    
  @Test(expected = RuntimeException.class)
  public void testException3() {
    instance = new InitialProperties();
    
    instance.getDataMappingFile();
  }

  /**
   * Test of getSourceExportBaseUrl method, of class InitialProperties.
   */
  @Test
  public void testGetSourceExportBaseUrl() {
    System.out.println("getSourceExportBaseUrl");
    instance = new InitialProperties(sourceExportBaseUrl, csvFilePath, dataMappingFile);
  
    String result = instance.getSourceExportBaseUrl();
    assertEquals(sourceExportBaseUrl, result); 
  }

  /**
   * Test of getCsvFilePath method, of class InitialProperties.
   */
  @Test
  public void testGetCsvFilePath() {
    System.out.println("getCsvFilePath");
    
    instance = new InitialProperties(sourceExportBaseUrl, csvFilePath, dataMappingFile);
 
    String result = instance.getCsvFilePath();
    assertEquals(csvFilePath, result); 
  }

  /**
   * Test of getDataMappingFile method, of class InitialProperties.
   */
  @Test
  public void testGetDataMappingFile() {
    System.out.println("getDataMappingFile");
    
    instance = new InitialProperties(sourceExportBaseUrl, csvFilePath, dataMappingFile);
 
    String result = instance.getDataMappingFile();
    assertEquals(dataMappingFile, result); 
  }
  
}
