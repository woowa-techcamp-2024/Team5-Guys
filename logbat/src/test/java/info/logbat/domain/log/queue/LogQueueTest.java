package info.logbat.domain.log.queue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LogQueueTest {

    //    private final ReentrantLogQueue<Integer> logQueue = new ReentrantLogQueue<>(2000L, 10);
    private final LogQueue<Integer> logQueue = new LogQueue<>(2000L, 10);

    @DisplayName("")
    @Test
    void test() throws InterruptedException {
        // given
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            logQueue.produce(
                List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21));
        });
        thread.start();

        Thread thread2 = new Thread(() -> {
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            logQueue.produce(
                List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21));
        });
        thread2.start();

        while (true) {
            List<Integer> consume = logQueue.consume();
            System.out.println("consume = " + consume);
        }

        // when

        // then

    }

}