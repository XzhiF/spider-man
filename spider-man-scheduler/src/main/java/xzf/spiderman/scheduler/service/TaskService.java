package xzf.spiderman.scheduler.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import xzf.spiderman.common.exception.BizException;
import xzf.spiderman.scheduler.data.AddTaskReq;
import xzf.spiderman.scheduler.data.QryTaskReq;
import xzf.spiderman.scheduler.data.TaskData;
import xzf.spiderman.scheduler.data.UptTaskReq;
import xzf.spiderman.scheduler.entity.Task;
import xzf.spiderman.scheduler.entity.TaskArg;
import xzf.spiderman.scheduler.repository.TaskArgRepository;
import xzf.spiderman.scheduler.repository.TaskRepository;
import xzf.spiderman.scheduler.service.event.TaskDisabledEvent;
import xzf.spiderman.scheduler.service.event.TaskEnabledEvent;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService
{
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskArgRepository taskArgRepository;
    @Autowired
    private EventPublisherRegistry eventPublisherRegistry;

    @Transactional
    public void add(AddTaskReq req)
    {
        // biz checking
        if( taskRepository.findById(req.getId()).isPresent() ){
            throw new BizException("task :"+req.getId()+"， 已存在。");
        }

        // create entities
        Task task = Task.create(req);
        List<TaskArg> args = task.createArgs(req.getArgs());

        // save in db
        taskRepository.save(task);
        taskArgRepository.saveAll(args);
    }

    @Transactional
    public void update(UptTaskReq req)
    {
        // validation
        Task task = getTask(req.getId());

        // update entities
        task.update(req);
        List<TaskArg> args = task.createArgs(req.getArgs());

        // save in db
        taskRepository.save(task);

        taskArgRepository.deleteAllByTaskId(req.getId());
        taskArgRepository.saveAll(args);
    }

    @Transactional
    public void delete(String id)
    {
        Task task = getTask(id);
        taskRepository.delete(task);
        taskArgRepository.deleteAllByTaskId(task.getId());
    }


    @Transactional(readOnly = true, propagation= Propagation.NOT_SUPPORTED)
    public Page<TaskData> findAll(QryTaskReq req, Pageable pageable)
    {
        Task qry = new Task();
        qry.setId(req.getStartWithId());
        qry.setName(req.getStartWithName());

        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("id", m -> m.startsWith())
                .withMatcher("name", m -> m.startsWith())
                .withIgnoreNullValues();

        Example<Task> example = Example.of(qry, matcher);

        Page<Task> src = taskRepository.findAll(example, pageable);

        List<TaskData> list = src.getContent().stream().map(Task::asTaskData).collect(Collectors.toList());

        Page<TaskData> tar = new PageImpl<>( list ,src.getPageable(),src.getTotalElements());

        return tar;
    }

    @Transactional(readOnly = true, propagation = Propagation.NOT_SUPPORTED)
    public TaskData findById(String id)
    {
        Task task = getTask(id);
        List<TaskArg> args = taskArgRepository.findAllByTaskId(id);
        TaskData ret = task.asTaskData(args);
        return ret;
    }

    private Task getTask(String id)
    {
        Task task = taskRepository.findById(id).orElseThrow(()->new BizException("task "+id+", 不存在"));
        return task;
    }

    @Transactional
    public void enable(String id)
    {
        Task task = getTask(id);
        task.setActiveFlag(Task.ACTIVE_FLAG_ENABLE);
        taskRepository.save(task);

        // 发布事件
        eventPublisherRegistry.taskEventPublisher()
                .publish(new TaskEnabledEvent(task));

    }

    @Transactional
    public void disable(String id)
    {
        Task task = getTask(id);
        task.setActiveFlag(Task.ACTIVE_FLAG_DISABLE);
        taskRepository.save(task);

        // 发布事件
        eventPublisherRegistry.taskEventPublisher()
                .publish(new TaskDisabledEvent(task));
    }




}
