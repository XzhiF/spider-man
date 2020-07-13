package xzf.spiderman.worker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import xzf.spiderman.worker.entity.SpiderCnf;

import java.util.List;

public interface SpiderCnfRepository extends JpaRepository<SpiderCnf,String>
{

    @Query("select s from SpiderCnf s where s.group.id = :groupId ")
    List<SpiderCnf> findALlByGroupId(String groupId);

}
