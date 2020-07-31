package xzf.spiderman.worker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.worker.data.AddSpiderCnfReq;
import xzf.spiderman.worker.data.SpiderCnfData;
import xzf.spiderman.worker.data.QrySpiderCnfReq;
import xzf.spiderman.worker.entity.*;
import xzf.spiderman.worker.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpiderCnfService
{
    @Autowired
    private SpiderCnfRepository spiderCnfRepository;

    @Autowired
    private SpiderServerRepository spiderServerRepository;

    @Autowired
    private SpiderGroupRepository spiderGroupRepository;

    @Autowired
    private SpiderStoreRepository spiderStoreRepository;

    @Autowired
    private SpiderCnfStoreRepository spiderCnfStoreRepository;



    @Transactional
    public void add(AddSpiderCnfReq req)
    {
        if(spiderCnfRepository.findById(req.getId()).isPresent()){
            throw new BizException("SpiderCnf " + req.getId() + " , 已存在");
        }

        SpiderServer server = spiderServerRepository.getOne(req.getServerId());
        SpiderGroup group = spiderGroupRepository.getOne(req.getGroupId());
        List<SpiderStore> stores = spiderStoreRepository.findAllById(req.getStoreIds());

        List<SpiderCnfStore> cnfStores = stores.stream()
                .map(s->SpiderCnfStore.create(req.getId(),s.getId()))
                .collect(Collectors.toList());

        SpiderCnf cnf = SpiderCnf.create(req, group, server);

        spiderCnfRepository.save(cnf);
        spiderCnfStoreRepository.saveAll(cnfStores);
    }

    @Transactional(readOnly = true, propagation= Propagation.NOT_SUPPORTED)
    public Page<SpiderCnfData> findAll(QrySpiderCnfReq req, Pageable pageable)
    {
        SpiderCnf qry = new SpiderCnf();
        qry.setId(req.getStartWithId());
        qry.setGroup(new SpiderGroup(req.getEqualsGroupId()));

        ExampleMatcher matcher = ExampleMatcher.matching()
                                .withMatcher("id",m->m.startsWith())
                                .withMatcher("group.id",m->m.exact());

        Example<SpiderCnf> example = Example.of(qry, matcher);

        Page<SpiderCnf> src = spiderCnfRepository.findAll(example, pageable);

        List<SpiderCnfData> list = src.getContent().stream().map(SpiderCnf::asSpiderCnfData).collect(Collectors.toList());;

        Page<SpiderCnfData> tar = new PageImpl<>(list, src.getPageable(), src.getTotalElements());

        return tar;
    }



}
