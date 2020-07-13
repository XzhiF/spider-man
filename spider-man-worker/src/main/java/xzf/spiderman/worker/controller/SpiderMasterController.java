package xzf.spiderman.worker.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.leader.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import xzf.spiderman.common.Ret;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.worker.data.SubmitSpiderReq;
import xzf.spiderman.worker.feign.SpiderMasterFeignService;
import xzf.spiderman.worker.service.MasterLeaderManager;

@RequestMapping("/worker/spider-master")
@RestController
@Slf4j
public class SpiderMasterController implements SpiderMasterFeignService
{
    @Autowired
    private MasterLeaderManager masterLeaderManager;


    @Override
    @PostMapping("/submit-spider")
    public Ret<String> submitSpider(SubmitSpiderReq req)
    {
        if (masterLeaderManager.hasLeadership()) {
            // 直接处理
            log.info(".... startTask handle");
            return Ret.success();

        }
        return forwardToMasterServer(req);
    }


    //---
    @PostMapping("/submit-spider-by-master")
    public Ret<String> submitSpiderByMaster(SubmitSpiderReq req)
    {
        log.info(".... submitSpiderByMaster handle");

        if (!masterLeaderManager.hasLeadership()) {
            throw new BizException("错误的转发。不是Leader节点。 ");
        }

        return Ret.success();
    }


    private Ret<String> forwardToMasterServer(SubmitSpiderReq req)
    {
        Participant leader = masterLeaderManager.getLeader();
        String hostAndPort = leader.getId();

        String url = "http://"+hostAndPort+"/worker/spider-master/submit-spider-by-master";

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Content-Type","application/json");

        HttpEntity<SubmitSpiderReq> postEntity = new HttpEntity<>(req, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Ret> responseEntity = restTemplate.postForEntity(url, postEntity, Ret.class);

        return responseEntity.getBody();
    }

}
