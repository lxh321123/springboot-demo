package trash;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 三种方式解决 count++ 的原子性，对比【正确性】和【性能】
 */
public class CasDemo {
    static final int THREADS = 20;
    static final int TIMES = 100000;

    static volatile int vCount = 0;                       // volatile —— 不保证原子性
    static AtomicInteger aCount = new AtomicInteger(0);   // AtomicInteger —— CAS 无锁
    static int sCount = 0;                                // synchronized —— 加锁
    static final Object lock = new Object();

    interface Task { void run(); }

    public static void main(String[] args) throws Exception {
        long t1 = run(() -> vCount++);
        long t2 = run(() -> { synchronized (lock) { sCount++; } });
        long t3 = run(() -> aCount.incrementAndGet());

        System.out.println("期望值 = " + (THREADS * TIMES) + "（20 线程 × 10 万次）");
        System.out.println("--------------------------------------------------");
        System.out.printf("volatile       结果 = %-9d 耗时 = %d ms%n", vCount, t1);
        System.out.printf("synchronized   结果 = %-9d 耗时 = %d ms%n", sCount, t2);
        System.out.printf("AtomicInteger  结果 = %-9d 耗时 = %d ms%n", aCount.get(), t3);
    }

    static long run(Task task) throws Exception {
        long start = System.currentTimeMillis();
        Thread[] ts = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++) {
            ts[i] = new Thread(() -> {
                for (int j = 0; j < TIMES; j++) task.run();
            });
        }
        for (Thread t : ts) t.start();
        for (Thread t : ts) t.join();
        return System.currentTimeMillis() - start;
    }
}
