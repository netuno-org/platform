/*
 * Licensed to the Netuno.org under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The Netuno.org licenses this file to You under the Apache License, Version
 * 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.netuno.psamata.os;

import org.apache.commons.lang3.SystemUtils;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.File;
import org.netuno.psamata.io.OutputStream;
import org.netuno.psamata.io.StreamGobbler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * Process Launcher
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ProcessLauncher {

    public String directory = ".";
    public boolean shell = true;
    public String shellCommand = "";
    public String shellParameter = "";
    public Values env = new Values();

    public boolean readOutput = true;
    public boolean readErrorOutput = true;

    public boolean inheritOutput = false;
    public boolean inheritErrorOutput = false;

    public long waitFor = 100;

    public boolean redirectErrorStream = false;

    public java.io.OutputStream outputStream = null;
    public java.io.OutputStream errorOutputStream = null;

    public Consumer<String> outputLineConsumer = null;
    public Consumer<String> errorOutputLineConsumer = null;

    public boolean autoCloseOutputStreams = false;

    public long timeLimit = 0;

    public boolean await = true;

    public Consumer<ParallelThread> onParallel = null;
    public Consumer<Result> onFinish = null;

    private ProcessBuilder builder = new ProcessBuilder();

    public ProcessLauncher() {

    }

    public boolean readOutput() {
        return isReadOutput();
    }

    public boolean isReadOutput() {
        return readOutput;
    }

    public ProcessLauncher readOutput(boolean readOutput) {
        setReadOutput(readOutput);
        return this;
    }

    public ProcessLauncher setReadOutput(boolean readOutput) {
        this.readOutput = readOutput;
        builder.redirectOutput(readOutput ? ProcessBuilder.Redirect.PIPE : ProcessBuilder.Redirect.DISCARD);
        return this;
    }

    public boolean readErrorOutput() {
        return isReadErrorOutput();
    }

    public boolean isReadErrorOutput() {
        return readErrorOutput;
    }

    public ProcessLauncher readErrorOutput(boolean readErrorOutput) {
        setReadErrorOutput(readErrorOutput);
        return this;
    }

    public ProcessLauncher setReadErrorOutput(boolean readErrorOutput) {
        this.readErrorOutput = readErrorOutput;
        builder.redirectError(readErrorOutput ? ProcessBuilder.Redirect.PIPE : ProcessBuilder.Redirect.DISCARD);
        return this;
    }

    public boolean inheritOutput() {
        return isInheritOutput();
    }

    public boolean isInheritOutput() {
        return inheritOutput;
    }

    public ProcessLauncher inheritOutput(boolean inheritOutput) {
        setInheritOutput(inheritOutput);
        return this;
    }

    public ProcessLauncher setInheritOutput(boolean inheritOutput) {
        this.inheritOutput = inheritOutput;
        builder.redirectOutput(inheritOutput ? ProcessBuilder.Redirect.INHERIT : ProcessBuilder.Redirect.PIPE);
        return this;
    }

    public boolean inheritErrorOutput() {
        return isInheritErrorOutput();
    }

    public boolean isInheritErrorOutput() {
        return inheritErrorOutput;
    }

    public ProcessLauncher inheritErrorOutput(boolean inheritErrorOutput) {
        setInheritErrorOutput(inheritErrorOutput);
        return this;
    }

    public ProcessLauncher setInheritErrorOutput(boolean inheritErrorOutput) {
        this.inheritErrorOutput = inheritErrorOutput;
        builder.redirectError(inheritErrorOutput ? ProcessBuilder.Redirect.INHERIT : ProcessBuilder.Redirect.PIPE);
        return this;
    }

    public boolean redirectErrorStream() {
        return isRedirectErrorStream();
    }

    public boolean isRedirectErrorStream() {
        return redirectErrorStream;
    }

    public ProcessLauncher redirectErrorStream(boolean redirectErrorStream) {
        readErrorOutput(false);
        setRedirectErrorStream(redirectErrorStream);
        return this;
    }

    public ProcessLauncher setRedirectErrorStream(boolean redirectErrorStream) {
        this.redirectErrorStream = redirectErrorStream;
        builder.redirectErrorStream(redirectErrorStream);
        return this;
    }

    public long waitFor() {
        return getWaitFor();
    }

    public long getWaitFor() {
        return waitFor;
    }

    public ProcessLauncher waitFor(long waitFor) {
        setWaitFor(waitFor);
        return this;
    }

    public ProcessLauncher setWaitFor(long waitFor) {
        this.waitFor = waitFor;
        return this;
    }

    public String directory() {
        return this.directory;
    }
    public String getDirectory() {
        return this.directory;
    }

    public ProcessLauncher directory(String directory) {
        this.directory = directory;
        builder.directory(new java.io.File(directory));
        return this;
    }
    public ProcessLauncher setDirectory(String directory) {
        return directory(directory);
    }

    public ProcessLauncher directory(File file) {
        return directory(file.fullPath());
    }
    public ProcessLauncher setDirectory(File file) {
        return directory(file);
    }

    public ProcessLauncher directory(java.io.File file) {
        this.directory = file.toString();
        builder.directory(file);
        return this;
    }

    public ProcessLauncher setDirectory(java.io.File file) {
        return directory(file);
    }

    public boolean shell() {
        return shell;
    }
    public boolean getShell() {
        return shell();
    }

    public ProcessLauncher shell(boolean shell) {
        this.shell = shell;
        return this;
    }
    public ProcessLauncher setShell(boolean shell) {
        return shell(shell);
    }

    public String shellCommand() {
        if (shell() && (shellCommand == null || shellCommand.isEmpty())) {
            if (SystemUtils.IS_OS_WINDOWS) {
                return "cmd.exe";
            }
            return "sh";
        }
        return shellCommand;
    }
    public String getShellCommand() {
        return shellCommand();
    }

    public ProcessLauncher shellCommand(String shellCommand) {
        this.shellCommand = shellCommand;
        return this;
    }
    public ProcessLauncher setShellCommand(String shellCommand) {
        return shellCommand(shellCommand);
    }

    public String shellParameter() {
        if (shell() && (shellParameter == null || shellParameter.isEmpty())) {
            if (SystemUtils.IS_OS_WINDOWS) {
                if (shellCommand() != null && (shellCommand().equals("cmd.exe") || shellCommand().equals("cmd"))) {
                    return "/c";
                }
                if (shellCommand() != null && (shellCommand().equals("powershell.exe") || shellCommand().equals("powershell"))) {
                    return "-command";
                }
                return shellParameter;
            }
            return "-c";
        }
        return shellParameter;
    }
    public String getShellParameter() {
        return shellParameter();
    }

    public ProcessLauncher shellParameter(String shellParameter) {
        this.shellParameter = shellParameter;
        return this;
    }
    public ProcessLauncher setShellParameter(String shellParameter) {
        return shellParameter(shellParameter);
    }

    public Values env() {
        return env;
    }
    public Values getEnv() {
        return env();
    }

    public ProcessLauncher env(Values env) {
        this.env = env;
        Map<String, String> processEnv = builder.environment();
        for (String key : env.keys()) {
            processEnv.put(key, env().getString(key));
        }
        return this;
    }
    public ProcessLauncher setEnv(Values env) {
        return env(env);
    }

    public java.io.OutputStream outputStream() {
        return outputStream;
    }

    public java.io.OutputStream getOutputStream() {
        return this.outputStream();
    }

    public ProcessLauncher outputStream(java.io.OutputStream out) {
        outputStream = out;
        return this;
    }

    public ProcessLauncher setOutputStream(java.io.OutputStream out) {
        return outputStream(out);
    }

    public ProcessLauncher output(OutputStream out) {
        outputStream = out;
        return this;
    }

    public ProcessLauncher setOutput(OutputStream out) {
        return this.outputStream(out);
    }

    public java.io.OutputStream errorOutputStream() {
        return errorOutputStream;
    }

    public java.io.OutputStream getErrorOutputStream() {
        return this.errorOutputStream();
    }

    public ProcessLauncher errorOutputStream(java.io.OutputStream err) {
        errorOutputStream = err;
        return this;
    }

    public ProcessLauncher setErrorOutputStream(java.io.OutputStream err) {
        return errorOutputStream(err);
    }

    public ProcessLauncher errorOutput(OutputStream err) {
        errorOutputStream = err;
        return this;
    }

    public ProcessLauncher setErrorOutput(OutputStream err) {
        return errorOutput(err);
    }

    public Consumer<String> outputLineConsumer() {
        return outputLineConsumer;
    }

    public Consumer<String> getOutputLineConsumer() {
        return this.outputLineConsumer();
    }

    public ProcessLauncher outputLineConsumer(Consumer<String> consumer) {
        outputLineConsumer = consumer;
        return this;
    }

    public ProcessLauncher setOutputLineConsumer(Consumer<String> consumer) {
        return outputLineConsumer(consumer);
    }

    public Consumer<String> errorOutputLineConsumer() {
        return errorOutputLineConsumer;
    }

    public Consumer<String> getErrorOutputLineConsumer() {
        return this.errorOutputLineConsumer();
    }

    public ProcessLauncher errorOutputLineConsumer(Consumer<String> consumer) {
        errorOutputLineConsumer = consumer;
        return this;
    }

    public ProcessLauncher setErrorOutputLineConsumer(Consumer<String> consumer) {
        return errorOutputLineConsumer(consumer);
    }

    public boolean autoCloseOutputStreams() {
        return this.autoCloseOutputStreams;
    }

    public boolean isAutoCloseOutputStreams() {
        return this.autoCloseOutputStreams();
    }

    public ProcessLauncher autoCloseOutputStreams(boolean autoCloseOutputStreams) {
        this.autoCloseOutputStreams = autoCloseOutputStreams;
        return this;
    }

    public ProcessLauncher setAutoCloseOutputStreams(boolean autoCloseOutputStreams) {
        this.autoCloseOutputStreams(autoCloseOutputStreams);
        return this;
    }

    public long timeLimit() {
        return this.timeLimit;
    }

    public long getTimeLimit() {
        return this.timeLimit();
    }

    public ProcessLauncher timeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
        return this;
    }

    public ProcessLauncher setTimeLimit(long timeLimit) {
        this.timeLimit(timeLimit);
        return this;
    }

    public Consumer<ParallelThread> onParallel() {
        return this.onParallel;
    }

    public Consumer<ParallelThread> getOnParallel() {
        return this.onParallel();
    }

    public ProcessLauncher onParallel(Consumer<ParallelThread> onParallel) {
        this.onParallel = onParallel;
        return this;
    }

    public ProcessLauncher setOnParallel(Consumer<ParallelThread> onParallel) {
        this.onParallel(onParallel);
        return this;
    }

    public Consumer<Result> onFinish() {
        return this.onFinish;
    }

    public Consumer<Result> getOnFinish() {
        return this.onFinish();
    }

    public ProcessLauncher onFinish(Consumer<Result> onFinish) {
        this.onFinish = onFinish;
        return this;
    }

    public ProcessLauncher setOnFinish(Consumer<Result> onFinish) {
        this.onFinish(onFinish);
        return this;
    }

    public Result execute(List<String> command) throws IOException, InterruptedException {
        return execute(command.toArray(new String[command.size()]));
    }

    public Result execute(Values command) throws IOException, InterruptedException {
        if (!command.isList()) {
            return null;
        }
        return execute(command.toArray(new String[command.size()]));
    }

    public Result execute(String... command) throws IOException, InterruptedException {
        ExecutionResources execRes = new ExecutionResources(this);
        Result result = execute(execRes, command);
        execRes.finish();
        return result;
    }

    private Result execute(ExecutionResources execRes, String... command) throws IOException, InterruptedException {
        if (shell()) {
            command = new String[] {shellCommand(), shellParameter(), String.join(" ", command)};
        }
        builder.command(command);
        execRes.process = builder.start();
        execRes.inputStream = null;
        ByteArrayOutputStream baosOutput = null;
        if (isReadOutput()) {
            execRes.inputStream = execRes.process.getInputStream();
            if (outputStream() != null) {
                execRes.inputStreamGobbler = new StreamGobbler(execRes.inputStream, outputStream());
            } else if (outputLineConsumer() != null) {
                execRes.inputStreamGobbler = new StreamGobbler(execRes.inputStream, outputLineConsumer());
            } else {
                baosOutput = new ByteArrayOutputStream();
                execRes.inputStreamGobbler = new StreamGobbler(execRes.inputStream, baosOutput);
            }
            execRes.inputStreamGobbler.start();
        }
        execRes.errorInputStream = null;
        ByteArrayOutputStream baosOutputError = null;
        if (isReadErrorOutput()) {
            execRes.errorInputStream = execRes.process.getErrorStream();
            if (errorOutputStream() != null) {
                execRes.errorInputStreamGobbler = new StreamGobbler(execRes.errorInputStream, errorOutputStream());
            } else if (errorOutputLineConsumer() != null) {
                execRes.errorInputStreamGobbler = new StreamGobbler(execRes.errorInputStream, errorOutputLineConsumer());
            } else {
                execRes.errorInputStreamGobbler = new StreamGobbler(execRes.errorInputStream, baosOutputError);
            }
            execRes.errorInputStreamGobbler.start();
        }
        // Initialize a thread that manages the closing IO and the exit delay.
        execRes.start();
        if (getWaitFor() > 0) {
            while (execRes.process.waitFor(getWaitFor(), TimeUnit.MILLISECONDS)) {
                if (!execRes.process.isAlive()) {
                    break;
                }
            }
        }
        // IO graceful time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) { }
        // Exit Code
        int exitCode = 0;
        if (waitFor() >= 0 && execRes.process.isAlive()) {
            exitCode = execRes.process.waitFor();
        } else {
            exitCode = execRes.process.exitValue();
        }
        // Threads & IOs graceful time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) { }
        // Destroy process and terminate stream threads
        execRes.result = new Result(
                baosOutput != null ? baosOutput.toString() : null,
                baosOutputError != null ? baosOutputError.toString() : null,
                exitCode
        );
        return execRes.result;
    }

    public boolean await() {
        return this.await;
    }

    public boolean getAwait() {
        return this.await();
    }

    public ProcessLauncher await(boolean await) {
        this.await = await;
        return this;
    }

    public ProcessLauncher setAwait(boolean wait) {
        this.await(wait);
        return this;
    }

    public Result executeAsync(List<String> command) throws IOException {
        return executeAsync(command.toArray(new String[command.size()]));
    }

    public Result executeAsync(Values command) throws IOException {
        if (!command.isList()) {
            return null;
        }
        return executeAsync(command.toArray(new String[command.size()]));
    }

    public Result executeAsync(String... command) throws IOException {
        ExecutionResources execRes = new ExecutionResources(this);
        AtomicReference<Result> result = new AtomicReference<>();
        AtomicReference<IOException> ioException = new AtomicReference<>();
        Thread thread = new Thread(() -> {
            try {
                result.set(execute(execRes, command));
            } catch (IOException e) {
                ioException.set(e);
            } catch (InterruptedException e) { }
        });
        thread.setName("Netuno Psamata: Process - Execute Async");
        thread.start();
        if (await()) {
            try {
                thread.join();
            } catch (InterruptedException e) { }
            execRes.finish();
        }
        if (ioException.get() != null) {
            throw ioException.get();
        }
        return result.get();
    }

    public class ParallelThread extends Thread {
        private Consumer<ParallelThread> consumer = null;
        private boolean running = false;

        private ParallelThread(Consumer<ParallelThread> consumer) {
            this.consumer = consumer;
        }

        public void pause(long millis) {
            try {
                sleep(millis);
            } catch (InterruptedException e) { }
        }

        public void run() {
            running = true;
            consumer.accept(this);
        }

        public boolean isRunning() {
            return running;
        }

        private void done() {
            running = false;
        }
    }

    private class ExecutionResources {
        private ProcessLauncher processLauncher;
        private Process process;
        private java.io.InputStream inputStream = null;
        private java.io.InputStream errorInputStream = null;
        private StreamGobbler inputStreamGobbler = null;
        private StreamGobbler errorInputStreamGobbler = null;
        private Thread monitorThread = null;
        private ParallelThread parallelThread = null;
        private Result result = null;

        private ExecutionResources(ProcessLauncher processLauncher) {
            this.processLauncher = processLauncher;
        }

        private void start() {
            long startedTime = System.currentTimeMillis();
            if (processLauncher.onParallel() != null) {
                parallelThread = new ParallelThread(processLauncher.onParallel);
                parallelThread.setName("Netuno Psamata: Process Parallel");
                parallelThread.start();
            }
            monitorThread = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) { }
                    if (!process.isAlive() || (processLauncher.timeLimit() > 0 && System.currentTimeMillis() - startedTime >= processLauncher.timeLimit())) {
                        // IO graceful time
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) { }
                        monitorThread = null;
                        finish();
                        break;
                    }
                }
            });
            monitorThread.setName("Netuno Psamata: Process Monitor");
            monitorThread.start();
        }

        private void finish() {
            if (monitorThread != null) {
                if (processLauncher.onFinish() != null) {
                    processLauncher.onFinish().accept(this.result);
                }
                monitorThread.interrupt();
                monitorThread = null;
            }
            if (parallelThread != null) {
                parallelThread.done();
                parallelThread.interrupt();
                parallelThread = null;
            }
            if (processLauncher.readOutput()) {
                inputStreamGobbler.interrupt();
                if (processLauncher.outputStream() != null && autoCloseOutputStreams()) {
                    try {
                        processLauncher.outputStream().close();
                    } catch (IOException e) { }
                }
            }
            if (processLauncher.readErrorOutput()) {
                errorInputStreamGobbler.interrupt();
                if (processLauncher.errorOutputStream() != null && autoCloseOutputStreams()) {
                    try {
                        processLauncher.errorOutputStream().close();
                    } catch (IOException e) { }
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) { }
                inputStream = null;
            }
            if (errorInputStream != null) {
                try {
                    errorInputStream.close();
                } catch (IOException e) { }
                errorInputStream = null;
            }
            if (process.isAlive()) {
                process.destroy();
            }
            if (process.isAlive()) {
                process.destroyForcibly();
            }
        }
    }

    public class Result {
        public String output = "";
        public String outputError = "";
        public int exitCode = 0;

        public Result(String output, String outputError, int exitCode) {
            this.output = output;
            this.outputError = outputError;
            this.exitCode = exitCode;
        }

        public String output() {
            return output;
        }

        public String getOutput() {
            return output;
        }

        public String outputError() {
            return outputError;
        }

        public String getOutputError() {
            return outputError;
        }

        public int exitCode() {
            return exitCode;
        }

        public int getExitCode() {
            return exitCode;
        }

        @Override
        public String toString() {
            return output;
        }
    }
}
