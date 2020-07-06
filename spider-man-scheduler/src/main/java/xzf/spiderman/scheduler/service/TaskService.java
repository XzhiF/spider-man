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
import xzf.spiderman.scheduler.data.UpdateTaskReq;
import xzf.spiderman.scheduler.entity.Task;
import xzf.spiderman.scheduler.entity.TaskArg;
import xzf.spiderman.scheduler.repository.TaskArgRepository;
import xzf.spiderman.scheduler.repository.TaskRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService
{
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskArgRepository taskArgRepository;

    @Transactional
    public void addTask(AddTaskReq req)
    {
        // biz checking
        if( taskRepository.findById(req.getId()).orElse(null) != null ){
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
    public void updateTask(UpdateTaskReq req)
    {
        // validation
        Task task = taskRepository.findById(req.getId())
                    .orElseThrow(()->new BizException("task " + req.getId() + ", 不存在。"));

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
        Task task = taskRepository.findById(id).orElseThrow(()->new BizException("task "+id+", 不存在"));
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
        Task task = taskRepository.findById(id).orElseThrow(()->new BizException("task "+id+", 不存在"));
        List<TaskArg> args = taskArgRepository.findAllByTaskId(id);
        TaskData ret = task.asTaskData(args);
        return ret;
    }

}
