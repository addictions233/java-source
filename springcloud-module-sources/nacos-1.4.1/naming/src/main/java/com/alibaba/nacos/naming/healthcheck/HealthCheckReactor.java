/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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

package com.alibaba.nacos.naming.healthcheck;

import com.alibaba.nacos.naming.misc.GlobalExecutor;
import com.alibaba.nacos.naming.misc.Loggers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Health check reactor.
 *
 * @author nacos
 */
@SuppressWarnings("PMD.ThreadPoolCreationRule")
public class HealthCheckReactor {

    private static Map<String, ScheduledFuture> futureMap = new ConcurrentHashMap<>();

    /**
     * Schedule health check task.
     *
     * @param task health check task
     * @return scheduled future
     */
    public static ScheduledFuture<?> scheduleCheck(HealthCheckTask task) {
        task.setStartTime(System.currentTimeMillis());
        return GlobalExecutor.scheduleNamingHealth(task, task.getCheckRtNormalized(), TimeUnit.MILLISECONDS);
    }

    /**
     * Schedule client beat check task with a delay.
     *
     * @param task client beat check task
     */
    public static void scheduleCheck(ClientBeatCheckTask task) {
        // 健康检查定时任务:对注册的service进行定时健康检测 初始化之后延迟5s执行, 之后每隔5s执行一次
        futureMap.putIfAbsent(task.taskKey(), GlobalExecutor.scheduleNamingHealth(task, 5000, 5000, TimeUnit.MILLISECONDS));
    }

    /**
     * Cancel client beat check task.
     *
     * @param task client beat check task
     */
    public static void cancelCheck(ClientBeatCheckTask task) {
        ScheduledFuture scheduledFuture = futureMap.get(task.taskKey());
        if (scheduledFuture == null) {
            return;
        }
        try {
            scheduledFuture.cancel(true);
            futureMap.remove(task.taskKey());
        } catch (Exception e) {
            Loggers.EVT_LOG.error("[CANCEL-CHECK] cancel failed!", e);
        }
    }

    /**
     * Schedule client beat check task without a delay.
     *
     * @param task health check task
     * @return scheduled future
     */
    public static ScheduledFuture<?> scheduleNow(Runnable task) {
        return GlobalExecutor.scheduleNamingHealth(task, 0, TimeUnit.MILLISECONDS);
    }
}
