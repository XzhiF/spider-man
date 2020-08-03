package xzf.spiderman.scheduler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.scheduler.data.QryTaskGroupReq;
import xzf.spiderman.scheduler.data.SaveTaskGroupReq;
import xzf.spiderman.scheduler.data.TaskGroupData;
import xzf.spiderman.scheduler.entity.TaskGroup;
import xzf.spiderman.scheduler.repository.TaskGroupRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskGroupService
{
    @Autowired
    private TaskGroupRepository taskGroupRepository;


    @Transactional
    public void add(SaveTaskGroupReq req)
    {
        if (taskGroupRepository.findById(req.getId()).isPresent()) {
            throw new BizException("任务组["+req.getId()+"]已经存在.");
        }
        TaskGroup group = TaskGroup.create(req);
        taskGroupRepository.save(group);
    }


    @Transactional
    public void update(SaveTaskGroupReq req)
    {
        TaskGroup group = getGroup(req.getId());
        group.update(req);
        taskGroupRepository.save(group);
    }

    @Transactional
    public void delete(String id)
    {
        int usingCount = taskGroupRepository.usingCount(id);
        if(usingCount>0){
            throw new BizException("任务组["+id+"]还有"+usingCount+"个任务配置，不能删除 ");
        }

        taskGroupRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public TaskGroupData get(String id)
    {
        TaskGroup group = getGroup(id);
        return group.asData();
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public Page<TaskGroupData> findAll(QryTaskGroupReq req, Pageable pageable)
    {
       TaskGroup qry = new TaskGroup();
       qry.setId(req.getStartWithId());
       qry.setName(req.getStartWithName());

        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("id", m -> m.startsWith())
                .withMatcher("name", m -> m.startsWith())
                .withIgnoreNullValues();

        Example<TaskGroup> example = Example.of(qry, matcher);

        Page<TaskGroup> tar = taskGroupRepository.findAll(example, pageable);

        List<TaskGroupData> list = tar.getContent().stream().map(TaskGroup::asData).collect(Collectors.toList());

        return new PageImpl<>(list,pageable,tar.getTotalElements());
    }




    private TaskGroup getGroup(String id)
    {
        return taskGroupRepository.findById(id).orElseThrow(()->new BizException("未找到任务组["+id+"]"));
    }

}
