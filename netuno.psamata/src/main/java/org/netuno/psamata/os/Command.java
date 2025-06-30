package org.netuno.psamata.os;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.SystemUtils;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.File;
import org.netuno.psamata.io.InputStream;
import org.netuno.psamata.io.OutputStream;
import org.netuno.psamata.io.StreamGobbler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class Command {

    public String directory = ".";
    public boolean shell = true;
    public String shellCommand = "";
    public String shellParameter = "";
    public Values env = new Values();

    public boolean readOutput = true;
    public boolean readErrorOutput = true;
    public long waitFor = 100;

    public boolean redirectErrorStream = false;

    public java.io.OutputStream outputStream = null;
    public java.io.OutputStream errorOutputStream = null;

    public boolean outputAutoClose = true;
    public boolean errorOutputAutoClose = true;

    public long exitDelay = 0;

    public boolean await = true;

    private ProcessBuilder builder = new ProcessBuilder();

    public Command() {

    }

    public boolean readOutput() {
        return isReadOutput();
    }

    public boolean isReadOutput() {
        return readOutput;
    }

    public Command readOutput(boolean readOutput) {
        setReadOutput(readOutput);
        return this;
    }

    public Command setReadOutput(boolean readOutput) {
        this.readOutput = readOutput;
        return this;
    }

    public boolean readErrorOutput() {
        return isReadErrorOutput();
    }

    public boolean isReadErrorOutput() {
        return readErrorOutput;
    }

    public Command readErrorOutput(boolean readErrorOutput) {
        setReadErrorOutput(readErrorOutput);
        return this;
    }

    public Command setReadErrorOutput(boolean readErrorOutput) {
        this.readErrorOutput = readErrorOutput;
        return this;
    }

    public boolean redirectErrorStream() {
        return isRedirectErrorStream();
    }

    public boolean isRedirectErrorStream() {
        return redirectErrorStream;
    }

    public Command redirectErrorStream(boolean redirectErrorStream) {
        setRedirectErrorStream(redirectErrorStream);
        return this;
    }

    public Command setRedirectErrorStream(boolean redirectErrorStream) {
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

    public Command waitFor(long waitFor) {
        setWaitFor(waitFor);
        return this;
    }

    public Command setWaitFor(long waitFor) {
        this.waitFor = waitFor;
        return this;
    }

    public String directory() {
        return this.directory;
    }
    public String getDirectory() {
        return this.directory;
    }

    public Command directory(String directory) {
        this.directory = directory;
        builder.directory(new java.io.File(directory));
        return this;
    }
    public Command setDirectory(String directory) {
        return directory(directory);
    }

    public Command directory(File file) {
        return directory(file.fullPath());
    }
    public Command setDirectory(File file) {
        return directory(file);
    }

    public boolean shell() {
        return shell;
    }
    public boolean getShell() {
        return shell();
    }

    public Command shell(boolean shell) {
        this.shell = shell;
        return this;
    }
    public Command setShell(boolean shell) {
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

    public Command shellCommand(String shellCommand) {
        this.shellCommand = shellCommand;
        return this;
    }
    public Command setShellCommand(String shellCommand) {
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

    public Command shellParameter(String shellParameter) {
        this.shellParameter = shellParameter;
        return this;
    }
    public Command setShellParameter(String shellParameter) {
        return shellParameter(shellParameter);
    }

    public Values env() {
        return env;
    }
    public Values getEnv() {
        return env();
    }

    public Command env(Values env) {
        this.env = env;
        Map<String, String> processEnv = builder.environment();
        for (String key : env.keys()) {
            processEnv.put(key, env().getString(key));
        }
        return this;
    }
    public Command setEnv(Values env) {
        return env(env);
    }

    public java.io.OutputStream outputStream() {
        return outputStream;
    }

    public java.io.OutputStream getOutputStream() {
        return this.outputStream();
    }

    public Command outputStream(java.io.OutputStream out) {
        outputStream = out;
        return this;
    }

    public Command setOutputStream(java.io.OutputStream out) {
        return outputStream(out);
    }

    public Command output(OutputStream out) {
        outputStream = out;
        return this;
    }

    public Command setOutput(OutputStream out) {
        return this.outputStream(out);
    }

    public java.io.OutputStream errorOutputStream() {
        return errorOutputStream;
    }

    public java.io.OutputStream getErrorOutputStream() {
        return this.errorOutputStream();
    }

    public Command errorOutputStream(java.io.OutputStream err) {
        errorOutputStream = err;
        return this;
    }

    public Command setErrorOutputStream(java.io.OutputStream err) {
        return errorOutputStream(err);
    }

    public Command errorOutput(OutputStream err) {
        errorOutputStream = err;
        return this;
    }

    public Command setErrorOutput(OutputStream err) {
        return errorOutput(err);
    }

    public boolean outputAutoClose() {
        return this.outputAutoClose;
    }

    public boolean isOutputAutoClose() {
        return this.outputAutoClose();
    }

    public Command outputAutoClose(boolean outputAutoClose) {
        this.outputAutoClose = outputAutoClose;
        return this;
    }

    public Command setOutputAutoClose(boolean outputAutoClose) {
        this.outputAutoClose(outputAutoClose);
        return this;
    }

    public boolean errorOutputAutoClose() {
        return this.errorOutputAutoClose;
    }

    public boolean isErrorOutputAutoClose() {
        return this.errorOutputAutoClose();
    }

    public Command errorOutputAutoClose(boolean errorOutputAutoClose) {
        this.errorOutputAutoClose = errorOutputAutoClose;
        return this;
    }

    public Command setErrorOutputAutoClose(boolean errorOutputAutoClose) {
        this.errorOutputAutoClose(errorOutputAutoClose);
        return this;
    }

    public long exitDelay() {
        return this.exitDelay;
    }

    public long getExitDelay() {
        return this.exitDelay();
    }

    public Command exitDelay(long exitDelay) {
        this.exitDelay = exitDelay;
        return this;
    }

    public Command setExitDelay(long exitDelay) {
        this.exitDelay(exitDelay);
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
        execRes.terminate();
        return result;
    }

    private Result execute(ExecutionResources execRes, String... command) throws IOException, InterruptedException {
        if (shell()) {
            command = new String[] {shellCommand(), shellParameter(), String.join(" ", command)};
        }
        builder.command(command);
        execRes.process = builder.start();
        String input = "";
        String error = "";
        execRes.inputStream = null;
        if (isReadOutput()) {
            execRes.inputStream = execRes.process.getInputStream();
            if (outputStream() != null) {
                execRes.inExecutorService = Executors.newSingleThreadExecutor();
                execRes.inExecutorService.submit(new StreamGobbler(execRes.inputStream, outputStream()));
            }
        }
        execRes.errorInputStream = null;
        if (isReadErrorOutput()) {
            execRes.errorInputStream = execRes.process.getErrorStream();
            if (errorOutputStream() != null) {
                execRes.errorExecutorService = Executors.newSingleThreadExecutor();
                execRes.errorExecutorService.submit(new StreamGobbler(execRes.errorInputStream, errorOutputStream()));
            }
        }
        // Initialize a thread that manages the closing IO and the exit delay.
        execRes.start();
        if (getWaitFor() > 0) {
            while (execRes.process.waitFor(getWaitFor(), TimeUnit.MILLISECONDS)) {
                if (isReadOutput() && outputStream() == null) {
                    input += InputStream.readAll(execRes.inputStream);
                }
                if (isReadErrorOutput() && errorOutputStream() == null) {
                    error += InputStream.readAll(execRes.errorInputStream);
                }
                if (!execRes.process.isAlive()) {
                    break;
                }
            }
        }
        if (isReadOutput() && outputStream() == null && execRes.inputStream.available() > 0) {
            input += InputStream.readAll(execRes.inputStream);
        }
        if (isReadErrorOutput() && errorOutputStream() == null && execRes.errorInputStream.available() > 0) {
            error += InputStream.readAll(execRes.errorInputStream);
        }
        // IO graceful time
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) { }
        // Exit Code
        int exitCode = 0;
        if (waitFor() >= 0 && execRes.process.isAlive()) {
            exitCode = execRes.process.waitFor();
        } else {
            exitCode = execRes.process.exitValue();
        }
        // Destroy process and terminate stream threads
        execRes.terminate();
        return new Result(input, error, exitCode);
    }

    public boolean await() {
        return this.await;
    }

    public boolean getAwait() {
        return this.await();
    }

    public Command await(boolean await) {
        this.await = await;
        return this;
    }

    public Command setAwait(boolean wait) {
        this.await(wait);
        return this;
    }

    public Result executeAsync(List<String> command) throws IOException, InterruptedException {
        return executeAsync(command.toArray(new String[command.size()]));
    }

    public Result executeAsync(Values command) throws IOException, InterruptedException {
        if (!command.isList()) {
            return null;
        }
        return executeAsync(command.toArray(new String[command.size()]));
    }

    public Result executeAsync(String... command) throws IOException, InterruptedException {
        ExecutionResources execRes = new ExecutionResources(this);
        AtomicReference<Result> result = new AtomicReference<>();
        AtomicReference<IOException> ioException = new AtomicReference<>();
        AtomicReference<InterruptedException> interruptedException = new AtomicReference<>();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                result.set(execute(execRes, command));
            } catch (IOException e) {
                ioException.set(e);
            } catch (InterruptedException e) {
                interruptedException.set(e);
            }
        });
        new Thread(() -> {
            try {
                if (exitDelay() > 0 && !executorService.awaitTermination(exitDelay(), TimeUnit.MILLISECONDS)) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) { }
                    execRes.terminate();
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) { }
        }).start();
        if (await()) {
            while (!execRes.started) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) { }
            }
            while (!execRes.started || execRes.process.isAlive()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) { }
            }
            execRes.terminate();
            executorService.shutdownNow();
        }
        if (ioException.get() != null) {
            throw ioException.get();
        }
        if (interruptedException.get() != null) {
            throw interruptedException.get();
        }
        return result.get();
    }

    private class ExecutionResources {
        private Command command;
        private Process process;
        private boolean started = false;
        private java.io.InputStream inputStream;
        private java.io.InputStream errorInputStream;
        private ExecutorService inExecutorService;
        private ExecutorService errorExecutorService = null;

        private ExecutionResources(Command command) {
            this.command = command;
        }

        private void start() {
            started = true;
            long startedTime = System.currentTimeMillis();
            new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) { }
                    if (!process.isAlive()) {
                        closeInputs();
                        break;
                    }
                    if (command.exitDelay() > 0 && System.currentTimeMillis() - startedTime >= command.exitDelay()) {
                        closeInputs();
                        terminate();
                        break;
                    }
                }
            }).start();
        }

        private void closeInputs() {
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
        }

        private void terminate() {
            if (process.isAlive()) {
                process.destroy();
            }
            if (process.isAlive()) {
                process.destroyForcibly();
            }
            if (command.outputStream() != null) {
                inExecutorService.shutdownNow();
                if (outputAutoClose()) {
                    try {
                        command.outputStream().close();
                    } catch (IOException e) { }
                }
            }
            if (command.errorOutputStream() != null) {
                errorExecutorService.shutdownNow();
                if (errorOutputAutoClose()) {
                    try {
                        command.errorOutputStream().close();
                    } catch (IOException e) { }
                }
            }
        }
    }

    public class Result {
        public String output = "";
        public String error = "";
        public int exitCode = 0;
        public Result(String output, String error, int exitCode) {
            this.output = output;
            this.error = error;
            this.exitCode = exitCode;
        }

        public String output() {
            return output;
        }

        public String getOutput() {
            return output;
        }

        public String error() {
            return error;
        }

        public String getError() {
            return error;
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
