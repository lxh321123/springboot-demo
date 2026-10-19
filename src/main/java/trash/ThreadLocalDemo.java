package trash;

import java.util.concurrent.*;

public class ThreadLocalDemo {

    static ThreadLocal<String> USER = new ThreadLocal<>();

    public static void main(String[] args) throws Exception {
        // 线程池只开 1 个线程 —— 3 个任务必然复用同一个线程
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                1, 1, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

        for (int i = 1; i <= 3; i++) {
            final int no = i;
            pool.submit(() -> {
                try {
                    String cur = USER.get();
                    if (cur == null) {
                        USER.set("用户-" + no);
                        System.out.printf("任务%d | 线程=%-16s | 首次设置  -> %s%n",
                                no, Thread.currentThread().getName(), USER.get());
                    } else {
                        System.out.printf("任务%d | 线程=%-16s | 拿到【脏数据】-> %s   <-- 上一个任务留下的！%n",
                                no, Thread.currentThread().getName(), cur);
                    }
                } finally {
                    USER.remove();
                }
                // 注意：这里【故意不调用】 USER.remove();
            });
        }

        pool.shutdown();
        pool.awaitTermination(5, TimeUnit.SECONDS);
    }
}
