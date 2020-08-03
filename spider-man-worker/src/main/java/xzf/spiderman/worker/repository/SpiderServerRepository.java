package xzf.spiderman.worker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xzf.spiderman.worker.entity.SpiderServer;

public interface SpiderServerRepository extends JpaRepository<SpiderServer, String>
{

    @Query("select count(o) from SpiderCnf  o where o.server.id=?1")
    int usingCount(String id);

}

