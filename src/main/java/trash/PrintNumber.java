package trash;

class PrintNumber{
    private static final Object lock = new Object();
    private static int number = 1;

    public static void main(String[] args) {
        Thread A = new Thread(() -> {
            while(number <= 100){
                synchronized (lock){
                    while(number % 2 == 0){
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if(number > 100) break;
                    System.out.println("线程A打印：" + number);
                    number ++;
                    lock.notifyAll();

                }
            }
        });
        Thread B = new Thread(() -> {
            while(number <= 100){
                synchronized (lock){
                    while(number % 2 == 1){
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if(number > 100) break;
                    System.out.println("线程B打印：" + number);
                    number ++;
                    lock.notifyAll();

                }
            }
        });
        A.start();
        B.start();
    }
}