/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.eudemon.flow.engine.impl;

import com.eudemon.flow.common.engine.api.FlowableException;
import com.eudemon.flow.common.engine.api.FlowableIllegalArgumentException;
import com.eudemon.flow.common.engine.api.management.TableMetaData;
import com.eudemon.flow.common.engine.api.management.TablePageQuery;
import com.eudemon.flow.common.engine.impl.cmd.CustomSqlExecution;
import com.eudemon.flow.common.engine.impl.db.DbSqlSession;
import com.eudemon.flow.common.engine.impl.db.DbSqlSessionFactory;
import com.eudemon.flow.common.engine.impl.interceptor.Command;
import com.eudemon.flow.common.engine.impl.interceptor.CommandConfig;
import com.eudemon.flow.common.engine.impl.interceptor.CommandContext;
import com.eudemon.flow.common.engine.impl.service.CommonEngineServiceImpl;
import com.eudemon.flow.engine.ManagementService;
import com.eudemon.flow.engine.event.EventLogEntry;
import com.eudemon.flow.engine.impl.cfg.ProcessEngineConfigurationImpl;
import com.eudemon.flow.engine.impl.cmd.DeleteEventLogEntry;
import com.eudemon.flow.engine.impl.cmd.ExecuteCustomSqlCmd;
import com.eudemon.flow.engine.impl.cmd.GetEventLogEntriesCmd;
import com.eudemon.flow.engine.impl.cmd.GetPropertiesCmd;
import com.eudemon.flow.engine.impl.cmd.GetTableCountCmd;
import com.eudemon.flow.engine.impl.cmd.GetTableMetaDataCmd;
import com.eudemon.flow.engine.impl.cmd.GetTableNameCmd;
import com.eudemon.flow.engine.impl.cmd.RescheduleTimerJobCmd;
import com.eudemon.flow.engine.impl.util.CommandContextUtil;
import com.eudemon.flow.job.api.DeadLetterJobQuery;
import com.eudemon.flow.job.api.HistoryJobQuery;
import com.eudemon.flow.job.api.Job;
import com.eudemon.flow.job.api.JobQuery;
import com.eudemon.flow.job.api.SuspendedJobQuery;
import com.eudemon.flow.job.api.TimerJobQuery;
import com.eudemon.flow.job.service.impl.DeadLetterJobQueryImpl;
import com.eudemon.flow.job.service.impl.HistoryJobQueryImpl;
import com.eudemon.flow.job.service.impl.JobQueryImpl;
import com.eudemon.flow.job.service.impl.SuspendedJobQueryImpl;
import com.eudemon.flow.job.service.impl.TimerJobQueryImpl;
import com.eudemon.flow.job.service.impl.cmd.DeleteDeadLetterJobCmd;
import com.eudemon.flow.job.service.impl.cmd.DeleteHistoryJobCmd;
import com.eudemon.flow.job.service.impl.cmd.DeleteJobCmd;
import com.eudemon.flow.job.service.impl.cmd.DeleteSuspendedJobCmd;
import com.eudemon.flow.job.service.impl.cmd.DeleteTimerJobCmd;
import com.eudemon.flow.job.service.impl.cmd.ExecuteHistoryJobCmd;
import com.eudemon.flow.job.service.impl.cmd.ExecuteJobCmd;
import com.eudemon.flow.job.service.impl.cmd.GetJobExceptionStacktraceCmd;
import com.eudemon.flow.job.service.impl.cmd.JobType;
import com.eudemon.flow.job.service.impl.cmd.MoveDeadLetterJobToExecutableJobCmd;
import com.eudemon.flow.job.service.impl.cmd.MoveJobToDeadLetterJobCmd;
import com.eudemon.flow.job.service.impl.cmd.MoveSuspendedJobToExecutableJobCmd;
import com.eudemon.flow.job.service.impl.cmd.MoveTimerToExecutableJobCmd;
import com.eudemon.flow.job.service.impl.cmd.SetJobRetriesCmd;
import com.eudemon.flow.job.service.impl.cmd.SetTimerJobRetriesCmd;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * @author Tom Baeyens
 * @author Joram Barrez
 * @author Falko Menge
 * @author Saeid Mizaei
 */
public class ManagementServiceImpl extends CommonEngineServiceImpl<ProcessEngineConfigurationImpl> implements ManagementService {

    @Override
    public Map<String, Long> getTableCount() {
        return commandExecutor.execute(new GetTableCountCmd());
    }

    @Override
    public String getTableName(Class<?> entityClass) {
        return commandExecutor.execute(new GetTableNameCmd(entityClass));
    }

    @Override
    public TableMetaData getTableMetaData(String tableName) {
        return commandExecutor.execute(new GetTableMetaDataCmd(tableName));
    }

    @Override
    public void executeJob(String jobId) {
        try {
            commandExecutor.execute(new ExecuteJobCmd(jobId));

        } catch (RuntimeException e) {
            if (e instanceof FlowableException) {
                throw e;
            } else {
                throw new FlowableException("Job " + jobId + " failed", e);
            }
        }
    }

    @Override
    public void executeHistoryJob(String historyJobId) {
        commandExecutor.execute(new ExecuteHistoryJobCmd(historyJobId));
    }

    @Override
    public Job moveTimerToExecutableJob(String jobId) {
        return commandExecutor.execute(new MoveTimerToExecutableJobCmd(jobId));
    }

    @Override
    public Job moveJobToDeadLetterJob(String jobId) {
        return commandExecutor.execute(new MoveJobToDeadLetterJobCmd(jobId));
    }

    @Override
    public Job moveDeadLetterJobToExecutableJob(String jobId, int retries) {
        return commandExecutor.execute(new MoveDeadLetterJobToExecutableJobCmd(jobId, retries));
    }

    @Override
    public Job moveSuspendedJobToExecutableJob(String jobId) {
        return commandExecutor.execute(new MoveSuspendedJobToExecutableJobCmd(jobId));
    }

    @Override
    public void deleteJob(String jobId) {
        commandExecutor.execute(new DeleteJobCmd(jobId));
    }

    @Override
    public void deleteTimerJob(String jobId) {
        commandExecutor.execute(new DeleteTimerJobCmd(jobId));
    }

    @Override
    public void deleteSuspendedJob(String jobId) {
        commandExecutor.execute(new DeleteSuspendedJobCmd(jobId));
    }

    @Override
    public void deleteDeadLetterJob(String jobId) {
        commandExecutor.execute(new DeleteDeadLetterJobCmd(jobId));
    }

    @Override
    public void deleteHistoryJob(String jobId) {
        commandExecutor.execute(new DeleteHistoryJobCmd(jobId));
    }

    @Override
    public void setJobRetries(String jobId, int retries) {
        commandExecutor.execute(new SetJobRetriesCmd(jobId, retries));
    }

    @Override
    public void setTimerJobRetries(String jobId, int retries) {
        commandExecutor.execute(new SetTimerJobRetriesCmd(jobId, retries));
    }

    @Override
    public Job rescheduleTimeDateJob(String jobId, String timeDate) {
        return commandExecutor.execute(new RescheduleTimerJobCmd(jobId, timeDate, null, null, null, null));
    }

    @Override
    public Job rescheduleTimeDurationJob(String jobId, String timeDuration) {
        return commandExecutor.execute(new RescheduleTimerJobCmd(jobId, null, timeDuration, null, null, null));
    }

    @Override
    public Job rescheduleTimeCycleJob(String jobId, String timeCycle) {
        return commandExecutor.execute(new RescheduleTimerJobCmd(jobId, null, null, timeCycle, null, null));
    }

    @Override
    public Job rescheduleTimerJob(String jobId, String timeDate, String timeDuration, String timeCycle, String endDate, String calendarName) {
        return commandExecutor.execute(new RescheduleTimerJobCmd(jobId, timeDate, timeDuration, timeCycle, endDate, calendarName));
    }

    @Override
    public TablePageQuery createTablePageQuery() {
        return new TablePageQueryImpl(commandExecutor);
    }

    @Override
    public JobQuery createJobQuery() {
        return new JobQueryImpl(commandExecutor);
    }

    @Override
    public TimerJobQuery createTimerJobQuery() {
        return new TimerJobQueryImpl(commandExecutor);
    }

    @Override
    public SuspendedJobQuery createSuspendedJobQuery() {
        return new SuspendedJobQueryImpl(commandExecutor);
    }

    @Override
    public DeadLetterJobQuery createDeadLetterJobQuery() {
        return new DeadLetterJobQueryImpl(commandExecutor);
    }

    @Override
    public HistoryJobQuery createHistoryJobQuery() {
        return new HistoryJobQueryImpl(commandExecutor);
    }

    @Override
    public String getJobExceptionStacktrace(String jobId) {
        return commandExecutor.execute(new GetJobExceptionStacktraceCmd(jobId, JobType.ASYNC));
    }

    @Override
    public String getTimerJobExceptionStacktrace(String jobId) {
        return commandExecutor.execute(new GetJobExceptionStacktraceCmd(jobId, JobType.TIMER));
    }

    @Override
    public String getSuspendedJobExceptionStacktrace(String jobId) {
        return commandExecutor.execute(new GetJobExceptionStacktraceCmd(jobId, JobType.SUSPENDED));
    }

    @Override
    public String getDeadLetterJobExceptionStacktrace(String jobId) {
        return commandExecutor.execute(new GetJobExceptionStacktraceCmd(jobId, JobType.DEADLETTER));
    }

    @Override
    public Map<String, String> getProperties() {
        return commandExecutor.execute(new GetPropertiesCmd());
    }

    @Override
    public String databaseSchemaUpgrade(final Connection connection, final String catalog, final String schema) {
        CommandConfig config = commandExecutor.getDefaultConfig().transactionNotSupported();
        return commandExecutor.execute(config, new Command<String>() {
            @Override
            public String execute(CommandContext commandContext) {
                DbSqlSessionFactory dbSqlSessionFactory = (DbSqlSessionFactory) commandContext.getSessionFactories().get(DbSqlSession.class);
                DbSqlSession dbSqlSession = new DbSqlSession(dbSqlSessionFactory, CommandContextUtil.getEntityCache(commandContext), connection, catalog, schema);
                commandContext.getSessions().put(DbSqlSession.class, dbSqlSession);
                return CommandContextUtil.getProcessEngineConfiguration(commandContext).getSchemaManager().schemaUpdate();
            }
        });
    }

    @Override
    public <T> T executeCommand(Command<T> command) {
        if (command == null) {
            throw new FlowableIllegalArgumentException("The command is null");
        }
        return commandExecutor.execute(command);
    }

    @Override
    public <T> T executeCommand(CommandConfig config, Command<T> command) {
        if (config == null) {
            throw new FlowableIllegalArgumentException("The config is null");
        }
        if (command == null) {
            throw new FlowableIllegalArgumentException("The command is null");
        }
        return commandExecutor.execute(config, command);
    }

    @Override
    public <MapperType, ResultType> ResultType executeCustomSql(CustomSqlExecution<MapperType, ResultType> customSqlExecution) {
        Class<MapperType> mapperClass = customSqlExecution.getMapperClass();
        return commandExecutor.execute(new ExecuteCustomSqlCmd<>(mapperClass, customSqlExecution));
    }

    @Override
    public List<EventLogEntry> getEventLogEntries(Long startLogNr, Long pageSize) {
        return commandExecutor.execute(new GetEventLogEntriesCmd(startLogNr, pageSize));
    }

    @Override
    public List<EventLogEntry> getEventLogEntriesByProcessInstanceId(String processInstanceId) {
        return commandExecutor.execute(new GetEventLogEntriesCmd(processInstanceId));
    }

    @Override
    public void deleteEventLogEntry(long logNr) {
        commandExecutor.execute(new DeleteEventLogEntry(logNr));
    }

}
