package org.netuno.tritao.query;
import org.apache.logging.log4j.LogManager;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.DB;
import org.netuno.psamata.Values;
import org.netuno.tritao.db.manager.Data;
import org.netuno.tritao.query.join.*;
import org.netuno.tritao.query.pagination.Page;
import org.netuno.tritao.query.populate.Populate;
import org.netuno.tritao.query.where.Condition;
import org.netuno.tritao.query.where.RelationOperator;
import org.netuno.tritao.query.where.Where;
import org.netuno.tritao.hili.Hili;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryEngine extends Data {

    public QueryEngine(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(QueryEngine.class);

    public String buildQuerySQL(Query query) {
        String joinSQL = "";
        String whereSQL = "";
        if (query.getWhere() != null) {
            whereSQL += "\n\t" + query.getWhere().getFirstCondition().getOperator().toString() + this.buildWhereSQL(query.getWhere());
        }
        for(Map.Entry<String, Join> entryJoin : query.getJoin().entrySet()) {
            final Join join = entryJoin.getValue();
            joinSQL += "\t"+this.buildJoinSQL(join);
            if (join.getWhere() != null) {
                whereSQL += "\n\t" + join.getWhere().getFirstCondition().getOperator().toString() + this.buildWhereSQL(join.getWhere());
            }
            for(Map.Entry<String, Join> entrySubJoin : join.getRelation().getSubRelations().entrySet()) {
                final Join subJoin = entrySubJoin.getValue();
                if (subJoin.getWhere() != null) {
                    whereSQL += "\n\t" + subJoin.getWhere().getFirstCondition().getOperator().toString() + this.buildWhereSQL(subJoin.getWhere());
                }
            }
        }
        whereSQL = "\nWHERE 1 = 1" + whereSQL;
        final String SQL = joinSQL + whereSQL;
        return SQL;
    }

    public String buildJoinSQL(Join join) {
        String joinSQL = "INNER JOIN";
        final Relation relation = join.getRelation();
        joinSQL += this.buildRelation(relation, join.getTable());
        return "\n\t" + joinSQL;
    }

    public String buildRelation(Relation relation, String table) {
        String relationSQL = "";
        relationSQL = relation.getTableName() + " ON ";
        switch (relation.getType()) {
            case ManyToOne -> relationSQL += table+"."+relation.getColumn() + " = " + relation.getTableName()+".id";
            case OneToMany -> relationSQL += relation.getTableName()+"."+relation.getColumn() + " = " + table+".id";
        }
        for(Map.Entry<String, Join> subRelationEntry : relation.getSubRelations().entrySet()) {
            Join join = subRelationEntry.getValue();
            relationSQL += this.buildJoinSQL(join);
        }
        return " " + relationSQL;
    }

    public String buildWhereSQL(Where where) {
        String whereSQL = "";
        final String table = where.getTable();
        final Condition firstCondition = where.getFirstCondition();
        whereSQL += this.buildRelationOperatorSQL(firstCondition.getRelationOperator(), table, firstCondition.getColumn());
        for (Map.Entry<String, Condition> conditionEntry : where.getConditions().entrySet()) {
            Condition condition = conditionEntry.getValue();
            whereSQL += buildCondition(condition, table);
        }
        return whereSQL;
    }

    public String objectToValue(Object object) {
        return switch (object) {
            case String s -> "'" + DB.sqlInjection(s) + "'";
            default -> object.toString();
        };
    }

    public String buildRelationOperatorSQL(RelationOperator relationOperator, String table, String column) {
//        getHili().resource().get(org.netuno.tritao.resource.DB.class).isPostgreSQL();
        return switch (relationOperator.getOperatorType()) {
            case Equals -> " " + table+"."+column + " = " + this.objectToValue(relationOperator.getValue());
            case StartsWith ->
                    " " + "LOWER(" +table+"."+column+ ")" + " LIKE " + "LOWER('"+DB.sqlInjection(relationOperator.getValue().toString())+"%')";
            case EndsWith ->
                    " " + "LOWER(" +table+"."+column+ ")" + " LIKE " + "LOWER('%"+DB.sqlInjection(relationOperator.getValue().toString())+"')";
            case Contains ->
                    " " + "LOWER(" +table+"."+column+ ")" + " LIKE " + "LOWER('%"+DB.sqlInjection(relationOperator.getValue().toString())+"%')";
            case In -> {
                List values = relationOperator.getInValues().list().stream().map(
                        value -> this.objectToValue(value)).collect(Collectors.toList());
                yield  " " + table+"."+column + " IN " + "("+String.join(",", values)+")";
            }
            case GreaterThan -> " " + table+"."+column + " > " + this.objectToValue(relationOperator.getValue());
            case LessThan -> " " + table+"."+column + " < " + this.objectToValue(relationOperator.getValue());
            case GreaterOrEqualsThan -> " " + table+"."+column + " >= " + this.objectToValue(relationOperator.getValue());
            case LessOrEqualsThan -> " " + table+"."+column + " <= " + this.objectToValue(relationOperator.getValue());
            case Different -> " " + table+"."+column + " <> " + this.objectToValue(relationOperator.getValue());
        };
    }

    private String buildCondition(Condition condition, String table) {
        String conditionSQL = "";
        if (condition.hasSubCondition()) {
            conditionSQL += " " + condition.getOperator() + " " + "(";
            conditionSQL += this.buildWhereSQL(condition.getSubCondition().setTable(table));
            conditionSQL += ")";
        } else {
            conditionSQL += " " + condition.getOperator() + " " + this.buildRelationOperatorSQL(condition.getRelationOperator(), table, condition.getColumn());
        }
        return conditionSQL;
    }

    public String buildSelectSQL(Query query) {
        String select = "SELECT \n\t" + query.getTableName()+".*" + " \nFROM ";
        List <String> fields = query.getFields().stream().map(field ->
                field.getElias() != null ? field.getColumn() + " AS " + field.getElias() : field.getColumn()
        ).collect(Collectors.toList());
        if (fields.size() > 0) {
            select = "SELECT \n\t" + String.join(", \n\t", fields) + " \nFROM ";
        }
        if (query.isDistinct()) {
            select = "SELECT DISTINCT\n" + query.getTableName()+".*" + " \nFROM ";
            if (fields.size() > 0) {
                select = "SELECT DISTINCT\n\t" + String.join(", \n\t", fields) + " \nFROM ";
            }
        }
        return select;
    }

    public List<Values> populateResults(List<Values> results, Query query) {
        String querySQLPart = this.buildQuerySQL(query);
        for (int i = 0; i < results.size();i++) {
            for (Populate populate : query.getTablesToPopulate()) {
                List<String> fieldList = populate.getFields().stream().map(
                        field -> field.getElias() != null ? field.getColumn() + " AS " + field.getElias() : field.getColumn())
                        .collect(Collectors.toList());
                String selectSQLPart = "SELECT DISTINCT "
                        + (fieldList.size() > 0 ? String.join(", ", fieldList) : populate.getTable() + ".*")
                        + " FROM " + query.getTableName();
                String commandSQL = selectSQLPart + " " + querySQLPart;
                commandSQL += " AND "
                        + populate.getFilter().getColumn() + " = "
                        + "'" + results.get(i).get((
                                populate.getFilter().getElias() != null
                                        ? populate.getFilter().getElias()
                                        : populate.getFilter().getColumn().split("\\.")[1]
                )).toString() + "'";
                List<Values> items = getManager().query(commandSQL);
                if (items.size() == 0) {
                    results.get(i).set(populate.getTable(), Collections.EMPTY_LIST);
                    continue;
                }
                results.get(i).set(populate.getTable(), items.size() > 1 ? items : items.get(0));
            }
        }
        return results;
    }

    public List<Values> all(Query query) {
        String select = this.buildSelectSQL(query);
        String selectCommandSQL = select + query.getTableName()+ this.buildQuerySQL(query);
        if (query.getGroup() != null) {
            selectCommandSQL += "\nGROUP BY " + query.getGroup().getColumn();
        }
        if (query.getOrder() != null) {
            selectCommandSQL += "\nORDER BY " + query.getOrder().getColumn() + " " + query.getOrder().getOrder();
        }
        if (query.isDebug()) {
            logger.warn("SQL Command executed: \n"+selectCommandSQL);
        }
        List<Values> items = getManager().query(selectCommandSQL);
        if (items.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        return query.getTablesToPopulate().size() > 0 ? populateResults(items, query) : items;
    }
    public Values first(Query query) {
        String select = this.buildSelectSQL(query);
        String selectCommandSQL = select + query.getTableName() + this.buildQuerySQL(query);
        if (query.getGroup() != null) {
            selectCommandSQL += "\nGROUP BY " + query.getGroup().getColumn();
        }
        if (query.getOrder() != null) {
            selectCommandSQL += "\nORDER BY " + query.getOrder().getColumn() + " " + query.getOrder().getOrder();
        }
        selectCommandSQL += "\nLIMIT 1";
        if (query.isDebug()) {
            logger.warn("SQL Command executed: \n"+selectCommandSQL);
        }
        List<Values> items = getManager().query(selectCommandSQL);
        if (items.size() == 0) {
            return new Values();
        }
        return items.get(0);
    }

    public int count(Query query) {
        String select = "SELECT COUNT("+query.getTableName()+".id"+") AS total \nFROM ";
        if (query.isDistinct()) {
            select = "SELECT COUNT(DISTINCT "+query.getTableName()+".id"+") AS total \nFROM ";
        }
        String selectCommandSQL = select + query.getTableName() + this.buildQuerySQL(query);
        if (query.getGroup() != null) {
            selectCommandSQL += "\nGROUP BY " + query.getGroup().getColumn();
        }
        if (query.getOrder() != null) {
            selectCommandSQL += "\nORDER BY " + query.getOrder().getColumn() + " " + query.getOrder().getOrder();
        }

        return (int) getManager().query(selectCommandSQL).get(0).getLong("total");
    }

    public Page page(Query query) {
        String select = this.buildSelectSQL(query);
        String selectCommandSQL = select + query.getTableName() + this.buildQuerySQL(query);
        if (query.getGroup() != null) {
            selectCommandSQL += "\nGROUP BY " + query.getGroup().getColumn();
        }
        if (query.getOrder() != null) {
            selectCommandSQL += "\nORDER BY " + query.getOrder().getColumn() + " " + query.getOrder().getOrder();
        }
        selectCommandSQL += "\nLIMIT "+query.getPagination().getPageSize() +" OFFSET " + query.getPagination().getOffset();
        if (query.isDebug()) {
            logger.warn("SQL Command executed: \n"+selectCommandSQL);
        }
        List<Values> items = getManager().query(selectCommandSQL);
        int total = this.count(query);
        Page page = new Page(items.size() == 0 ? Collections.EMPTY_LIST : items , total, query.getPagination());
        return page;
    }
}
