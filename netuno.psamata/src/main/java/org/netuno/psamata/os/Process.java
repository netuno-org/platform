package org.netuno.psamata.os;

import org.apache.commons.lang3.SystemUtils;
import org.netuno.psamata.Values;
import org.netuno.psamata.io.File;
import org.netuno.psamata.io.InputStream;
import org.netuno.psamata.io.OutputStream;
import org.netuno.psamata.io.StreamGobbler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Process {

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

    public long timeLimit = 0;

    public boolean await = true;

    private ProcessBuilder builder = new ProcessBuilder();

    public Process() {

    }

    public boolean readOutput() {
        return isReadOutput();
    }

    public boolean isReadOutput() {
        return readOutput;
    }

    public Process readOutput(boolean readOutput) {
        setReadOutput(readOutput);
        return this;
    }

    public Process setReadOutput(boolean readOutput) {
        this.readOutput = readOutput;
        return this;
    }

    public boolean readErrorOutput() {
        return isReadErrorOutput();
    }

    public boolean isReadErrorOutput() {
        return readErrorOutput;
    }

    public Process readErrorOutput(boolean readErrorOutput) {
        setReadErrorOutput(readErrorOutput);
        return this;
    }

    public Process setReadErrorOutput(boolean readErrorOutput) {
        this.readErrorOutput = readErrorOutput;
        return this;
    }

    public boolean redirectErrorStream() {
        return isRedirectErrorStream();
    }

    public boolean isRedirectErrorStream() {
        return redirectErrorStream;
    }

    public Process redirectErrorStream(boolean redirectErrorStream) {
        readErrorOutput(false);
        setRedirectErrorStream(redirectErrorStream);
        return this;
    }

    public Process setRedirectErrorStream(boolean redirectErrorStream) {
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

    public Process waitFor(long waitFor) {
        setWaitFor(waitFor);
        return this;
    }

    public Process setWaitFor(long waitFor) {
        this.waitFor = waitFor;
        return this;
    }

    public String directory() {
        return this.directory;
    }
    public String getDirectory() {
        return this.directory;
    }

    public Process directory(String directory) {
        this.directory = directory;
        builder.directory(new java.io.File(directory));
        return this;
    }
    public Process setDirectory(String directory) {
        return directory(directory);
    }

    public Process directory(File file) {
        return directory(file.fullPath());
    }
    public Process setDirectory(File file) {
        return directory(file);
    }

    public boolean shell() {
        return shell;
    }
    public boolean getShell() {
        return shell();
    }

    public Process shell(boolean shell) {
        this.shell = shell;
        return this;
    }
    public Process setShell(boolean shell) {
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

    public Process shellCommand(String shellCommand) {
        this.shellCommand = shellCommand;
        return this;
    }
    public Process setShellCommand(String shellCommand) {
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

    public Process shellParameter(String shellParameter) {
        this.shellParameter = shellParameter;
        return this;
    }
    public Process setShellParameter(String shellParameter) {
        return shellParameter(shellParameter);
    }

    public Values env() {
        return env;
    }
    public Values getEnv() {
        return env();
    }

    public Process env(Values env) {
        this.env = env;
        Map<String, String> processEnv = builder.environment();
        for (String key : env.keys()) {
            processEnv.put(key, env().getString(key));
        }
        return this;
    }
    public Process setEnv(Values env) {
        return env(env);
    }

    public java.io.OutputStream outputStream() {
        return outputStream;
    }

    public java.io.OutputStream getOutputStream() {
        return this.outputStream();
    }

    public Process outputStream(java.io.OutputStream out) {
        outputStream = out;
        return this;
    }

    public Process setOutputStream(java.io.OutputStream out) {
        return outputStream(out);
    }

    public Process output(OutputStream out) {
        outputStream = out;
        return this;
    }

    public Process setOutput(OutputStream out) {
        return this.outputStream(out);
    }

    public java.io.OutputStream errorOutputStream() {
        return errorOutputStream;
    }

    public java.io.OutputStream getErrorOutputStream() {
        return this.errorOutputStream();
    }

    public Process errorOutputStream(java.io.OutputStream err) {
        errorOutputStream = err;
        return this;
    }

    public Process setErrorOutputStream(java.io.OutputStream err) {
        return errorOutputStream(err);
    }

    public Process errorOutput(OutputStream err) {
        errorOutputStream = err;
        return this;
    }

    public Process setErrorOutput(OutputStream err) {
        return errorOutput(err);
    }

    public boolean outputAutoClose() {
        return this.outputAutoClose;
    }

    public boolean isOutputAutoClose() {
        return this.outputAutoClose();
    }

    public Process outputAutoClose(boolean outputAutoClose) {
        this.outputAutoClose = outputAutoClose;
        return this;
    }

    public Process setOutputAutoClose(boolean outputAutoClose) {
        this.outputAutoClose(outputAutoClose);
        return this;
    }

    public boolean errorOutputAutoClose() {
        return this.errorOutputAutoClose;
    }

    public boolean isErrorOutputAutoClose() {
        return this.errorOutputAutoClose();
    }

    public Process errorOutputAutoClose(boolean errorOutputAutoClose) {
        this.errorOutputAutoClose = errorOutputAutoClose;
        return this;
    }

    public Process setErrorOutputAutoClose(boolean errorOutputAutoClose) {
        this.errorOutputAutoClose(errorOutputAutoClose);
        return this;
    }

    public long timeLimit() {
        return this.timeLimit;
    }

    public long getTimeLimit() {
        return this.timeLimit();
    }

    public Process timeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
        return this;
    }

    public Process setTimeLimit(long timeLimit) {
        this.timeLimit(timeLimit);
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
        execRes.jProcess = builder.start();
        String input = "";
        String error = "";
        execRes.inputStream = null;
        if (isReadOutput()) {
            execRes.inputStream = execRes.jProcess.getInputStream();
            if (outputStream() != null) {
                execRes.inputStreamGobbler = new StreamGobbler(execRes.inputStream, outputStream());
                execRes.inputStreamGobbler.start();
            }
        }
        execRes.errorInputStream = null;
        if (isReadErrorOutput()) {
            execRes.errorInputStream = execRes.jProcess.getErrorStream();
            if (errorOutputStream() != null) {
                execRes.errorInputStreamGobbler = new StreamGobbler(execRes.errorInputStream, errorOutputStream());
                execRes.errorInputStreamGobbler.start();
            }
        }
        // Initialize a thread that manages the closing IO and the exit delay.
        execRes.start();
        if (getWaitFor() > 0) {
            while (execRes.jProcess.waitFor(getWaitFor(), TimeUnit.MILLISECONDS)) {
                if (isReadOutput() && outputStream() == null && execRes.inputStream != null
                        && execRes.inputStream.available() > 0) {
                    input += InputStream.readAll(execRes.inputStream);
                }
                if (isReadErrorOutput() && errorOutputStream() == null && execRes.errorInputStream != null
                        && execRes.errorInputStream.available() > 0) {
                    error += InputStream.readAll(execRes.errorInputStream);
                }
                if (!execRes.jProcess.isAlive()) {
                    break;
                }
            }
        }
        if (isReadOutput() && outputStream() == null && execRes.inputStream != null
                && execRes.inputStream.available() > 0) {
            input += InputStream.readAll(execRes.inputStream);
        }
        if (isReadErrorOutput() && errorOutputStream() == null && execRes.errorInputStream != null
                && execRes.errorInputStream.available() > 0) {
            error += InputStream.readAll(execRes.errorInputStream);
        }
        // IO graceful time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) { }
        // Exit Code
        int exitCode = 0;
        if (waitFor() >= 0 && execRes.jProcess.isAlive()) {
            exitCode = execRes.jProcess.waitFor();
        } else {
            exitCode = execRes.jProcess.exitValue();
        }
        // IO graceful time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) { }
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

    public Process await(boolean await) {
        this.await = await;
        return this;
    }

    public Process setAwait(boolean wait) {
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
            execRes.terminate();
        }
        if (ioException.get() != null) {
            throw ioException.get();
        }
        return result.get();
    }

    private class ExecutionResources {
        private Process process;
        private java.lang.Process jProcess;
        private java.io.InputStream inputStream = null;
        private java.io.InputStream errorInputStream = null;
        private StreamGobbler inputStreamGobbler = null;
        private StreamGobbler errorInputStreamGobbler = null;
        private Thread thread = null;

        private ExecutionResources(Process process) {
            this.process = process;
        }

        private void start() {
            long startedTime = System.currentTimeMillis();
            thread = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) { }
                    if (!jProcess.isAlive() || (process.timeLimit() > 0 && System.currentTimeMillis() - startedTime >= process.timeLimit())) {
                        thread = null;
                        terminate();
                        break;
                    }
                }
            });
            thread.setName("Netuno Psamata: Process - Force Termination");
            thread.start();
        }

        private void terminate() {
            if (thread != null) {
                thread.interrupt();
                thread = null;
            }
            if (process.outputStream() != null) {
                inputStreamGobbler.interrupt();
                if (outputAutoClose()) {
                    try {
                        process.outputStream().close();
                    } catch (IOException e) { }
                }
            }
            if (process.errorOutputStream() != null) {
                errorInputStreamGobbler.interrupt();
                if (errorOutputAutoClose()) {
                    try {
                        process.errorOutputStream().close();
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
            if (jProcess.isAlive()) {
                jProcess.destroy();
            }
            if (jProcess.isAlive()) {
                jProcess.destroyForcibly();
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
