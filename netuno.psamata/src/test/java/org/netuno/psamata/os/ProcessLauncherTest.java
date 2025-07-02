package org.netuno.psamata.os;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ProcessLauncherTest {

    private void checkThreads() {
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            assertFalse(thread.getName().startsWith("Netuno Psamata:"), "Thread still alive: "+ thread.getName());
        }
    }

    @Test
    public void execute() throws IOException, InterruptedException {
        ProcessLauncher processLauncher = new ProcessLauncher();
        ProcessLauncher.Result result = processLauncher.execute("echo", "1");
        assertEquals(0, result.exitCode, "Exit Code");
        assertEquals("1", result.output.trim(), "Output");
    }

    @Test
    public void executeOutput() throws IOException, InterruptedException {
        ProcessLauncher processLauncher = new ProcessLauncher();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        processLauncher.outputStream(baos);
        ProcessLauncher.Result result = processLauncher.execute("echo", "1");
        assertEquals(0, result.exitCode, "Exit Code");
        assertEquals("1", baos.toString().trim(), "Output");
    }

    @Test
    public void async() throws IOException, InterruptedException {
        ProcessLauncher processLauncher = new ProcessLauncher();
        ProcessLauncher.Result result = processLauncher.executeAsync("echo", "1");
        assertEquals(0, result.exitCode, "Exit Code");
        assertEquals("1", result.output().trim(), "Output");
        checkThreads();
    }

    @Test
    public void asyncOutput() throws IOException, InterruptedException {
        ProcessLauncher processLauncher = new ProcessLauncher();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        processLauncher.outputStream(baos);
        ProcessLauncher.Result result = processLauncher.executeAsync("echo", "1");
        assertEquals(0, result.exitCode(), "Exit Code");
        assertEquals("1", baos.toString().trim(), "Output");
        checkThreads();
    }

    @Test
    public void timeLimit() throws IOException, InterruptedException {
        ProcessLauncher processLauncher = new ProcessLauncher();
        processLauncher.timeLimit(1000);
        ProcessLauncher.Result result = processLauncher.execute("echo", "1", "&&", "sleep", "2", "&&", "echo", "2");
        assertEquals(143, result.exitCode(), "Exit Code");
        assertEquals("1", result.output().toString().trim(), "Output");
        checkThreads();
    }

    @Test
    public void timeLimitAsync() throws IOException, InterruptedException {
        ProcessLauncher processLauncher = new ProcessLauncher();
        processLauncher.timeLimit(1000);
        ProcessLauncher.Result result = processLauncher.executeAsync("echo", "1", "&&", "sleep", "2", "&&", "echo", "2");
        assertEquals(143, result.exitCode(), "Exit Code");
        assertEquals("1", result.output().trim(), "Output");
        checkThreads();
    }

    @Test
    public void delay() throws IOException, InterruptedException {
        ProcessLauncher processLauncher = new ProcessLauncher();
        ProcessLauncher.Result result = processLauncher.execute("sleep 1 && echo 1 && sleep 1");
        assertEquals(0, result.exitCode(), "Exit Code");
        assertEquals("1", result.output().toString().trim(), "Output");
        checkThreads();
    }

    @Test
    public void delayOutput() throws IOException, InterruptedException {
        ProcessLauncher processLauncher = new ProcessLauncher();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        processLauncher.outputStream(baos);
        ProcessLauncher.Result result = processLauncher.execute("sleep 1 && echo 1 && sleep 1");
        assertEquals(0, result.exitCode(), "Exit Code");
        assertEquals("1", baos.toString().trim(), "Output");
        checkThreads();
    }

    @Test
    public void discardOutput() throws IOException, InterruptedException {
        ProcessLauncher processLauncher = new ProcessLauncher();
        processLauncher.readOutput(false);
        processLauncher.readErrorOutput(false);
        ProcessLauncher.Result result = processLauncher.execute("echo 1");
        assertEquals(0, result.exitCode(), "Exit Code");
        assertEquals(null, result.output(), "Output");
        assertEquals(null, result.outputError(), "Output Error");
        checkThreads();
    }

    @Test
    public void parallel() throws IOException, InterruptedException {
        AtomicInteger counter = new AtomicInteger();
        ProcessLauncher processLauncher = new ProcessLauncher();
        processLauncher.onParallel((t)-> {
            while (t.isRunning()) {
                counter.set(counter.get() + 1);
                t.pause(250);
            }
        });
        ProcessLauncher.Result result = processLauncher.execute("sleep 1");
        assertEquals(0, result.exitCode(), "Exit Code");
        assertTrue(counter.get() >= 4, "Counter");
        checkThreads();
    }
}
