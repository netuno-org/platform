/*
 * Licensed to the Netuno.org under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Netuno.org licenses this file to You under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.netuno.cli;

import org.apache.logging.log4j.LogManager;
import org.netuno.psamata.Values;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.impl.matchers.GroupMatcher.jobGroupEquals;

/**
 * Manage the CRON Jobs execution.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Cron {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(Cron.class);

    private static SchedulerFactory schedulerFactory = null;
    private static Scheduler scheduler = null;

    static {
        if (scheduler == null) {
            try {
                Properties props = new Properties();
                props.setProperty(StdSchedulerFactory.PROP_JOB_STORE_CLASS, "org.quartz.simpl.RAMJobStore");
                props.setProperty(StdSchedulerFactory.PROP_THREAD_POOL_CLASS, "org.quartz.simpl.SimpleThreadPool");
                props.setProperty("org.quartz.threadPool.threadCount", Integer.toString(Config.getCronThreadCount()));
                schedulerFactory = new StdSchedulerFactory(props);
                scheduler = schedulerFactory.getScheduler();
                scheduler.start();
            } catch (Exception e) {
                logger.error("Cron scheduler initializing: " + e.getMessage(), e);
            }
        }
    }

    public static void schedule(String config, String key, String url) {
        schedule("_", key, config, url, null);
    }

    public static void schedule(String app, String key, String config, String url) {
        schedule(app, key, config, url, null);
    }

    public static Values schedules(String app) {
        try {
            Set<JobKey> jobsKeys = scheduler.getJobKeys(jobGroupEquals(app));
            Values schedules = new Values().forceList();
            for (JobKey jobKey : jobsKeys) {
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                Trigger trigger = triggers.get(0);
                JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                JobDataMap jobDataMap = jobDetail.getJobDataMap();
                schedules.add(
                        new Values()
                                .set("key", jobKey.getName())
                                .set("url", jobDataMap.getString("url"))
                                .set("params", Values.fromJSON(jobDataMap.getString("params")))
                                .set("detail", jobDetail)
                                .set("trigger", trigger)
                );
            }
            return schedules;
        } catch (Exception e) {
            logger.trace(e.getMessage(), e);
            logger.error("\n#\n# Getting schedules for the "+ app +" app:\n#     "+ e.getMessage() +"\n#\n");
        }
        return null;
    }

    public static void schedule(String app, String key, String config, String url, String params) {
        try {
            JobKey jobKey = jobKey(key, app);
            if (scheduler.checkExists(jobKey)) {
                scheduler.pauseJob(jobKey);
                scheduler.deleteJob(jobKey);
            }
            JobDetail jobDetail = newJob(CronJob.class)
                    .withIdentity(jobKey)
                    .build();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                if (!url.startsWith("/")) {
                    url = "/"+ url;
                }
                url = "http://"+ app.replace("_", "-") + ".local.netu.no:"+ Config.getPort() + url;
            }
            jobDetail.getJobDataMap().put(CronJob.URL, url);
            if (params != null) {
                logger.info("Cron job schedule {} with post url:\n{}\n{}\n", jobDetail.getKey(), url, params);
                jobDetail.getJobDataMap().put(CronJob.PARAMS, params);
            }
            Trigger trigger = newTrigger()
                    .withIdentity(key, app)
                    .startNow()
                    .withSchedule(cronSchedule(config))
                    .build();
            
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            logger.trace(e.getMessage(), e);
            logger.error("\n#\n# Cron job schedule "+ key +":\n#     "+ e.getMessage() +"\n#\n");
        }
    }

    public static boolean pause(String app, String key) throws SchedulerException {
        Set<JobKey> jobsKeys = scheduler.getJobKeys(jobGroupEquals(app));
        for (JobKey jobKey : jobsKeys) {
            if (jobKey.getName().equalsIgnoreCase(key)) {
                scheduler.pauseJob(jobKey);
                return true;
            }
        }
        return false;
    }

    public static boolean resume(String app, String key) throws SchedulerException {
        Set<JobKey> jobsKeys = scheduler.getJobKeys(jobGroupEquals(app));
        for (JobKey jobKey : jobsKeys) {
            if (jobKey.getName().equalsIgnoreCase(key)) {
                scheduler.resumeJob(jobKey);
                return true;
            }
        }
        return false;
    }

    public static boolean interrupt(String app, String key) throws SchedulerException {
        Set<JobKey> jobsKeys = scheduler.getJobKeys(jobGroupEquals(app));
        for (JobKey jobKey : jobsKeys) {
            if (jobKey.getName().equalsIgnoreCase(key)) {
                scheduler.interrupt(jobKey);
                return true;
            }
        }
        return false;
    }

    public static boolean delete(String app, String key) throws SchedulerException {
        Set<JobKey> jobsKeys = scheduler.getJobKeys(jobGroupEquals(app));
        for (JobKey jobKey : jobsKeys) {
            if (jobKey.getName().equalsIgnoreCase(key)) {
                scheduler.deleteJob(jobKey);
                return true;
            }
        }
        return false;
    }

    public static boolean checkExists(String app, String key) throws SchedulerException {
        Set<JobKey> jobsKeys = scheduler.getJobKeys(jobGroupEquals(app));
        for (JobKey jobKey : jobsKeys) {
            if (jobKey.getName().equalsIgnoreCase(key)) {
                return scheduler.checkExists(jobKey);
            }
        }
        return false;
    }
}
