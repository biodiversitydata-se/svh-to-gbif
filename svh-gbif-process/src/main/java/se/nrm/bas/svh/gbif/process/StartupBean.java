package se.nrm.bas.svh.gbif.process;

import java.util.concurrent.CompletableFuture;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import se.nrm.bas.svh.gbif.process.logic.DataProcessor;

/**
 *
 * @author idali
 */
@Slf4j
@ApplicationScoped
public class StartupBean {

  @Inject
  private DataProcessor process;

  void init(@Observes @Initialized(ApplicationScoped.class) Object event) {
    log.info("StartupBean Application - INITIALIZATION");

    CompletableFuture.runAsync(() -> {
      process.run();
    });

    log.info("app processing....");
  }

}
