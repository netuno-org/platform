package org.netuno.psamata.io;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileManager implements AutoCloseable {
    private static Logger logger = LogManager.getLogger(FileManager.class);

    private static Map<String, FileManager> managers = Collections.synchronizedMap(new HashMap<>());

    private class Stream {
        private File file;
        private Closeable closeable;
        private Stream(File file, Closeable closeable) {
            this.file = file;
            this.closeable = closeable;
        }
        public File getFile() {
            return file;
        }
        public Closeable getCloseable() {
            return closeable;
        }
    }

    private List<Stream> streams = new ArrayList<>();

    private FileManager() {

    }

    public OutputStream onOutput(File file, OutputStream output) {
        streams.add(new Stream(file, output));
        return output;
    };

    public java.io.OutputStream onOutputStream(File file, java.io.OutputStream output) {
        streams.add(new Stream(file, output));
        return output;
    };

    public InputStream onInput(File file, InputStream input) {
        streams.add(new Stream(file, input));
        return input;
    }

    public java.io.InputStream onInputStream(File file, java.io.InputStream input) {
        streams.add(new Stream(file, input));
        return input;
    };

    public java.io.Reader onReader(File file, java.io.Reader reader) {
        streams.add(new Stream(file, reader));
        return reader;
    }

    @Override
    public void close() throws Exception {
        for (Stream stream : streams) {
            try {
                stream.getCloseable().close();
            } catch (IOException e) {
                logger.trace("Closing stream of the file: "+ stream.getFile().getFullPath(), e);
            }
        }
        streams.clear();
    }

    public static synchronized FileManager get() {
        String threadName = Thread.currentThread().getName();
        if (managers.containsKey(threadName)) {
            return managers.get(threadName);
        }
        FileManager fileManager = new FileManager();
        managers.put(threadName, fileManager);
        return fileManager;
    }

    public static synchronized void clear() {
        String threadName = Thread.currentThread().getName();
        if (managers.containsKey(threadName)) {
            try {
                get().close();
            } catch (Exception e) {
                logger.trace("Cleaning file manager for the thread: "+ threadName, e);
            }
            managers.remove(threadName);
        }
    }
}
