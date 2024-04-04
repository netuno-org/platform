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

package org.netuno.tritao.util.hibernate;

import java.sql.SQLException;
import org.hibernate.SessionFactory;
import java.sql.Connection;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.netuno.proteu.Proteu;
import org.netuno.tritao.config.Config;
import org.netuno.tritao.hili.Hili;

/**
 * Hibernate Management
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Management<T> extends Pagination {
    protected SessionFactory sessionFactory;
    protected Connection connection;
    protected Session session;
    protected Proteu proteu;
    protected Hili hili;

    public Management(Proteu proteu, Hili hili) {
        this.proteu = proteu;
        this.hili = hili;
        sessionFactory = (SessionFactory)proteu.getConfig().get("Hibernate-SessionFactory");
        connection = (Connection)proteu.getConfig().get("Hibernate-Connection");
        session = (Session)proteu.getConfig().get("Hibernate-Session");
    }

    @SuppressWarnings("rawtypes")
	public static void loadDataBase(Proteu proteu, Hili hili, String db) throws SQLException {
        proteu.getConfig().set("Hibernate-Database", db);
        Management management = new Management(proteu, hili);
        management.close();
        management.open();
    }

    public void insert(T obj) {
        StatelessSession insertSession = sessionFactory.openStatelessSession(connection);
        try {
            insertSession.beginTransaction();
            insertSession.insert(obj);
            insertSession.getTransaction().commit();
        } catch (Throwable t) {
        	insertSession.getTransaction().rollback();
            throw new Error(t);
        } finally {
            insertSession.close();
        }
    }

    public void update(T obj) {
        StatelessSession updateSession = sessionFactory.openStatelessSession(connection);
        try {
            updateSession.beginTransaction();
            updateSession.update(obj);
            updateSession.getTransaction().commit();
        } catch (Throwable t) {
        	updateSession.getTransaction().rollback();
            throw new Error(t);
        } finally {
            updateSession.close();
        }
    }

    public void delete(T obj) {
        StatelessSession deleteSession = sessionFactory.openStatelessSession(connection);
        try {
            deleteSession.beginTransaction();
            deleteSession.delete(obj);
            deleteSession.getTransaction().commit();
        } catch (Throwable t) {
        	deleteSession.getTransaction().rollback();
            throw new Error(t);
        } finally {
            deleteSession.close();
        }
    }

    public void saveOrUpdate(T obj) {
        Session saveOrUpdateSession = sessionFactory.openSession();
        try {
            saveOrUpdateSession.beginTransaction();
            saveOrUpdateSession.persist(obj);
            saveOrUpdateSession.getTransaction().commit();
            saveOrUpdateSession.flush();
        } catch (Throwable t) {
        	saveOrUpdateSession.getTransaction().rollback();
            throw new Error(t);
        } finally {
            saveOrUpdateSession.close();
        }
    }
    
    public void open() throws SQLException {
    	close();
        connection = Config.getDataBaseManager(proteu).getConnection();
        proteu.getConfig().set("Hibernate-Connection", connection);
        openSession();
    }
    
    public void close() throws SQLException {
        try {
            closeSession();
        } finally {
            try {
                if (connection != null) {
                    if (!connection.isClosed()) {
                        connection.close();
                        connection = null;
                    }
                }
            } finally {
                Config.getDataBaseManager(proteu).closeConnections();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public Proteu getProteu() {
        return proteu;
    }

    public void closeSession() {
        if (getSession() != null && getSession().isOpen()) {
            getSession().close();
            session = null;
        }
    }
    
    public void openSession() {
    	closeSession();
    	session = ((SessionFactory)proteu.getConfig().get("Hibernate-SessionFactory")).openSession();
        proteu.getConfig().set("Hibernate-Session", session);
    }

    public Session getSession() {
        return session;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
