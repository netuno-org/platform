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

 package org.netuno.psamata.io;
 
 /**
  * Required methods to supports .
  * @author Eduardo Fonseca Velasques - @eduveks
  */
public interface IO {

    InputStream input();

    InputStream getInput();

    java.io.InputStream inputStream();

    java.io.InputStream getInputStream();

    OutputStream output();

    OutputStream getOutput();

    java.io.OutputStream outputStream();

    java.io.OutputStream getOutputStream();

}