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

package org.netuno.tritao.resource.util;

import org.netuno.proteu.ProteuError;
import org.netuno.tritao.resource.Resource;

/**
 * File System Path Utilities
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class ResourceException extends ProteuError {

    public ResourceException() {
        super();
    }

    public ResourceException(String message) {
        super(message.replace("\n", "\n# "));
    }

    public ResourceException(String message, Throwable cause) {
        super(cause != null && cause.getMessage() != null ? 
                message +":\n#   "+ cause.getMessage().replace("\n", "\n#     ")
                : message, cause);
    }

    public static String message(Class resourceClass, String message, Throwable cause) {
        Resource resource = (Resource)resourceClass.getAnnotation(Resource.class);
            return "\n" +
                "\n#" +
                "\n# "+ resource.name().toUpperCase() +
                "\n#" +
                "\n# " + message +
                "\n#" +
                "\n# " + cause.getMessage() +
                "\n#" +
                "\n"
            ;
    }
}
