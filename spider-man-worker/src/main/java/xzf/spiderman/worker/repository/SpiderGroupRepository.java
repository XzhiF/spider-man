package xzf.spiderman.worker.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import xzf.spiderman.worker.entity.SpiderGroup;

public interface SpiderGroupRepository extends JpaRepository<SpiderGroup, String>
{
}
