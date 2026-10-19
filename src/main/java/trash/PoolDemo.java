package trash;

import java.util.concurrent.*;

public class PoolDemo {
    public static void main(String[] args) {
        // 核心 2 个线程 / 最大 4 个线程 / 队列容量 3
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                2,                                  // 1. corePoolSize    核心线程数
                4,                                  // 2. maximumPoolSize 最大线程数
                10, TimeUnit.SECONDS,               // 3-4. 空闲线程存活时间 + 单位
                new ArrayBlockingQueue<>(3),        // 5. workQueue       有界任务队列
                Executors.defaultThreadFactory(),   // 6. threadFactory   线程工厂
                new ThreadPoolExecutor.CallerRunsPolicy()// 7. handler         拒绝策略
        );

        for (int i = 1; i <= 10; i++) {
            final int no = i;
            try {
                pool.execute(() -> {
                    System.out.printf("  [任务%-2d 执行] 线程=%-16s 池大小=%d 队列中=%d%n",
                            no, Thread.currentThread().getName(),
                            pool.getPoolSize(), pool.getQueue().size());
                    try {
                        Thread.sleep(3000);      // 模拟任务耗时，让线程池保持繁忙
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                System.out.printf("提交任务 %-2d -> 成功%n", no);
            } catch (RejectedExecutionException e) {
                System.out.printf("提交任务 %-2d -> 【被拒绝！】%n", no);
            }
        }

        pool.shutdown();
    }

    // ============================================================
    // 实验：把上面的 AbortPolicy 换成下面任意一种，重新跑，观察区别
    //
    //   new ThreadPoolExecutor.CallerRunsPolicy()      谁提交谁跑
    //   new ThreadPoolExecutor.DiscardPolicy()         静默丢弃
    //   new ThreadPoolExecutor.DiscardOldestPolicy()   丢最老的
    // ============================================================
}
