package xzf.spiderman.worker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.worker.data.*;
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
    public void add(SaveSpiderCnfReq req)
    {
        if(spiderCnfRepository.findById(req.getId()).isPresent()){
            throw new BizException("SpiderCnf " + req.getId() + " , 已存在");
        }
        save(req, null);
    }

    @Transactional
    public void update(SaveSpiderCnfReq req)
    {
        SpiderCnf cnf = getCnfById(req.getId());
        save(req, cnf);
    }

    private void save(SaveSpiderCnfReq req, SpiderCnf cnf)
    {
        SpiderServer server = spiderServerRepository.getOne(req.getServerId());
        SpiderGroup group = spiderGroupRepository.getOne(req.getGroupId());
        List<SpiderStore> stores = spiderStoreRepository.findAllById(req.getStoreIds());

        List<SpiderCnfStore> cnfStores = stores.stream()
                .map(s->SpiderCnfStore.create(req.getId(),s.getId()))
                .collect(Collectors.toList());

        if(cnf == null){
            cnf = SpiderCnf.create(req, group, server);
        }
        else {
            cnf.update(req, group, server);
        }

        spiderCnfRepository.save(cnf);
        spiderCnfStoreRepository.deleteAllByCnfId(cnf.getId());
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


    @Transactional(readOnly = true, propagation= Propagation.NOT_SUPPORTED)
    public SpiderCnfData get(String id)
    {
        SpiderCnf src = getCnfById(id);

        SpiderCnfData ret =  src.asSpiderCnfData();

        List<SpiderStoreData> stores = spiderStoreRepository.findAllByCnfId(id)
                .stream().map(SpiderStore::asSpiderStoreData).collect(Collectors.toList());

        ret.setStores(stores);

        return ret;
    }



    @Transactional
    public void delete(String id)
    {
        SpiderCnf cnf = getCnfById(id);
        if(cnf.isRunning()){
            throw new BizException("爬虫正在工作中不能删除");
        }
        //
        spiderCnfRepository.delete(cnf);
        spiderCnfStoreRepository.deleteAllByCnfId(cnf.getId());
    }

    @Transactional
    public void enable(String id)
    {
        SpiderCnf cnf = getCnfById(id);
        cnf.enable();
        spiderCnfRepository.save(cnf);
    }


    @Transactional
    public void disable(String id)
    {
        SpiderCnf cnf = getCnfById(id);
        cnf.disable();
        spiderCnfRepository.save(cnf);
    }

    private SpiderCnf getCnfById(String id)
    {
        return spiderCnfRepository.findById(id).orElseThrow(()->new BizException("未找到ID为"+id+"的配置"));
    }

}
