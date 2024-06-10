package org.netuno.tritao.form;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.db.manager.Data;
import org.netuno.tritao.hili.Hili;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QueryEngine extends Data {

    public QueryEngine(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }

    public String buildQuerySQL(Query query) {
        String linkSQL = "";
        String whereSQL = "";
        for(Map.Entry<String, Where> whereEntry : query.getWhere().entrySet()) {
            whereSQL += this.buildWhereSQL(whereEntry.getValue());
        }
        for(Map.Entry<String, Link> linkEntry : query.getLink().entrySet()) {
            final Link link = linkEntry.getValue();
            linkSQL += this.buildLinkSQL(link);
            if (link.getWhere() != null) {
                whereSQL += " " + link.getWhere().getFirstCondition().getOperator().toString() + this.buildWhereSQL(link.getWhere());
            }
        }
        if (whereSQL.length() > 0) {
            whereSQL = " WHERE" + whereSQL;
        }
        final String SQL = linkSQL + whereSQL;
        return SQL;
    }

    public String buildLinkSQL(Link link) {
        String linkSQL = "INNER JOIN";
        final Relation relation = link.getRelation();
        linkSQL += this.buildRelation(relation, link.getTable());
        return " " + linkSQL;
    }

    public String buildRelation(Relation relation, String table) {
        String relationSQL = "";
        relationSQL = relation.getTableName() + " ON ";
        if (relation.getType().equals(RelationType.ManyToOne)) {
            relationSQL += table+"."+relation.getColumn() + " = " + relation.getTableName()+".id";
        } else if (relation.getType().equals(RelationType.OneToMany)) {
            relationSQL += relation.getTableName()+"."+relation.getColumn() + " = " + table+".id";
        } else if (relation.getType().equals(RelationType.OneToOne)) {
            relationSQL += table+"."+relation.getColumn() + " = " + relation.getTableName()+".id";
        }
        return " " + relationSQL;
    }

    public String buildWhereSQL(Where where) {
        String whereSQL = "";
        final String table = where.getTable();
        final Condition firstCondition = where.getFirstCondition();
        if (firstCondition.getValue() instanceof String) {
            whereSQL += " " + table+"."+firstCondition.getColumn() + " = " + "'"+firstCondition.getValue().toString()+"'";
        } else {
            whereSQL += " " + table+"."+firstCondition.getColumn() + " = " + firstCondition.getValue().toString();
        }
        for (Map.Entry<String, Condition> conditionEntry : where.getConditions().entrySet()) {
            Condition condition = conditionEntry.getValue();
            whereSQL += buildCondition(condition, table);
        }
        return whereSQL;
    }

    private String buildCondition(Condition condition, String table) {
        String conditionSQL = "";
        if (condition.hasSubCondition()) {
            conditionSQL += " " + condition.getOperator() + " " + "(";
            conditionSQL += this.buildWhereSQL(condition.getSubCondition().setTable(table));
            conditionSQL += ")";
        } else {
            if (condition.getValue() instanceof String) {
                conditionSQL += " " + condition.getOperator() + " " + table+"."+condition.getColumn() + " = " + "'" + condition.getValue().toString() + "'";
            } else {
                conditionSQL += " " + condition.getOperator() + " " + table+"."+condition.getColumn() + " = " + condition.getValue().toString();
            }
        }
        return conditionSQL;
    }

    public String buildSelectCommandSQL(Query query) {
        String select = "select " + query.getTableName()+".*" + " from ";
        if (query.getFields().size() > 0) {
            select = "select " + String.join(", ", query.getFields()) + " from ";
        }

        final String SelectCommandSQL = select + query.getTableName() + this.buildQuerySQL(query);
        return SelectCommandSQL;
    }

    public List<Values> all(Query query) {
        final String selectCommandSQL = this.buildSelectCommandSQL(query);
        List<Values> items = getManager().query(selectCommandSQL);
        if (items.size() == 0) {
            return new ArrayList<Values>();
        }
        return items;
    }
}
