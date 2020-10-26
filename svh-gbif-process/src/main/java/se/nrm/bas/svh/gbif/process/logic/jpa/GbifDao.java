package se.nrm.bas.svh.gbif.process.logic.jpa;

import java.io.Serializable;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
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

  @PersistenceContext(unitName = "jpaGbifOhnPU")
  private EntityManager ohnEntityManager;

  private final String gb = "GB";
  private final String ume = "UME";
  private final String ups = "UPS";
  private final String ohn = "OHN";

  public GbifDao() {
  }

//  @Transactional
  public Logs createLogs(Logs logs, String institutionCode) {
    log.info("createLogs : {}", logs);

    EntityManager entityManager = getEntityManager(institutionCode);
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

    EntityManager entityManager = getEntityManager(institutionCode);
    if (entityManager != null) {
      entities.stream()
              .forEach(entity -> {
//              isExist(entity.getId(), entityManager);  
                SimpleDwc tmp = entity;
                try {
                  tmp = entityManager.merge(entity);
                } catch (OptimisticLockException | ConstraintViolationException e) {
                  log.error(e.getMessage());
                  throw e;
                }
              });
      entityManager.flush();
      entityManager.clear();
    } 
  }

  private EntityManager getEntityManager(String institutionCode) {
    switch (institutionCode) {
      case gb:
        return gbEntityManager;
      case ume:
        return umeEntityManager;
      case ups:
        return upsEntityManager;
      case ohn:
        return ohnEntityManager;
      default:
        return null;
    }
  }

  private void isExist(String id, EntityManager entityManager) {
    Query query = entityManager.createNamedQuery("SimpleDwc.findCount")
            .setParameter("id", id);
    try {
      boolean exist = ((Number) query.getSingleResult()).intValue() > 0;
      if (exist) {
        log.info("find : {}", id);
      }
    } catch (Exception e) {
      log.info(e.getMessage());
    }
  }
}
