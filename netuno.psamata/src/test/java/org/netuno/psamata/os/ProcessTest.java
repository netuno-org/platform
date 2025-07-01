package org.netuno.psamata.os;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ProcessTest {

    private void checkThreads() {
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            assertFalse(thread.getName().startsWith("Netuno Psamata:"), "");
        }
    }

    @Test
    public void execute() throws IOException, InterruptedException {
        Process process = new Process();
        Process.Result result = process.execute("echo", "1");
        assertEquals(0, result.exitCode, "Exit Code");
        assertEquals("1", result.output.trim(), "Output");
    }

    @Test
    public void executeOutput() throws IOException, InterruptedException {
        Process process = new Process();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        process.outputStream(baos);
        Process.Result result = process.execute("echo", "1");
        assertEquals(0, result.exitCode, "Exit Code");
        assertEquals("1", baos.toString().trim(), "Output");
    }

    @Test
    public void async() throws IOException, InterruptedException {
        Process process = new Process();
        Process.Result result = process.executeAsync("echo", "1");
        assertEquals(0, result.exitCode, "Exit Code");
        assertEquals("1", result.output().trim(), "Output");
        checkThreads();
    }

    @Test
    public void asyncOutput() throws IOException, InterruptedException {
        Process process = new Process();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        process.outputStream(baos);
        Process.Result result = process.executeAsync("echo", "1");
        assertEquals(0, result.exitCode(), "Exit Code");
        assertEquals("1", baos.toString().trim(), "Output");
        checkThreads();
    }

    @Test
    public void timeLimit() throws IOException, InterruptedException {
        Process process = new Process();
        process.timeLimit(1000);
        Process.Result result = process.execute("echo", "1", "&&", "sleep", "2", "&&", "echo", "2");
        assertEquals(143, result.exitCode(), "Exit Code");
        assertEquals("1", result.output().toString().trim(), "Output");
        checkThreads();
    }

    @Test
    public void timeLimitAsync() throws IOException, InterruptedException {
        Process process = new Process();
        process.timeLimit(1000);
        Process.Result result = process.executeAsync("echo", "1", "&&", "sleep", "2", "&&", "echo", "2");
        assertEquals(143, result.exitCode(), "Exit Code");
        assertEquals("1", result.output().trim(), "Output");
        checkThreads();
    }
}
