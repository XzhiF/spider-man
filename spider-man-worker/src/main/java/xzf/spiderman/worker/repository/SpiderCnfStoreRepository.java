package xzf.spiderman.worker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xzf.spiderman.worker.entity.SpiderCnfStore;

public interface SpiderCnfStoreRepository extends JpaRepository<SpiderCnfStore,Integer>
{
    void deleteAllByCnfId(String cnfId);
}
