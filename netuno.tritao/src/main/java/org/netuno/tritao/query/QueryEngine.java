package org.netuno.tritao.query;
import org.apache.logging.log4j.LogManager;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.DB;
import org.netuno.psamata.Values;
import org.netuno.tritao.db.manager.Data;
import org.netuno.tritao.query.join.*;
import org.netuno.tritao.query.pagination.Page;
import org.netuno.tritao.query.where.Condition;
import org.netuno.tritao.query.where.RelationOperator;
import org.netuno.tritao.query.where.RelationOperatorType;
import org.netuno.tritao.query.where.Where;
import org.netuno.tritao.hili.Hili;

import java.util.ArrayList;
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
        if (relation.getType().equals(RelationType.ManyToOne)) {
            relationSQL += table+"."+relation.getColumn() + " = " + relation.getTableName()+".id";
        } else if (relation.getType().equals(RelationType.OneToMany)) {
            relationSQL += relation.getTableName()+"."+relation.getColumn() + " = " + table+".id";
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
        if (object instanceof String) {
            return "'"+ DB.sqlInjection(object.toString()) +"'";
        } else {
            return object.toString();
        }
    }

    public String buildRelationOperatorSQL(RelationOperator relationOperator, String table, String column) {
        String relationOperatorSQL = "";
        if (relationOperator.getOperatorType().equals(RelationOperatorType.Equals)) {
            relationOperatorSQL = " " + table+"."+column + " = " + this.objectToValue(relationOperator.getValue());
        } else if (relationOperator.getOperatorType().equals(RelationOperatorType.StartsWith)) {
            relationOperatorSQL = " " + "LOWER(" +table+"."+column+ ")" + " LIKE " + "LOWER('"+DB.sqlInjection(relationOperator.getValue().toString())+"%')";
        } else if (relationOperator.getOperatorType().equals(RelationOperatorType.EndsWith)) {
            relationOperatorSQL = " " + "LOWER(" +table+"."+column+ ")" + " LIKE " + "LOWER('%"+DB.sqlInjection(relationOperator.getValue().toString())+"')";
        } else if (relationOperator.getOperatorType().equals(RelationOperatorType.Contains)) {
            relationOperatorSQL = " " + "LOWER(" +table+"."+column+ ")" + " LIKE " + "LOWER('%"+DB.sqlInjection(relationOperator.getValue().toString())+"%')";
        } else if (relationOperator.getOperatorType().equals(RelationOperatorType.In)) {
           List values = relationOperator.getInValues().list().stream().map(value -> this.objectToValue(value)).collect(Collectors.toList());
            relationOperatorSQL = " " + table+"."+column + " IN " + "("+String.join(",", values)+")";
        } else if (relationOperator.getOperatorType().equals(RelationOperatorType.GreaterThan)) {
            relationOperatorSQL = " " + table+"."+column + " > " + this.objectToValue(relationOperator.getValue());
        } else if (relationOperator.getOperatorType().equals(RelationOperatorType.LessThan)) {
            relationOperatorSQL = " " + table+"."+column + " < " + this.objectToValue(relationOperator.getValue());
        } else if (relationOperator.getOperatorType().equals(RelationOperatorType.GreaterOrEqualsThan)) {
            relationOperatorSQL = " " + table+"."+column + " >= " + this.objectToValue(relationOperator.getValue());
        } else if (relationOperator.getOperatorType().equals(RelationOperatorType.LessOrEqualsThan)) {
            relationOperatorSQL = " " + table+"."+column + " <= " + this.objectToValue(relationOperator.getValue());
        } else if (relationOperator.getOperatorType().equals(RelationOperatorType.Different)) {
            relationOperatorSQL = " " + table+"."+column + " <> " + this.objectToValue(relationOperator.getValue());
        }
        return relationOperatorSQL;
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

    public List<Values> all(Query query) {
        String select = "SELECT \n" + query.getTableName()+".*" + " \nFROM ";
        if (query.getFields().size() > 0) {
            select = "SELECT \n\t" + String.join(", \n\t", query.getFields()) + " \nFROM ";
        }
        if (query.isDistinct()) {
            select = "SELECT DISTINCT\n" + query.getTableName()+".*" + " \nFROM ";
            if (query.getFields().size() > 0) {
                select = "SELECT DISTINCT\n\t" + String.join(", \n\t", query.getFields()) + " \nFROM ";
            }
        }
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
            return new ArrayList<Values>();
        }
        return items;
    }
    public Values first(Query query) {
        String select = "SELECT \n" + query.getTableName()+".*" + " \nFROM ";
        if (query.getFields().size() > 0) {
            select = "SELECT \n\t" + String.join(", \n\t", query.getFields()) + " \nFROM ";
        }
        if (query.isDistinct()) {
            select = "SELECT DISTINCT\n" + query.getTableName()+".*" + " \nFROM ";
            if (query.getFields().size() > 0) {
                select = "SELECT DISTINCT\n\t" + String.join(", \n\t", query.getFields()) + " \nFROM ";
            }
        }
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
        String select = "SELECT \n" + query.getTableName()+".*" + " \nFROM ";
        if (query.getFields().size() > 0) {
            select = "SELECT \n\t" + String.join(", \n\t", query.getFields()) + " \nFROM ";
        }
        if (query.isDistinct()) {
            select = "SELECT DISTINCT\n" + query.getTableName()+".*" + " \nFROM ";
            if (query.getFields().size() > 0) {
                select = "SELECT DISTINCT\n\t" + String.join(", \n\t", query.getFields()) + " \nFROM ";
            }
        }
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
        Page page = new Page(items, total, query.getPagination());
        return page;
    }
}
