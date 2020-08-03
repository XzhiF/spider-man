package xzf.spiderman.worker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.worker.data.QrySpiderStoreReq;
import xzf.spiderman.worker.data.SaveSpiderStoreReq;
import xzf.spiderman.worker.data.SpiderStoreData;
import xzf.spiderman.worker.entity.SpiderStore;
import xzf.spiderman.worker.repository.SpiderStoreRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpiderStoreService
{
    @Autowired
    private SpiderStoreRepository spiderStoreRepository;


    @Transactional
    public void add(SaveSpiderStoreReq req)
    {
        if (spiderStoreRepository.findById(req.getId()).isPresent()) {
            throw new BizException("爬虫数据存储配置["+req.getId()+"]已经存在.");
        }

        SpiderStore store = SpiderStore.create(req);
        spiderStoreRepository.save(store);
    }

    @Transactional
    public void update(SaveSpiderStoreReq req)
    {
        SpiderStore store = getStore(req.getId());
        store.update(req);
        spiderStoreRepository.save(store);
    }

    @Transactional
    public void delete(String id)
    {
        int usingCount = spiderStoreRepository.usingCount(id);
        if(usingCount>0){
            throw new BizException("爬虫数据存储配置["+id+"]下有"+usingCount+"个爬虫配置关联，不能删除。");
        }
        spiderStoreRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED,readOnly = true)
    public SpiderStoreData get(String id)
    {
        SpiderStore store = getStore(id);
        return store.asData();
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED,readOnly = true)
    public Page<SpiderStoreData> findAll(QrySpiderStoreReq req, Pageable pageable)
    {
        SpiderStore qry = new SpiderStore();
        qry.setId(req.getStartWithId());
        qry.setHost(req.getStartWithHost());

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("id", m -> m.startsWith())
                .withMatcher("host", m -> m.startsWith())
                .withIgnoreNullValues();

        Example<SpiderStore> example = Example.of(qry, matcher);


        Page<SpiderStore> tar = spiderStoreRepository.findAll(example,pageable);

        List<SpiderStoreData> list = tar.getContent().stream()
                .map(SpiderStore::asData)
                .collect(Collectors.toList());

        return new PageImpl<>(list,pageable,tar.getTotalElements());
    }


    public SpiderStore getStore(String id)
    {
        return spiderStoreRepository.findById(id).orElseThrow(()-> new BizException("未找到爬虫数据存储配置["+id+"]."));
    }

}
