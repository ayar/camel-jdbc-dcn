package com.eudemon.flow.engine.impl;

import com.eudemon.flow.common.engine.impl.interceptor.CommandExecutor;
import com.eudemon.flow.engine.impl.cmd.CreateTaskCmd;
import com.eudemon.flow.task.api.Task;
import com.eudemon.flow.task.api.TaskBuilder;
import com.eudemon.flow.task.service.impl.BaseTaskBuilderImpl;

/**
 * {@link TaskBuilder} implementation
 */
public class TaskBuilderImpl extends BaseTaskBuilderImpl {
    TaskBuilderImpl(CommandExecutor commandExecutor) {
        super(commandExecutor);
    }

    @Override
    public Task create() {
        return commandExecutor.execute(new CreateTaskCmd(this));
    }

}
