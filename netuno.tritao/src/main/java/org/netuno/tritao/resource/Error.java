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

package org.netuno.tritao.resource;

import org.netuno.library.doc.LanguageDoc;
import org.netuno.library.doc.LibraryDoc;
import org.netuno.library.doc.LibraryTranslationDoc;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.resource.util.ErrorException;

/**
 * Error - Resource
 * @author Eduardo Fonseca Velasques - @eduveks
 */
@Resource(name = "error")
@LibraryDoc(translations = {
        @LibraryTranslationDoc(
                language = LanguageDoc.PT,
                title = "Error",
                introduction = "Geração erros gerais da aplicação e categorizar a gravidade do erro com os tipos:\n" +
                        "<ul>\n" +
                        "<li>trace</li>\n" +
                        "<li>debug</li>\n" +
                        "<li>info</li>\n" +
                        "<li>warn</li>\n" +
                        "<li>error</li>\n" +
                        "<li>fatal</li>\n" +
                        "</ul>",
                howToUse = { }
        ),
        @LibraryTranslationDoc(
                language = LanguageDoc.EN,
                title = "Error",
                introduction = "Generating general application errors and categorizing the severity of the error with the types:\n" +
                        "<ul>\n" +
                        "<li>trace</li>\n" +
                        "<li>debug</li>\n" +
                        "<li>info</li>\n" +
                        "<li>warn</li>\n" +
                        "<li>error</li>\n" +
                        "<li>fatal</li>\n" +
                        "</ul>",
                howToUse = { }
        )
})
public class Error extends ResourceBase {

    public Values data = new Values();

    public Error(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public boolean is(Object o) {
        return isError(o) || isException(o) || isThrowable(o);
    }
    
    public boolean isError(Object o) {
    	if (o == null) {
            return false;
        }
    	if (o instanceof java.lang.Error) {
            return true;
        }
    	return false;
    }
    
    public boolean isException(Object o) {
    	if (o == null) {
            return false;
        }
    	if (o instanceof java.lang.Exception) {
            return true;
        }
    	return false;
    }
    
    public boolean isThrowable(Object o) {
    	if (o == null) {
            return false;
        }
    	if (o instanceof java.lang.Throwable) {
            return true;
        }
    	return false;
    }

    public java.lang.Error createError(String message) {
        return new java.lang.Error(message);
    }

    public java.lang.Exception createException(String message) {
        return new java.lang.Exception(message);
    }

    public java.lang.Throwable createThrowable(String message) {
        return new java.lang.Throwable(message);
    }
    
    public void raise(Object o) throws Throwable {
    	if (o instanceof java.lang.Error) {
    		throw (java.lang.Error)o;
    	}
    	if (o instanceof java.lang.Exception) {
    		throw (java.lang.Exception)o;
    	}
    	if (o instanceof java.lang.Throwable) {
    		throw (java.lang.Throwable)o;
    	}
    }

    public Values data() {
        return data;
    }

    public Error data(Values data) {
        this.data = data;
        return this;
    }

    public void trace(String message) {
        throw new ErrorException(message).setLogTrace(true);
    }

    public void trace(String message, Throwable cause) {
        throw new ErrorException(message, cause).setLogTrace(true);
    }

    public void trace(String message, Object cause) {
        throw new ErrorException(message, new Exception(cause.toString())).setLogTrace(true);
    }

    public void debug(String message) {
        throw new ErrorException(message).setLogDebug(true);
    }

    public void debug(String message, Throwable cause) {
        throw new ErrorException(message, cause).setLogDebug(true);
    }

    public void debug(String message, Object cause) {
        throw new ErrorException(message, new Exception(cause.toString())).setLogDebug(true);
    }

    public void info(String message) {
        throw new ErrorException(message).setLogInfo(true);
    }

    public void info(String message, Throwable cause) {
        throw new ErrorException(message, cause).setLogInfo(true);
    }

    public void info(String message, Object cause) {
        throw new ErrorException(message, new Exception(cause.toString())).setLogInfo(true);
    }

    public void warn(String message) {
        throw new ErrorException(message).setLogWarn(true);
    }

    public void warn(String message, Throwable cause) {
        throw new ErrorException(message, cause).setLogWarn(true);
    }

    public void warn(String message, Object cause) {
        throw new ErrorException(message, new Exception(cause.toString())).setLogWarn(true);
    }

    public void error(String message) {
        throw new ErrorException(message).setLogError(true);
    }

    public void error(String message, Throwable cause) {
        throw new ErrorException(message, cause).setLogError(true);
    }

    public void error(String message, Object cause) {
        throw new ErrorException(message, new Exception(cause.toString())).setLogError(true);
    }

    public void fatal(String message) {
        throw new ErrorException(message).setLogFatal(true);
    }

    public void fatal(String message, Throwable cause) {
        throw new ErrorException(message, cause).setLogFatal(true);
    }

    public void fatal(String message, Object cause) {
        throw new ErrorException(message, new Exception(cause.toString())).setLogFatal(true);
    }
}
