package xzf.spiderman.worker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xzf.spiderman.worker.entity.SpiderStore;

import java.util.List;

public interface SpiderStoreRepository extends JpaRepository<SpiderStore, String>
{

    @Query("select o from SpiderStore o join SpiderCnfStore s on s.storeId=o.id where s.cnfId=?1")
    List<SpiderStore> findAllByCnfId(String confId);

}
