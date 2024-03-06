/*
 * Copyright 2023 original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package runnerdemo;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author afěi
 * @version 1.0.0
 */
@SpringBootApplication
public class Main implements ApplicationRunner {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final ExecutorService executorService = new ThreadPoolExecutor(2,
            2,
            1,
            TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(1),
            new CustomizableThreadFactory("runner-"));


    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);
    }

    /**
     * 线程交替打印测试runner
     *
     * @param args incoming application arguments
     * @author afěi
     */
    @Override
    public void run(ApplicationArguments args) {

        final int count = 100;
        executorService.execute(() -> {

            for (int i = 0; i < count; i++) {
                lock.lock();
                try {
                    // 任务A的执行逻辑
                    System.out.println("Task A executed");
                    Thread.sleep(10);
                    condition.signal();
                    condition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    lock.unlock();
                }
            }
            lock.lock();
            try {
                condition.signal();
            } finally {
                lock.unlock();
            }
        });

        executorService.execute(() -> {
            for (int i = 0; i < count; i++) {
                lock.lock();
                try {
                    // 任务B的执行逻辑
                    System.out.println("Task B executed");
                    Thread.sleep(10);
                    condition.signal();
                    condition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    lock.unlock();
                }
            }
            lock.lock();
            try {
                condition.signal();
            } finally {
                lock.unlock();
            }
        });
        executorService.shutdown();
    }
}
