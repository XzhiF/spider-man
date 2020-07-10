package xzf.spiderman.worker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.worker.data.AddSpiderCnfReq;
import xzf.spiderman.worker.entity.SpiderCnf;
import xzf.spiderman.worker.entity.SpiderGroup;
import xzf.spiderman.worker.entity.SpiderServer;
import xzf.spiderman.worker.repository.SpiderCnfRepository;
import xzf.spiderman.worker.repository.SpiderGroupRepository;
import xzf.spiderman.worker.repository.SpiderServerRepository;

@Service
public class SpiderCnfService
{
    @Autowired
    private SpiderCnfRepository spiderCnfRepository;

    @Autowired
    private SpiderServerRepository spiderServerRepository;

    @Autowired
    private SpiderGroupRepository spiderGroupRepository;


    @Transactional
    public void add(AddSpiderCnfReq req)
    {
        if(spiderCnfRepository.findById(req.getId()).isPresent()){
            throw new BizException("SpiderCnf " + req.getId() + " , 已存在");
        }

        SpiderServer server = spiderServerRepository.getOne(req.getServerId());
        SpiderGroup group = spiderGroupRepository.getOne(req.getGroupId());
        SpiderCnf cnf = SpiderCnf.create(req, group, server);
        spiderCnfRepository.save(cnf);
    }


}
