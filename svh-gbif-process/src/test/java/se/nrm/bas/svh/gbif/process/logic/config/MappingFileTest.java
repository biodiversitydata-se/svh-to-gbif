package se.nrm.bas.svh.gbif.process.logic.config;
 
import javax.json.JsonArray; 
import org.junit.After; 
import org.junit.Before; 
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author idali
 */
public class MappingFileTest {
  
  private MappingFile instance;
  private String mappingFilePath; 
  
  public MappingFileTest() {
  }
   
  @Before
  public void setUp() {
    mappingFilePath = "src/test/resources/data_mapping.json"; 
    instance = new MappingFile();
  }
  
  @After
  public void tearDown() {
    instance = null;
  }

  /**
   * Test of read method, of class MappingFile.
   */
  @Test
  public void testRead() {
    System.out.println("read"); 
    
    instance = new MappingFile(); 
    JsonArray result = instance.read(mappingFilePath);
    assertEquals(3, result.size()); 
  }
  
}
