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
 * Page of the HTTP Erro 404
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Error404 {
    /**
     * Page Constructor
     * @param proteu Proteu
     */
    public Error404(Proteu proteu) throws java.io.IOException {
        if (Config.getError404().equals("")) {
        	proteu.getOutput().println("<html>");
        	proteu.getOutput().println("<head>");
        	proteu.getOutput().println("  <title>404 Not Found</title>");
        	proteu.getOutput().println("</head>");
        	proteu.getOutput().println("<body>");
        	proteu.getOutput().println("  <h2>Not Found</h2><p>The requested URL <b>"+ proteu.getRequestHeader().getString("Url") +"</b> was not found on this server.</p>");
        	proteu.getOutput().println("</body>");
        	proteu.getOutput().println("</html>");
        } else {
        	proteu.redirect(Config.getError404());
        }
    }
}
