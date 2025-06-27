package org.netuno.psamata;

import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Exchanger;
import java.util.concurrent.atomic.AtomicReference;

public class EventTest {

    @Test
    public void queue() {
        class RunThread extends Thread {
            private int number = 0;

            public RunThread(int number) {
                this.number = number;
            }

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                for (int i = 0; i < 10; i++) {
                    try { Thread.sleep(100); } catch (InterruptedException e) { }
                    Values data = Values.newMap().set("uid", UUID.randomUUID().toString());
                    Values result = Event.run("queue", data);
                    if (result != null) {
                        System.out.println(number + " - " + i + " # " + (result.getString("uid").equals(data.getString("uid"))));
                    }
                }
            }
        }

        AtomicReference<String> queueThreadName = new AtomicReference<>();

        Event.setQueue("queue", (v) -> {
            try { Thread.sleep(100); } catch (InterruptedException e) { }
            queueThreadName.set(Thread.currentThread().getName());
            return Values.newMap().set("uid", v.getString("uid"));
        });

        new RunThread(1).start();
        new RunThread(2).start();
        new RunThread(3).start();
        new RunThread(4).start();
        new RunThread(5).start();

        new Thread(() -> {
            try { Thread.sleep(4000); } catch (InterruptedException e) { }
            Event.remove("queue");
            try { Thread.sleep(100); } catch (InterruptedException e) { }
            for (Thread t : Thread.getAllStackTraces().keySet()) {
                if (t.getName().equals(queueThreadName.get())) {
                    System.out.println("Queue thread still alive! "+ queueThreadName.get());
                }
            }
        }).start();

        try { Thread.sleep(10000); } catch (InterruptedException e) { }

        System.out.println("Test Queue...");
    }

}
