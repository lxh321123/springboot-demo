package trash;

public class AtomicityDemo {

    // 注意：这里【加了 volatile】，但结果依然会出错
    static volatile int count = 0;

    public static void main(String[] args) throws Exception {
        int threadNum = 20;
        int times = 100000;
        Thread[] threads = new Thread[threadNum];
        for (int i = 0; i < threadNum; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < times; j++) {
                    count++;             // 读 -> 加1 -> 写回，三步，不是原子操作
                }
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.println(threadNum + " 个线程各加 " + times + " 次，期望结果 = " + (threadNum * times));
        System.out.println("实际结果 = " + count);
        System.out.println(count == 10000 ? "✅ 正确" : "❌ 丢更新了！volatile 救不了原子性");
    }
}
