package xzf.spiderman.worker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xzf.spiderman.worker.entity.SpiderCnf;

import java.util.List;

public interface SpiderCnfRepository extends JpaRepository<SpiderCnf,String>
{

    @Query("select s from SpiderCnf s where s.group.id= ?1 and s.status=1")
    List<SpiderCnf> findAllByGroupIdAndEnabled(String groupId);


    @Modifying
    @Query("update SpiderCnf s set s.status= :status  where  s.id= :id ")
    int updateStatus(@Param("id") String id,  @Param("status") int status);
}
