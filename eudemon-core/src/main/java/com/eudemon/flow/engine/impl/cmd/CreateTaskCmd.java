package com.eudemon.flow.engine.impl.cmd;

import org.apache.commons.lang3.StringUtils;
import com.eudemon.flow.common.engine.impl.interceptor.Command;
import com.eudemon.flow.common.engine.impl.interceptor.CommandContext;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;
import com.eudemon.flow.engine.impl.util.CountingEntityUtil;
import com.eudemon.flow.task.api.Task;
import com.eudemon.flow.task.api.TaskBuilder;
import com.eudemon.flow.task.service.impl.persistence.CountingTaskEntity;
import com.eudemon.flow.task.api.TaskEntity;
import com.eudemon.flow.task.service.impl.util.CountingTaskUtil;

/**
 * Creates new task by {@link com.eudemon.flow.task.api.TaskBuilder}
 *
 * @author martin.grofcik
 */
public class CreateTaskCmd implements Command<Task> {
    protected TaskBuilder taskBuilder;

    public CreateTaskCmd(TaskBuilder taskBuilder) {
        this.taskBuilder = taskBuilder;
    }

    @Override
    public Task execute(CommandContext commandContext) {
        Task task = CommandContextUtil.getTaskService().createTask(this.taskBuilder);
        if (CountingTaskUtil.isTaskRelatedEntityCountEnabledGlobally()) {
            if (StringUtils.isNotEmpty(task.getParentTaskId())) {
                TaskEntity parentTaskEntity = CommandContextUtil.getTaskService().getTask(task.getParentTaskId());
                if (CountingEntityUtil.isTaskRelatedEntityCountEnabled(parentTaskEntity)) {
                    CountingTaskEntity countingParentTaskEntity = (CountingTaskEntity) parentTaskEntity;
                    countingParentTaskEntity.setSubTaskCount(countingParentTaskEntity.getSubTaskCount() + 1);
                }
            }
        }
        return task;
    }
}
