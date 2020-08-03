package xzf.spiderman.worker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.worker.data.QrySpiderGroupReq;
import xzf.spiderman.worker.data.SaveSpiderGroupReq;
import xzf.spiderman.worker.data.SpiderGroupData;
import xzf.spiderman.worker.entity.SpiderGroup;
import xzf.spiderman.worker.repository.SpiderGroupRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpiderGroupService
{
    @Autowired
    private SpiderGroupRepository spiderGroupRepository;



    @Transactional
    public void add(SaveSpiderGroupReq req)
    {
        if (spiderGroupRepository.findById(req.getId()).isPresent()) {
            throw new BizException("爬虫组["+req.getId()+"]已经存在");
        }
        SpiderGroup group = SpiderGroup.create(req);
        spiderGroupRepository.save(group);
    }


    @Transactional
    public void update(SaveSpiderGroupReq req)
    {
        SpiderGroup group = getGroup(req.getId());
        group.update(req);
        spiderGroupRepository.save(group);
    }

    @Transactional
    public void delete(String id)
    {
        int usingCount = spiderGroupRepository.usingCount(id);
        if(usingCount>0){
            throw new BizException("爬虫组["+id+"]下有"+usingCount+"爬虫配置,不能删除");
        }
        spiderGroupRepository.deleteById(id);
    }


    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public SpiderGroupData get(String id)
    {
        SpiderGroup group = getGroup(id);
        return group.asSpiderGroupData();
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public Page<SpiderGroupData> findAll(QrySpiderGroupReq req, Pageable pageable)
    {
        SpiderGroup qry = new SpiderGroup();
        qry.setId(req.getStartWithId());

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("id", m -> m.startsWith());

        Example<SpiderGroup> example = Example.of(qry, matcher);


        Page<SpiderGroup> src = spiderGroupRepository.findAll(example, pageable);

        List<SpiderGroupData> list = src.getContent().stream()
                .map(SpiderGroup::asSpiderGroupData)
                .collect(Collectors.toList());

        Page<SpiderGroupData> tar = new PageImpl<>(list,pageable,src.getTotalElements());

        return tar;
    }

    private SpiderGroup getGroup(String id)
    {
        return spiderGroupRepository.findById(id).orElseThrow(()->new BizException("未找到爬虫组["+id+"]"));
    }

}
