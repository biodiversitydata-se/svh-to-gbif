package se.nrm.svh.data.process;
 
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URISyntaxException; 
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser; 
import org.apache.commons.csv.QuoteMode; 
import se.nrm.svh.data.process.logic.util.Util;

/**
 *
 * @author idali
 */
public class MyMain {
  
  static int count;

  public static void main(String[] args) throws FileNotFoundException, IOException, URISyntaxException {
//    String strDouble = "23.18";
//    double d = Double.parseDouble(strDouble);  
//    System.out.println("d..." + d);
    
  
 

    CSVFormat csvFileFormat = CSVFormat.TDF
            .withFirstRecordAsHeader() 
            .withIgnoreHeaderCase()
            .withTrim()
            .withQuoteMode(QuoteMode.ALL)
            .withQuote(null);
 
    Reader in = new FileReader("/Users/idali/svh_exports/GB_Page3.csv");
 
  
    CSVParser csvParser = csvFileFormat.parse(in);
    System.out.println(csvParser.getHeaderMap());
     
    count = 0;
    csvParser.getRecords().stream()
            .forEach(r -> {
              if(!r.isConsistent()) {
                count++;
              }
              
            });
    System.out.println("cunt..." + count);
//    for (CSVRecord csvRecord : csvParser) {
////      System.out.println("records : " + csvRecord.toMap()); 
//      Iterator itr = csvRecord.iterator();
//      while (itr.hasNext()) 
//            System.out.println("result : " + itr.next() + " "); 
//  
//      
//      
////      System.out.println("Institution : " + csvRecord.iterator());
//    }

//    System.out.println("hi");
//    System.out.println("env..." + System.getenv("MAVEN_CMD_LINE_ARGS"));
//    
//    System.out.println("args,.." + args.length);
//    CSVFileHandler handler = new CSVFileHandler();
//    handler.fetchCsvFile();
//    String path = "http://herbarium.emg.umu.se/export/CSV.php?InstitutionCode=UME&Page=1";
//
//    System.out.println("hi");
////    Thorntail.run();
//
//    Client client = ClientBuilder.newClient();
//    WebTarget target = client.target(path);
////    Invocation.Builder invocationBuilder = target.request(MediaType.TEXT_PLAIN_TYPE);
//    
//    
//    Response response = target.request().get();
//    String result = response.readEntity(String.class);
//    
//    System.out.println("env..." + System.getenv("swarm.mail.host"));
//    System.out.println("response: " + result);
  }

}
