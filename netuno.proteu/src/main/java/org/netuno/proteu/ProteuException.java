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
 * Proteu exception.
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ProteuException extends Exception {
	private static final long serialVersionUID = 1L;

    private String logTrace = "";
    private String logDebug = "";
    private String logInfo = "";
    private String logWarn = "";
    private String logError = "";
    private String logFatal = "";

	/**
     * New exception.
     * @param text Exception message
     * @param ex Exception
     */
    public ProteuException(final String text, final Throwable ex) {
        super(text, ex);
    }
    /**
     * New exception.
     * @param ex Exception
     */
    public ProteuException(final Exception ex) {
        super(ex);
    }
    /**
     * New exception.
     * @param text Exception message
     */
    public ProteuException(final String text) {
        super(text);
    }

    public String getLogTrace() {
        return logTrace;
    }

    public ProteuException setLogTrace(String logTrace) {
        this.logTrace = logTrace;
        return this;
    }

    public String getLogDebug() {
        return logDebug;
    }

    public ProteuException setLogDebug(String logDebug) {
        this.logDebug = logDebug;
        return this;
    }

    public String getLogInfo() {
        return logInfo;
    }

    public ProteuException setLogInfo(String logInfo) {
        this.logInfo = logInfo;
        return this;
    }

    public String getLogWarn() {
        return logWarn;
    }

    public ProteuException setLogWarn(String logWarn) {
        this.logWarn = logWarn;
        return this;
    }

    public String getLogError() {
        return logError;
    }

    public ProteuException setLogError(String logError) {
        this.logError = logError;
        return this;
    }

    public String getLogFatal() {
        return logFatal;
    }

    public ProteuException setLogFatal(String logFatal) {
        this.logFatal = logFatal;
        return this;
    }
}
