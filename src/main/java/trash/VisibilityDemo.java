package trash;

public class VisibilityDemo {

    // 实验：改成 volatile static boolean flag = false; 再跑一次，对比结果
    static volatile boolean flag = false;

    public static void main(String[] args) throws Exception {
        Thread t = new Thread(() -> {
            System.out.println("子线程：开始空转，等 flag 变 true ...");
            long n = 0;
            while (!flag) {          // 死等 flag 变成 true
                n++;
            }
            System.out.println("子线程：看到了 flag = true，退出循环 ✅（空转了 " + n + " 次）");
        });
        t.setDaemon(true);           // 设为守护线程，防止主线程结束后 JVM 卡住
        t.start();

        Thread.sleep(1000);
        flag = true;                 // 主线程改 flag
        System.out.println("主线程：已经把 flag 改成 true 了");
        Thread.sleep(2000);
        System.out.println("主线程：结束");
    }
}
