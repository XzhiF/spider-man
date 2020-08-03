package xzf.spiderman.worker.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xzf.spiderman.worker.entity.SpiderGroup;

public interface SpiderGroupRepository extends JpaRepository<SpiderGroup, String>
{
    @Query("select  count(o) from SpiderCnf  o where o.group.id=?1")
    int usingCount(String id);

}
