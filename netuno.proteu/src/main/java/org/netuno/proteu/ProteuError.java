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

package org.netuno.proteu;

/**
 * Errors with log type supported.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ProteuError extends Error {
    private static final long serialVersionUID = 1L;

    private boolean logTrace = false;
    private boolean logDebug = false;
    private boolean logInfo = false;
    private boolean logWarn = false;
    private boolean logError = false;
    private boolean logFatal = false;
    private String logTraceMessage = "";
    private String logDebugMessage = "";
    private String logInfoMessage = "";
    private String logWarnMessage = "";
    private String logErrorMessage = "";
    private String logFatalMessage = "";
    private boolean appendError = false;

    public ProteuError() {
        super();
    }

    public ProteuError(Throwable t) {
        super(t);
    }

    public ProteuError(final String text, final Throwable ex) {
        super(text, ex);
    }

    public ProteuError(final Exception ex) {
        super(ex);
    }

    public ProteuError(final String text) {
        super(text);
    }
    
    private String chainOfCausesMessages(Throwable t) {
        if (t == null) {
            return "";
        }
        String cause = "\n#     > "+ (t.getMessage() != null ? t.getMessage() : t.toString());
        return cause + chainOfCausesMessages(t.getCause());
    }
    
    public String getLogMessage() {
        String message = getMessage();
        Throwable throwable = getCause();
        while (throwable != null) {
            if (throwable instanceof ProteuError) {
                message += ((ProteuError)throwable).getLogMessage() + "\n";
            }
            if (throwable.getCause() != null) {
                throwable = throwable.getCause();
            }
            break;
        }
        if (throwable != null) {
            if (!message.endsWith("\n")) {
                message += "\n";
            }
            message += "# Causes\n";
            message += chainOfCausesMessages(throwable);
            message += "#\n";
        }
        if (logTrace && !logTraceMessage.isEmpty()) {
            message = "\n# TRACE: "+ logTraceMessage +"\n" + message;
        }
        if (logInfo && !logInfoMessage.isEmpty()) {
            message = "\n# INFO: "+ logInfoMessage +"\n" + message;
        }
        if (logWarn && !logWarnMessage.isEmpty()) {
            message = "\n# WARN: "+ logWarnMessage +"\n" + message;
        }
        if (logError && !logErrorMessage.isEmpty()) {
            message = "\n# ERROR: "+ logErrorMessage +"\n" + message;
        }
        if (logFatal && !logFatalMessage.isEmpty()) {
            message = "\n# FATAL: "+ logFatalMessage +"\n" + message;
        }
        return message;
    }

    public boolean isLogTrace() {
        return logTrace;
    }

    public ProteuError setLogTrace(boolean logTrace) {
        this.logTrace = logTrace;
        return this;
    }

    public boolean isLogDebug() {
        return logDebug;
    }

    public ProteuError setLogDebug(boolean logDebug) {
        this.logDebug = logDebug;
        return this;
    }

    public boolean isLogInfo() {
        return logInfo;
    }

    public ProteuError setLogInfo(boolean logInfo) {
        this.logInfo = logInfo;
        return this;
    }

    public boolean isLogWarn() {
        return logWarn;
    }

    public ProteuError setLogWarn(boolean logWarn) {
        this.logWarn = logWarn;
        return this;
    }

    public boolean isLogError() {
        return logError;
    }

    public ProteuError setLogError(boolean logError) {
        this.logError = logError;
        return this;
    }

    public boolean isLogFatal() {
        return logFatal;
    }

    public ProteuError setLogFatal(boolean logFatal) {
        this.logFatal = logFatal;
        return this;
    }

    public String getLogTrace() {
        return logTraceMessage;
    }

    public ProteuError setLogTrace(String logTrace) {
        this.logTrace = true;
        this.logTraceMessage = logTrace;
        return this;
    }

    public String getLogDebug() {
        return logDebugMessage;
    }

    public ProteuError setLogDebug(String logDebug) {
        this.logDebug = true;
        this.logDebugMessage = logDebug;
        return this;
    }

    public String getLogInfo() {
        return logInfoMessage;
    }

    public ProteuError setLogInfo(String logInfo) {
        this.logInfo = true;
        this.logInfoMessage = logInfo;
        return this;
    }

    public String getLogWarn() {
        return logWarnMessage;
    }

    public ProteuError setLogWarn(String logWarn) {
        this.logWarn = true;
        this.logWarnMessage = logWarn;
        return this;
    }

    public String getLogError() {
        return logErrorMessage;
    }

    public ProteuError setLogError(String logError) {
        this.logError = true;
        this.logErrorMessage = logError;
        return this;
    }

    public String getLogFatal() {
        return logFatalMessage;
    }

    public ProteuError setLogFatal(String logFatal) {
        this.logFatal = true;
        this.logFatalMessage = logFatal;
        return this;
    }

    public boolean isAppendError() {
        return appendError;
    }

    public ProteuError setAppendError(boolean appendError) {
        this.appendError = appendError;
        return this;
    }
}
