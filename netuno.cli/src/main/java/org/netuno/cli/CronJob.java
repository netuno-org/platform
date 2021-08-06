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
import org.netuno.psamata.net.Remote;
import org.netuno.psamata.net.Remote.Response;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * CRON Job execution.
 * 
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class CronJob implements Job {
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(CronJob.class);

    public static final String URL = "url";
    public static final String PARAMS = "params";

    public CronJob() {

    }

    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("Job "+ context.getJobDetail().getKey() +" starting.");
        String url = "";
        Values params = null;
        try {
            JobDataMap dataMap = context.getJobDetail().getJobDataMap();
            url = dataMap.getString(URL);
            if (dataMap.containsKey(PARAMS) && dataMap.get(PARAMS) != null) {
                params = Values.fromJSON(dataMap.get(PARAMS).toString());
            } else {
            	params = new Values();
            }
            logger.info("Job {} sending post to:\n{}\n{}\n", context.getJobDetail().getKey(), url, params.toJSON());
            Response response = new Remote().asJSON().post(
                    url,
                    params
            );
            if (!response.isOk()) {
            	logger.error(
            			"Job "+ context.getJobDetail().getKey() +" status "+ response.statusCode +" when sending POST to:\n{}\n{}\n",
                		url,
                		params.toJSON(),
                		response.toString());
            }
        } catch (Throwable e) {
            logger.error("Job "+ context.getJobDetail().getKey() +" execute error when sending POST to:\n{}\n{}\n",
            		context.getJobDetail().getKey(),
            		url,
            		params.toJSON(),
            		e);
        }
    }
}
