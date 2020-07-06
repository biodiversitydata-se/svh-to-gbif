package se.nrm.bas.svh.gbif.process;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import se.nrm.bas.svh.gbif.process.logic.DataProcess;

/**
 *
 * @author idali
 */
@Slf4j
@ApplicationScoped
public class StartupBean {
  
  @Inject
  private DataProcess process;
  
  void init(@Observes @Initialized(ApplicationScoped.class) Object event) {
    log.info("My Application - INITIALIZATION"); 
    
    process.run();
  }

}
