package trash;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 高并发计数：AtomicLong vs LongAdder
 */
public class LongAdderDemo {
    static final int THREADS = 50;
    static final int TIMES = 500000;      // 50 × 50万 = 2500 万次自增

    interface Task { void run(); }

    public static void main(String[] args) throws Exception {
        AtomicLong atomicLong = new AtomicLong();
        LongAdder longAdder = new LongAdder();

        long t1 = run(() -> atomicLong.incrementAndGet());
        long t2 = run(() -> longAdder.increment());

        System.out.println("50 个线程，每个自增 50 万次，共 2500 万次");
        System.out.println("---------------------------------------------------");
        System.out.printf("AtomicLong  结果 = %-10d 耗时 = %d ms%n", atomicLong.get(), t1);
        System.out.printf("LongAdder   结果 = %-10d 耗时 = %d ms%n", longAdder.sum(), t2);
        System.out.println("---------------------------------------------------");
        System.out.printf("LongAdder 快了约 %.1f 倍%n", (double) t1 / t2);
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
