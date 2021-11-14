import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Server {
    private final List<Integer> integers = new ArrayList<>();

    public static void main(String[] args) {
        Server server = new Server();
        final ReentrantLock thread1Lock = new ReentrantLock(true);
        Condition condition = thread1Lock.newCondition();

        Thread thread1 = new Thread(() -> {
            thread1Lock.lock();

            try {
                for (int i = 0; i < 50; i += 2) {
                    server.addNumber(i);
                    condition.await();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                thread1Lock.unlock();
            }

        });

        Thread thread2 = new Thread(() -> {

            for (int i = 1; i < 50; i += 2) {

                thread1Lock.lock();

                try {
                    server.addNumber(i);

                    condition.signal();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    thread1Lock.unlock();
                }

            }
        });

        thread1.start();
        thread2.start();

        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        server.show();
    }

    private void show() {
        String array = integers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        System.out.println(array);
    }

    public void addNumber(int i) {
        integers.add(i);
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(200, 500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
}