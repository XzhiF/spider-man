package xzf.spiderman.worker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.worker.data.QrySpiderServerReq;
import xzf.spiderman.worker.data.SaveSpiderGroupReq;
import xzf.spiderman.worker.data.SaveSpiderServerReq;
import xzf.spiderman.worker.data.SpiderServerData;
import xzf.spiderman.worker.entity.SpiderServer;
import xzf.spiderman.worker.repository.SpiderServerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpiderServerService
{
    @Autowired
    private SpiderServerRepository spiderServerRepository;


    @Transactional
    public void add(SaveSpiderServerReq req)
    {
        if (spiderServerRepository.findById(req.getId()).isPresent()) {
            throw new BizException("爬虫服务["+req.getId()+"]已经存在");
        }
        SpiderServer server = SpiderServer.create(req);
        spiderServerRepository.save(server);
    }

    @Transactional
    public void update(SaveSpiderServerReq req)
    {
        SpiderServer server = getServer(req.getId());
        server.update(req);
        spiderServerRepository.save(server);
    }


    @Transactional
    public void delete(String id)
    {
        int usingCount = spiderServerRepository.usingCount(id);
        if(usingCount>0){
            throw new BizException("爬虫服务["+id+"]下有"+usingCount+"个爬虫配置使用，不能删除.");
        }

        spiderServerRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED,readOnly = true)
    public SpiderServerData get(String id)
    {
        SpiderServer server = getServer(id);
        return server.asData();
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED,readOnly = true)
    public Page<SpiderServerData> findAll(QrySpiderServerReq req, Pageable pageable)
    {
        SpiderServer qry = new SpiderServer();
        qry.setId(req.getStartWithId());
        qry.setHost(req.getStartWithHost());

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("id", m -> m.startsWith())
                .withMatcher("host", m -> m.startsWith())
                .withIgnoreNullValues();


        Example<SpiderServer> example = Example.of(qry, matcher);

        Page<SpiderServer> tar = spiderServerRepository.findAll(example, pageable);

        List<SpiderServerData> list = tar.getContent().stream()
                .map(SpiderServer::asData)
                .collect(Collectors.toList());

        return new PageImpl<>(list,pageable,tar.getTotalElements());
    }



    public SpiderServer getServer(String id)
    {
        return spiderServerRepository.findById(id).orElseThrow(()->new BizException("未找到爬虫服务器["+id+"]"));
    }


}
