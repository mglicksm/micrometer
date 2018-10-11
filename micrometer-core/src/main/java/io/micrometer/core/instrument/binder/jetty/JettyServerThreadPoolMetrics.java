/**
 * Copyright 2017 Pivotal Software, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.core.instrument.binder.jetty;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool.SizedThreadPool;

/**
 * {@link MeterBinder} for Jetty {@link ThreadPool}.
 *
 * @author Manabu Matsuzaki
 * @author Andy Wilkinson
 */
public class JettyServerThreadPoolMetrics implements MeterBinder {

    private final ThreadPool threadPool;

    private final Iterable<Tag> tags;

    public JettyServerThreadPoolMetrics(ThreadPool threadPool, Iterable<Tag> tags) {
        this.threadPool = threadPool;
        this.tags = tags;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        if (this.threadPool instanceof SizedThreadPool) {
            SizedThreadPool sizedThreadPool = (SizedThreadPool) this.threadPool;
            Gauge.builder("jetty.threads.config.min", sizedThreadPool,
                SizedThreadPool::getMinThreads)
                .description("The minimum number of threads in the pool")
                .tags(this.tags).register(registry);
            Gauge.builder("jetty.threads.config.max", sizedThreadPool,
                SizedThreadPool::getMaxThreads)
                .description("The maximum number of threads in the pool")
                .tags(this.tags).register(registry);
            if (this.threadPool instanceof QueuedThreadPool) {
                QueuedThreadPool queuedThreadPool = (QueuedThreadPool) this.threadPool;
                Gauge.builder("jetty.threads.busy", queuedThreadPool,
                    QueuedThreadPool::getBusyThreads)
                    .description("The number of busy threads in the pool")
                    .tags(this.tags).register(registry);
            }
        }
        Gauge.builder("jetty.threads.current", this.threadPool,
            ThreadPool::getThreads)
            .description("The total number of threads in the pool")
            .tags(this.tags).register(registry);
        Gauge.builder("jetty.threads.idle", this.threadPool,
            ThreadPool::getIdleThreads)
            .description("The number of idle threads in the pool").tags(this.tags)
            .register(registry);
    }

}
