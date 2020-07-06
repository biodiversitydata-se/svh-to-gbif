package se.nrm.bas.svh.gbif.process.logic.jpa;

import java.io.Serializable;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import se.nrm.bas.svh.gbif.datamodel.Logs;
import se.nrm.bas.svh.gbif.datamodel.SimpleDwc;

/**
 *
 * @author idali
 */
@Slf4j
@TransactionManagement(TransactionManagementType.CONTAINER)
@Stateless
public class GbifDao implements Serializable {
  
  @PersistenceContext(unitName = "jpaGbifGbPU")
  private EntityManager gbEntityManager;
  
  @PersistenceContext(unitName = "jpaGbifUmePU")
  private EntityManager umeEntityManager;
  
  @PersistenceContext(unitName = "jpaGbifUpsPU")
  private EntityManager upsEntityManager;
  
  private EntityManager entityManager;
  
  public GbifDao() { 
  }
 
//  @Transactional
  public Logs createLogs(Logs logs, String institutionCode) {
    log.info("createLogs : {}", logs);

    entityManager = getEntityManager(institutionCode);
    Logs tmp = logs;
    try { 
      entityManager.persist(logs);
      entityManager.flush();
    } catch (ConstraintViolationException e) {
      log.error(e.getMessage());  
    } catch (Exception e) {
      log.error(e.getMessage()); 
    } 
    return tmp;
  }

  public void merge(List<SimpleDwc> entities, String institutionCode)
          throws OptimisticLockException, ConstraintViolationException {
//    log.info("merge : {} - {}", entities.size(), institutionCode);

    entityManager = getEntityManager(institutionCode);  
    entities.stream()
            .forEach(entity -> {
//              log.info("entity : {} -- {}", entity.toString(), entity.getInstitutionCode());
              
              SimpleDwc tmp = entity; 
              try {
                tmp = entityManager.merge(entity);
                entityManager.flush();                              // this one used for throwing OptimisticLockException if method called with web service 
              } catch (OptimisticLockException | ConstraintViolationException e) {
                log.error(e.getMessage()); 
                throw e;
              } 
            }); 
  } 
  
//  @Transactional
  public SimpleDwc merge(SimpleDwc entity, String institutionCode)
          throws OptimisticLockException, ConstraintViolationException{
    log.info("create(T) : {}", entity);
 
    entityManager = getEntityManager(institutionCode); 
    SimpleDwc tmp = entity; 
    try {
      tmp = entityManager.merge(entity);
      entityManager.flush();                              // this one used for throwing OptimisticLockException if method called with web service 
    } catch (OptimisticLockException | ConstraintViolationException e) {
      log.error(e.getMessage()); 
      throw e;
    } 
    return tmp;
  }
  
  private EntityManager getEntityManager(String institutionCode) {  
    return institutionCode.equals("GB") ? gbEntityManager 
            : institutionCode.equals("UME") ? umeEntityManager : upsEntityManager;
  }
}
