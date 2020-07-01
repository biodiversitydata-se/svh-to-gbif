package se.nrm.svh.data.process.logic;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j; 

/**
 *
 * @author idali
 */
@Slf4j
@ApplicationScoped
public class StartupBean {
     
  @Inject
  private DataProcessLogic logic;
  
  void init(@Observes @Initialized(ApplicationScoped.class) Object event) {
    log.info("My Application - INITIALIZATION");
      
    readInCsvFile();
  }
  
  private void readInCsvFile() {
    log.info("readInCsvFile");  
    logic.run(); 
    
    shutdown();
  }

  @PreDestroy
  public void shutdown() {
    log.info("shutdown .... ");
 
//    System.exit(1);
  }

}
