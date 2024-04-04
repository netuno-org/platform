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

import java.util.List;
import java.util.Map;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.hibernate.CacheMode;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.Search;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.AsciiUtils;
import org.netuno.tritao.hili.Hili;

/**
 * Hibernate Indexer
 * @author Eduardo Fonseca Velasques - @eduveks
 */
public class Indexer {
    @SuppressWarnings("rawtypes")
	private Management mng = null;

    public int searchResultsSize = 0;

    @SuppressWarnings("rawtypes")
	public Indexer(Proteu proteu, Hili hili) {
        mng = new Management(proteu, hili);
    }

    public static String textOptimize(String text) {
        return AsciiUtils.convertNonAscii(text)
                .replace('.', ' ').replace('-', ' ')
                .replace('\\', ' ').replace('/', ' ')
                .replace('!', ' ').replace('?', ' ')
                .replace('\'', ' ').replace('"', ' ')
                .replace('|', ' ').replace('@', ' ')
                .replace('#', ' ').replace('$', ' ')
                .replace('%', ' ').replace('&', ' ')
                .replace('(', ' ').replace(')', ' ')
                .replace('{', ' ').replace('}', ' ')
                .replace('[', ' ').replace(']', ' ')
                .replace('=', ' ').replace(':', ' ')
                .replace(',', ' ').replace(';', ' ')
                .replace('+', ' ').replace('*', ' ')
                .replace('<', ' ').replace('>', ' ')
                .replace('_', ' ');
    }

    public void startDefaultMassIndexer(Class<?> c) throws InterruptedException {
        getMassIndexer(c).startAndWait();
    }

    public MassIndexer getMassIndexer(Class<?> c) throws InterruptedException {
        return Search.getFullTextSession(mng.getSession()).createIndexer(c)
            .batchSizeToLoadObjects(25)
            .cacheMode(CacheMode.NORMAL)
            .threadsToLoadObjects(5)
            .threadsToLoadObjects(searchResultsSize);
    }

    public void indexAll(@SuppressWarnings("rawtypes") List list) {
        FullTextSession fullTextSession = Search.getFullTextSession(mng.getSession());
        Transaction tx = fullTextSession.beginTransaction();
        for (Object i : list) {
            fullTextSession.index(i);
        }
        tx.commit();
        fullTextSession.clear();
    }

    public void index(Object obj) {
        FullTextSession fullTextSession = Search.getFullTextSession(mng.getSession());
        Transaction tx = fullTextSession.beginTransaction();
        fullTextSession.index(obj);
        tx.commit();
        fullTextSession.clear();
    }
    
    public void removeAll(@SuppressWarnings("rawtypes") List list) {
        FullTextSession fullTextSession = Search.getFullTextSession(mng.getSession());
        Transaction tx = fullTextSession.beginTransaction();
        for (Object i : list) {            
            fullTextSession.index(i);
        }
        tx.commit();
        fullTextSession.clear();
    }

    public void remove(Object obj) {
        FullTextSession fullTextSession = Search.getFullTextSession(mng.getSession());
        Transaction tx = fullTextSession.beginTransaction();
        fullTextSession.remove(obj);
        tx.commit();
        fullTextSession.clear();
    }

    @SuppressWarnings("rawtypes")
	public List searchMultiField(String keyword, String[] fields, Class c) throws ParseException {
        FullTextSession fullTextSession = Search.getFullTextSession(mng.getSession());
        Transaction tx = fullTextSession.beginTransaction();
        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
        Query query = parser.parse(keyword);
        org.hibernate.query.Query hibQuery = fullTextSession.createFullTextQuery(query, c);
        List result = hibQuery.list();
        tx.commit();
        fullTextSession.clear();
        return result;
    }

    @SuppressWarnings("rawtypes")
	public List search(String keyword, String[] fields, Map<String, String> fieldAndValues, int page, int pageSize, Class c) {
        int first = 0;
        if(page <= 0) page = 1;
        first = (page - 1) * pageSize;
        Sort sort = new Sort(new SortField("date", SortField.Type.LONG, true));

        FullTextSession fullTextSession = Search.getFullTextSession(mng.getSession());
        Transaction tx = fullTextSession.beginTransaction();

        BooleanQuery.Builder query = new BooleanQuery.Builder();
        
        for(String s : fieldAndValues.keySet()) {
            query.add(new TermQuery(new Term(s, fieldAndValues.get(s))), BooleanClause.Occur.MUST);
        }

        String[] vKeywords = keyword.split(" ");

        for(String s : vKeywords) {
            for(String field : fields){
                query.add(new PrefixQuery(new Term(field, s.toLowerCase())), BooleanClause.Occur.MUST);
            }
        }

        org.hibernate.search.FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(query.build(), c);
        fullTextQuery.setSort(sort);
        fullTextQuery.setFirstResult(first);
        fullTextQuery.setMaxResults(pageSize);

        List result = fullTextQuery.list();

        this.searchResultsSize = fullTextQuery.getResultSize();

        tx.commit();
        fullTextSession.clear();
        return result;
    
    }
    @SuppressWarnings("rawtypes")
	public List wildCardSearch(String keyword, String[] fields, Map<String, String> fieldAndValues, int page, int pageSize, int maxClauseCount, Class c) throws ParseException {
        int first = 0;
        if(page <= 0) page = 1;
        first = (page - 1) * pageSize;
        Sort sort = new Sort(new SortField("date", SortField.Type.LONG, true));

        FullTextSession fullTextSession = Search.getFullTextSession(mng.getSession());
        Transaction tx = fullTextSession.beginTransaction();

        BooleanQuery.Builder query = new BooleanQuery.Builder();

        for(String s : fieldAndValues.keySet()) {
            query.add(new TermQuery(new Term(s, fieldAndValues.get(s))), BooleanClause.Occur.MUST);
        }

        keyword = keyword.trim();
        String[] vKeywords = keyword.split(" ");

        if(!keyword.equals("")) {
            for(String s : vKeywords) {
                for(String field : fields){
                    QueryParser parser = new QueryParser(field, fullTextSession.getSearchFactory().getAnalyzer( c ));
                    parser.setAllowLeadingWildcard(true);
                    query.add(parser.parse(parser.parse(QueryParser.escape(s)).toString().replace(field + ":", "*") + "*"), BooleanClause.Occur.MUST);
                    //query.add(new WildcardQuery(new Term(field, "*" + QueryParser.escape(s.toLowerCase()) + "*")), BooleanClause.Occur.MUST);
                }
            }
        }
        query.setMinimumNumberShouldMatch(maxClauseCount);
        
        System.out.println("Query -  " + query.toString());

        org.hibernate.search.FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(query.build(), c);
                
        fullTextQuery.setSort(sort);
        fullTextQuery.setFirstResult(first);
        fullTextQuery.setMaxResults(pageSize);

        List result = fullTextQuery.list();

        this.searchResultsSize = fullTextQuery.getResultSize();

        tx.commit();
        fullTextSession.clear();
        return result;
    }
    @SuppressWarnings("rawtypes")
	public List exactTermSearch(String keyword, String[] fields, Map<String, String> fieldAndValues, int page, int pageSize, int maxClauseCount, Class c) throws ParseException {
        int first = 0;
        if(page <= 0) page = 1;
        first = (page - 1) * pageSize;
        Sort sort = new Sort(new SortField("date", SortField.Type.LONG, true));

        FullTextSession fullTextSession = Search.getFullTextSession(mng.getSession());
        Transaction tx = fullTextSession.beginTransaction();

        BooleanQuery.Builder query = new BooleanQuery.Builder();

        for(String s : fieldAndValues.keySet()) {
            query.add(new TermQuery(new Term(s, fieldAndValues.get(s))), BooleanClause.Occur.MUST);
        }

        keyword = keyword.trim();
        String[] vKeywords = keyword.split(" ");

            for(String s : vKeywords) {
                for(String field : fields){
                    query.add(new TermQuery(new Term(field, QueryParser.escape(s.toLowerCase()))), BooleanClause.Occur.MUST);
                    //query.add(parser.parse(parser.parse(QueryParser.escape(s)).toString().replace(field + ":", "*") + "*"), BooleanClause.Occur.MUST);
                    //query.add(new WildcardQuery(new Term(field, "*" + QueryParser.escape(s.toLowerCase()) + "*")), BooleanClause.Occur.MUST);
                }
            }        

        query.setMinimumNumberShouldMatch(maxClauseCount);

        System.out.println("Query -  " + query.toString());
        org.hibernate.search.FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(query.build(), c);

        fullTextQuery.setSort(sort);
        fullTextQuery.setFirstResult(first);
        fullTextQuery.setMaxResults(pageSize);

        List result = fullTextQuery.list();

        this.searchResultsSize = fullTextQuery.getResultSize();

        tx.commit();
        fullTextSession.clear();
        return result;
    }
    @SuppressWarnings("rawtypes")
	public List prefixSearch(String keyword, String[] fields, Map<String, String> fieldAndValues, int page, int pageSize, int maxClauseCount, Class c, SortField[] sortFields) throws ParseException {
        int first = 0;
        if(page <= 0) page = 1;
        first = (page - 1) * pageSize;
        Sort sort = new Sort(sortFields);

        FullTextSession fullTextSession = Search.getFullTextSession(mng.getSession());
        Transaction tx = fullTextSession.beginTransaction();

        BooleanQuery.Builder query = new BooleanQuery.Builder();

        for(String s : fieldAndValues.keySet()) {
            query.add(new TermQuery(new Term(s, fieldAndValues.get(s))), BooleanClause.Occur.MUST);
        }

        keyword = keyword.trim();
        String[] vKeywords = keyword.split(" ");

            for(String s : vKeywords) {
                for(String field : fields){
                    query.add(new PrefixQuery(new Term(field, QueryParser.escape(s.toLowerCase()))), BooleanClause.Occur.MUST);
                    //query.add(parser.parse(parser.parse(QueryParser.escape(s)).toString().replace(field + ":", "*") + "*"), BooleanClause.Occur.MUST);
                    //query.add(new WildcardQuery(new Term(field, "*" + QueryParser.escape(s.toLowerCase()) + "*")), BooleanClause.Occur.MUST);
                }
            }

        query.setMinimumNumberShouldMatch(maxClauseCount);
        System.out.println("Query -  " + query.toString());
        org.hibernate.search.FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(query.build(), c);

        fullTextQuery.setSort(sort);
        fullTextQuery.setFirstResult(first);
        fullTextQuery.setMaxResults(pageSize);

        List result = fullTextQuery.list();

        this.searchResultsSize = fullTextQuery.getResultSize();

        tx.commit();
        fullTextSession.clear();
        return result;
    }
}
