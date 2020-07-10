package xzf.spiderman.worker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xzf.spiderman.worker.entity.SpiderStore;

public interface SpiderStoreRepository extends JpaRepository<SpiderStore, String>
{
}
