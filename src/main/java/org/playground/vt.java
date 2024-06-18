package org.playground;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

public class vt {

    public static void intro() throws InterruptedException {
        Thread platformThread = Thread.ofPlatform().unstarted(() -> System.out.println("Hello from: " + Thread.currentThread()));

        // unstarted thread, start it now
        platformThread.start();
        // prevent main thread from dying before this thread's log is printed
        platformThread.join();
        // ^^ Hello from: Thread[#21,Thread-0,5,main]


        Thread virtualThread = Thread.ofVirtual().unstarted(() -> System.out.println("Hello from: " + Thread.currentThread()));
        virtualThread.start();
        virtualThread.join();
        // ^^ Hello from: VirtualThread[#22]/runnable@ForkJoinPool-1-worker-1
        System.out.println("Class of virtual thread: " + virtualThread.getClass());
        // ^^ Class of virtual thread: class java.lang.VirtualThread


        var task = ForkJoinPool.commonPool().submit(() -> System.out.println("Hello from: " + Thread.currentThread()));
        task.join();
        // Hello from: Thread[#25,ForkJoinPool.commonPool-worker-1,5,main]
    }

    public static void hopping() throws InterruptedException {

        var threads = IntStream.range(0, 10).mapToObj(
                index -> Thread.ofVirtual().unstarted(() -> {
                    if (index == 0) {
                        System.out.println("before sleep: platformThread: " + Thread.currentThread());
                    }

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    if (index == 0) {
                        System.out.println("after sleep: platformThread: " + Thread.currentThread());
                    }

                })
        ).toList();
        threads.forEach(Thread::start);
        for (Thread t : threads) {
            t.join();
        }
        /*
            From the below log of Thread#0, we see that it came back on a different thread that it started on before going to sleep
            This demonstrates the idea that virtual threads, during IO (sleep in this case) is off-loaded to heap from the stack
            and then when the IO (sleep in this case) is done, they can come back in another platform thread.
            Thus a single platform thread can work with many virtual threads.
            NOTE: If any native call, or `synchronized` usage, in the virtual thread, then java will pin the virtual thread to the same platform thread
            ------------------------------------------------------------------------------------------------------------
            before sleep: platformThread: VirtualThread[#21]/runnable@ForkJoinPool-1-worker-1
            after sleep: platformThread: VirtualThread[#21]/runnable@ForkJoinPool-1-worker-8
        */
    }

    public static void utilize() throws InterruptedException {
        Set<String> poolNames = ConcurrentHashMap.newKeySet();
        Set<String> threadNames = ConcurrentHashMap.newKeySet();

        List<Thread> threads = IntStream.range(0, 1_000_000).mapToObj(i -> Thread.ofVirtual().unstarted(() -> {
            poolNames.add(readPoolName());
            threadNames.add(readWorkerName());
        })).toList();

        threads.forEach(Thread::start);
        for (Thread t: threads) {
            t.join();
        }

        System.out.println("pool count: " + poolNames.size());
        System.out.println("worker count: " + threadNames.size());
    }

    public static String readPoolName() {
        String threadName = Thread.currentThread().toString();
        int i1 = threadName.indexOf("ForkJoinPool");
        int i2 = threadName.indexOf("worker");
        return threadName.substring(i1, i2);
    }

    public static String readWorkerName() {
        String threadName = Thread.currentThread().toString();
        int i1 = threadName.indexOf("worker");
        return threadName.substring(i1);
    }

    public static void main(String[] args) throws InterruptedException {
        Instant startTime = Instant.now();
        utilize();
        System.out.println("Completed in: " + Duration.between(startTime, Instant.now()).toMillis());
    }
}
