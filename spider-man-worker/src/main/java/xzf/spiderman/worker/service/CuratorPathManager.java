package xzf.spiderman.worker.service;

import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CuratorPathManager
{
    @Autowired
    private CuratorFramework curator;


}
