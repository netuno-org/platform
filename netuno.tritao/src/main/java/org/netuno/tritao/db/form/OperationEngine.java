package org.netuno.tritao.db.form;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.netuno.proteu.Proteu;
import org.netuno.psamata.DB;
import org.netuno.psamata.Values;
import org.netuno.tritao.db.DataItem;
import org.netuno.tritao.db.form.join.Join;
import org.netuno.tritao.db.form.join.Relationship;
import org.netuno.tritao.db.manager.Data;
import org.netuno.tritao.db.form.pagination.Page;
import org.netuno.tritao.db.form.populate.Populate;
import org.netuno.tritao.db.form.where.ConditionalOperator;
import org.netuno.tritao.db.form.where.RelationalOperator;
import org.netuno.tritao.db.form.where.Where;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.util.ResourceException;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Operation Engine - Engine of SQL queries and all db form operations.
 * Responsible for building db commands from the Operation object
 * @author Jailton de Araujo Santos - @jailtonaraujo
 */
public class OperationEngine extends Data {

    public OperationEngine(Proteu proteu, Hili hili) {
        super(proteu, hili);
    }
    private static final Logger logger = LogManager.getLogger(OperationEngine.class);

    public String buildQuerySQL(Operation query) {
        StringBuilder joinSQL = new StringBuilder();
        StringBuilder whereSQL = new StringBuilder();
        if (query.getWhere() != null && !query.getWhere().getConditions().isEmpty()) {
            final ConditionalOperator firstConditional = query.getWhere().getConditions().getFirst();
            whereSQL.append("\n").append("\t")
                    .append(firstConditional.getOperator() != null ? "" : " AND")
                    .append(this.buildWhereSQL(query.getWhere()));
        }
        for(Map.Entry<String, Join> entryJoin : query.getJoin().entrySet()) {
            final Join join = entryJoin.getValue();
            joinSQL.append("\t").append("\t").append(this.buildJoinSQL(join));
            if (join.getWhere() != null && !join.getWhere().getConditions().isEmpty()) {
                final ConditionalOperator firstConditional = join.getWhere().getConditions().getFirst();
                whereSQL.append("\n").append("\t")
                        .append(firstConditional.getOperator() != null ? "" : " AND")
                        .append(this.buildWhereSQL(join.getWhere()));
            }
            for(Map.Entry<String, Join> entrySubJoin : join.getRelation().getSubRelations().entrySet()) {
                final Join subJoin = entrySubJoin.getValue();
                if (subJoin.getWhere() != null && !subJoin.getWhere().getConditions().isEmpty()) {
                    final ConditionalOperator firstConditional = subJoin.getWhere().getConditions().getFirst();
                    whereSQL.append("\n").append("\t")
                            .append(firstConditional.getOperator() != null ? "" : " AND")
                            .append(this.buildWhereSQL(subJoin.getWhere()));
                }
            }
        }
        whereSQL.insert(0, "\nWHERE 1 = 1");
        return joinSQL.toString() + whereSQL;
    }

    public String buildJoinSQL(Join join) {
        String joinSQL = switch (join.getJoinType()) {
            case INNER_JOIN -> "INNER JOIN";
            case LEFT_JOIN -> "LEFT JOIN";
            case RIGHT_JOIN -> "RIGHT JOIN";
        };
        final Relationship relation = join.getRelation();
        joinSQL += this.buildRelation(relation, join.getTable());
        return "\n\t" + joinSQL;
    }

    public String buildRelation(Relationship relation, String table) {
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
        StringBuilder whereSQL = new StringBuilder();
        final String table = where.getTable();
        for (ConditionalOperator conditionalOperator : where.getConditions()) {
            whereSQL.append(buildCondition(conditionalOperator, table));
        }
        return whereSQL.toString();
    }

    private String buildCondition(ConditionalOperator condition, String table) {
        String conditionSQL = "";
        if (condition.hasSubCondition()) {
            conditionSQL += " " + condition.getOperator() + " (";
            conditionSQL += this.buildWhereSQL(condition.getSubCondition().setTable(table));
            conditionSQL += ")";
        } else {
            conditionSQL += " " + (condition.getOperator() != null ? condition.getOperator() : "")
                    +  this.buildRelationOperatorSQL(condition.getRelationOperator(), table, condition.getColumn());
        }
        return conditionSQL;
    }

    public String objectToValue(Object object) {
        return switch (object) {
            case String string -> "'" + DB.sqlInjection(string) + "'";
            case UUID uuid -> "'" + uuid + "'";
            case Number number -> number.toString();
            case Timestamp timestamp -> "'" + timestamp + "'";
            case Time time -> "'" + time + "'";
            case LocalDateTime localDateTime -> "'" + Timestamp.valueOf(localDateTime) + "'";
            case LocalDate localDate -> "'" + Date.valueOf(localDate) + "'";
            case LocalTime localTime -> "'" + Time.valueOf(localTime) + "'";
            default -> object.toString();
        };
    }

    public String buildRelationOperatorSQL(RelationalOperator relationOperator, String table, String column) {
//        getHili().resource().get(org.netuno.tritao.resource.DB.class).isPostgreSQL();
        return switch (relationOperator.getOperatorType()) {
            case Equals -> " " + table+"."+column + " = " + this.objectToValue(relationOperator.getValue());
            case NotEquals -> " " + table+"."+column + " != " + this.objectToValue(relationOperator.getValue());
            case StartsWith ->
                    " " + "LOWER(" +table+"."+column+ ")" + " LIKE " + "LOWER('"+DB.sqlInjection(relationOperator.getValue().toString())+"%')";
            case EndsWith ->
                    " " + "LOWER(" +table+"."+column+ ")" + " LIKE " + "LOWER('%"+DB.sqlInjection(relationOperator.getValue().toString())+"')";
            case Contains ->
                    " " + "LOWER(" +table+"."+column+ ")" + " LIKE " + "LOWER('%"+DB.sqlInjection(relationOperator.getValue().toString())+"%')";
            case In -> {
                List<String> values = null;
                if (relationOperator.getValue() instanceof Values) {
                    values = ((Values) relationOperator.getValue()).list().stream().map(
                            this::objectToValue).collect(Collectors.toList());
                } else if (relationOperator.getValue() instanceof List) {
                    values = ((List<Object>)relationOperator.getValue()).stream().map(
                            this::objectToValue).collect(Collectors.toList());
                }
                yield  " " + table+"."+column + " IN " + "("+String.join(",", values)+")";
            }
            case GreaterThan -> " " + table+"."+column + " > " + this.objectToValue(relationOperator.getValue());
            case LessThan -> " " + table+"."+column + " < " + this.objectToValue(relationOperator.getValue());
            case GreaterOrEqualsThan -> " " + table+"."+column + " >= " + this.objectToValue(relationOperator.getValue());
            case LessOrEqualsThan -> " " + table+"."+column + " <= " + this.objectToValue(relationOperator.getValue());
            case Different -> " " + table+"."+column + " <> " + this.objectToValue(relationOperator.getValue());
            case InRaw -> {
                String columnName = " " + table+"."+column;
                yield relationOperator.getValue().toString().replaceAll("\\?", columnName);
            }
            case NotIn -> {
                List<String> values = null;
                if (relationOperator.getValue() instanceof Values) {
                    values = ((Values) relationOperator.getValue()).list().stream().map(
                            this::objectToValue).collect(Collectors.toList());
                } else if (relationOperator.getValue() instanceof List) {
                    values = ((List<Object>)relationOperator.getValue()).stream().map(
                            this::objectToValue).collect(Collectors.toList());
                }
                yield  " " + table+"."+column + " NOT IN " + "("+String.join(",", values)+")";
            }
        };
    }

    public String buildSelectSQL(Operation query) {
        String select = "SELECT \n\t" + query.getFormName()+".*" + " \nFROM ";
        List <String> fields = query.getFieldsToGet().stream().map(field ->
                field.getAlias() != null ? field.getColumn() + " AS " + field.getAlias() : field.getColumn()
        ).collect(Collectors.toList());
        if (!fields.isEmpty()) {
            select = "SELECT \n\t" + String.join(", \n\t", fields) + " \nFROM ";
        }
        if (query.isDistinct()) {
            select = "SELECT DISTINCT\n" + query.getFormName()+".*" + " \nFROM ";
            if (!fields.isEmpty()) {
                select = "SELECT DISTINCT\n\t" + String.join(", \n\t", fields) + " \nFROM ";
            }
        }
        return select;
    }

    public List<Values> populateResults(List<Values> results, Operation query) {
        String querySQLPart = this.buildQuerySQL(query);
        for (int i = 0; i < results.size();i++) {
            for (Populate populate : query.getTablesToPopulate()) {
                List<String> fieldList = populate.getFields().stream().map(
                        field -> field.getAlias() != null ? field.getColumn() + " AS " + field.getAlias() : field.getColumn())
                        .collect(Collectors.toList());
                String selectSQLPart = "SELECT DISTINCT "
                        + (!fieldList.isEmpty() ? String.join(", ", fieldList) : populate.getTable() + ".*")
                        + " FROM " + query.getFormName();
                String commandSQL = selectSQLPart + " " + querySQLPart;
                commandSQL += " AND "
                        + populate.getFilter().getColumn() + " = "
                        + "'" + results.get(i).get((
                                populate.getFilter().getAlias() != null
                                        ? populate.getFilter().getAlias()
                                        : populate.getFilter().getColumn().split("\\.")[1]
                )).toString() + "'";
                List<Values> items = getExecutor().query(commandSQL);
                if (items.isEmpty()) {
                    results.get(i).set(populate.getTable(), Collections.EMPTY_LIST);
                    continue;
                }
                results.get(i).set(populate.getTable(), items.size() > 1 ? items : items.get(0));
            }
        }
        return results;
    }

    public List<Values> all(Operation query) {
        String select = this.buildSelectSQL(query);
        String selectCommandSQL = select + query.getFormName()+ this.buildQuerySQL(query);
        if (query.getGroup() != null) {
            selectCommandSQL += "\nGROUP BY " + query.getGroup().getColumn();
        }
        if (query.getOrder() != null) {
            selectCommandSQL += "\nORDER BY " + query.getOrder().getColumn() + " " + query.getOrder().getOrder();
        }
        selectCommandSQL += "\nLIMIT " + query.getLimit();
        if (query.isDebug()) {
            logger.warn("SQL Command executed:\n {}", selectCommandSQL);
        }
        List<Values> items = getExecutor().query(selectCommandSQL);
        if (items.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        return !query.getTablesToPopulate().isEmpty() ? populateResults(items, query) : items;
    }

    public Values first(Operation query) {
        String select = this.buildSelectSQL(query);
        String selectCommandSQL = select + query.getFormName() + this.buildQuerySQL(query);
        if (query.getGroup() != null) {
            selectCommandSQL += "\nGROUP BY " + query.getGroup().getColumn();
        }
        if (query.getOrder() != null) {
            selectCommandSQL += "\nORDER BY " + query.getOrder().getColumn() + " " + query.getOrder().getOrder();
        }
        selectCommandSQL += "\nLIMIT 1";
        if (query.isDebug()) {
            logger.warn("SQL Command executed:\n {}",selectCommandSQL);
        }
        List<Values> items = getExecutor().query(selectCommandSQL);
        if (items.isEmpty()) {
            return null;
        }
        items = !query.getTablesToPopulate().isEmpty() ? populateResults(items, query) : items;
        return items.getFirst();
    }

    public int count(Operation query) {
        final var pagination = query.getPagination();
        String select = "SELECT COUNT("+query.getFormName()+".id"+") AS total \nFROM ";
        if (pagination.getDistinct() != null && !pagination.getDistinct().isBlank()) {
            select = "SELECT COUNT(DISTINCT " + DB.sqlInjection(pagination.getDistinct()) + ") AS total \nFROM ";
        } else if (query.isDistinct()) {
            select = "SELECT COUNT(DISTINCT " + query.getFormName() + ".id" + ") AS total \nFROM ";
        }
        String selectCommandSQL = select + query.getFormName() + this.buildQuerySQL(query);
        if (pagination.isUseGroup() && query.getGroup() != null) {
            selectCommandSQL += "\nGROUP BY " + query.getGroup().getColumn();
        }
        if (query.isDebug()) {
            logger.warn("SQL Command executed:\n {}",selectCommandSQL);
        }
        var dbTotal = getExecutor().query(selectCommandSQL);

        return !dbTotal.isEmpty() ? dbTotal.getFirst().getInt("total") : 0;
    }

    public Page page(Operation query) {
        String select = this.buildSelectSQL(query);
        String selectCommandSQL = select + query.getFormName() + this.buildQuerySQL(query);
        if (query.getGroup() != null) {
            selectCommandSQL += "\nGROUP BY " + query.getGroup().getColumn();
        }
        if (query.getOrder() != null) {
            selectCommandSQL += "\nORDER BY " + query.getOrder().getColumn() + " " + query.getOrder().getOrder();
        }
        selectCommandSQL += "\nLIMIT "+query.getPagination().getPageSize() +" OFFSET " + query.getPagination().getOffset();
        if (query.isDebug()) {
            logger.warn("SQL Command executed:\n {}",selectCommandSQL);
        }
        List<Values> items = getExecutor().query(selectCommandSQL);
        items = !query.getTablesToPopulate().isEmpty() ? populateResults(items, query) : items;
        int total = this.count(query);
        return new Page(items.isEmpty() ? Collections.EMPTY_LIST : items , total, query.getPagination());
    }

    public List<Values> getRecordIDs(Operation query) {
        String selectIdsCommandSQL = "SELECT " + query.getFormName() + ".id FROM " + query.getFormName() + this.buildQuerySQL(query);
        if (query.getOrder() != null) {
            selectIdsCommandSQL += "\nORDER BY " + query.getOrder().getColumn() + " " + query.getOrder().getOrder();
        }
        selectIdsCommandSQL += "\nLIMIT " + query.getLimit();
        List<Values> recordIDs = getExecutor().query(selectIdsCommandSQL);
        if (recordIDs.isEmpty()) {
            throw new ResourceException("Not found records with query:\n"+selectIdsCommandSQL);
        }
        return recordIDs;
    }

    public Values deleteAll(Operation query) {
        List<Values> recordIDs = getRecordIDs(query);
        int numberOfAffectedRecords = 0;
        List<String> undeletedRecords = new ArrayList<>();
        for (Values recordID : recordIDs) {
            final DataItem dataItem = getBuilder().delete(query.getFormName(), recordID.getString("id"));
            if (dataItem.getStatusType() == DataItem.StatusType.Ok) {
                numberOfAffectedRecords++;
            } else {
                undeletedRecords.add(recordID.getString("id"));
            }
        }
        if (!undeletedRecords.isEmpty() && query.isDebug()) {
            logger.warn("Impossible to delete the following records IDs: [{}]", String.join(", ", undeletedRecords));
        }
        if (query.isDebug()) {
            logger.warn("Number of affected records: " + numberOfAffectedRecords);
        }
        return Values.newMap().set(query.getFormName(), numberOfAffectedRecords);
    }

    public Values cascadeDelete(Values deleteLinks, Operation query) {
        List<Values> recordIDs = getRecordIDs(query);
        Values affectedForms = new Values();
        Values undeletedRecords = new Values();
        for (Values recordID : recordIDs) {
            for (Map.Entry<String, Object> deleteLink: deleteLinks.entrySet()) {
                String selectDeleteLinkIdsCommandSQL = "SELECT id FROM " + deleteLink.getKey() + " WHERE " + deleteLink.getValue() + " = " + recordID.get("id");
                List<Values> deleteLinkIds = getExecutor().query(selectDeleteLinkIdsCommandSQL);
                for (Values deleteLinkId: deleteLinkIds) {
                    DataItem dataItem = getBuilder().delete(deleteLink.getKey(), deleteLinkId.getString("id"));
                    if (dataItem.getStatusType() == DataItem.StatusType.Ok) {
                        affectedForms.set(deleteLink.getKey(), affectedForms.getInt(deleteLink.getKey(), 0) + 1);
                    } else {
                        undeletedRecords.set(deleteLink.getKey(), undeletedRecords.getInt(deleteLink.getKey(), 0) + 1);
                    }
                }
            }
            DataItem dataItem = getBuilder().delete(query.getFormName(), recordID.getString("id"));
            if (dataItem.getStatusType() == DataItem.StatusType.Ok) {
                affectedForms.set(
                        query.getFormName(),
                        (affectedForms.getInt(query.getFormName(), 0) + 1)
                );
            } else {
                undeletedRecords.set(
                        query.getFormName(),
                        (undeletedRecords.getInt(query.getFormName(), 0) + 1)
                );
            }
        }
        if (query.isDebug()) {
            if (!undeletedRecords.isEmpty()) {
                logger.warn("Impossible to delete the records in these forms: {}", undeletedRecords.toJSON());
            }
            logger.warn("Number of rows deleted in these forms: {}", affectedForms.toJSON());
        }
        return affectedForms;
    }

    public int updateFirst(Values data, Operation query) {
        if (data == null) {
            throw new UnsupportedOperationException("No values in update method");
        }
        List<Values> recordIDs = this.getRecordIDs(query.setLimit(1));
        DataItem dataItem = getBuilder().update(query.getFormName(), recordIDs.getFirst().getString("id"), data);
        if (dataItem.getStatusType() == DataItem.StatusType.Ok) {
            return 1;
        } else {
            if (query.isDebug()) {
                logger.warn("Impossible update the record ID: {}", recordIDs.getFirst().getString("id"));
            }
            return 0;
        }
    }

    public Values updateAll(Values data, Operation query) {
        if (data == null) {
            throw new UnsupportedOperationException("No values in update method");
        }
        List<Values> recordIDs = this.getRecordIDs(query);
        int numberOfAffectedRecords = 0;
        List<String> unaffectedRecords = new ArrayList<>();
        for (Values recordID : recordIDs) {
            DataItem dataItem = getBuilder().update(query.getFormName(), recordID.getString("id"), data);
            if (dataItem.getStatusType() == DataItem.StatusType.Ok) {
                numberOfAffectedRecords++;
            } else {
                unaffectedRecords.add(recordID.getString("id"));
            }
        }
        if (query.isDebug()) {
            if (!unaffectedRecords.isEmpty()) {
                logger.warn("Impossible to update the following records IDs: [{}]", String.join(", ", unaffectedRecords));
            }
            logger.warn("Number of records affected: " + numberOfAffectedRecords);
        }
        return Values.newMap().set(query.getFormName(), numberOfAffectedRecords);
    }

    public String cascadeUpdateSubForms(Values dataValues, Map.Entry<String, Object> updateLink, String recordID) {
        if (dataValues.get("id") != null) {
            String getIdQuerySQL =
                "SELECT "
                    + updateLink.getKey()+".id"
                    + " FROM " + updateLink.getKey()
                    + " WHERE 1 = 1"
                    + " AND id = " + dataValues.getString("id")
                    + " AND " + updateLink.getValue() + " = " + recordID;
            final List<Values> dbID = getExecutor().query(getIdQuerySQL);
            String id = !dbID.isEmpty() ? dbID.getFirst().getString("id") : "0";
            DataItem dataItem = getBuilder().update(updateLink.getKey(), id, validDataValues(dataValues));
            if (dataItem.getStatusType() == DataItem.StatusType.Ok) {
                return dataValues.getString("id");
            }
        } else if (dataValues.get("uid") != null) {
            String getIdQuerySQL =
                "SELECT "
                    + updateLink.getKey()+".id"
                    + " FROM " + updateLink.getKey()
                    + " WHERE 1 = 1"
                    + " AND uid = " + "'" + dataValues.getString("uid")+ "'"
                    + " AND " + updateLink.getValue() + " = " + recordID;
            final List<Values> dbUID = getExecutor().query(getIdQuerySQL);
            String uid = !dbUID.isEmpty() ? dbUID.getFirst().getString("id") : "0";
            DataItem dataItem = getBuilder().update(updateLink.getKey(), uid, validDataValues(dataValues));
            if (dataItem.getStatusType() == DataItem.StatusType.Ok) {
                return uid;
            }
        } else {
            DataItem dataItem = getBuilder().insert(updateLink.getKey(), validDataValues(dataValues.set(updateLink.getValue().toString(), recordID)));
            if (dataItem.getStatusType() == DataItem.StatusType.Ok) {
                return dataItem.getId();
            }
        }
        return null;
    }

    public Values updateCascade(Values data, Values updateLinks, Operation query) {
        Values affectedValues = new Values();
        String recordID = this.getRecordIDs(query.setLimit(1)).getFirst().getString("id");
        DataItem result = getBuilder().update(query.getFormName(), recordID, validDataValues(data));
        checkDataItemErrors(result, "update");
        affectedValues.set(query.getFormName(), (result.getStatusType() == DataItem.StatusType.Ok) ? 1 : 0);
        for (Map.Entry<String, Object> updateLink : updateLinks.entrySet()) {
            List<String> updatedRecords = new ArrayList<String>();
            List<String> recordsLinkID = getExecutor().query(
                "SELECT " + updateLink.getKey()+".id FROM " + updateLink.getKey() + " WHERE " + updateLink.getValue() + " = " + recordID
            ).stream().map(values -> values.getString("id")).collect(Collectors.toList());
            if (data.get(updateLink.getKey()) instanceof Values && !((Values) data.get(updateLink.getKey())).isEmpty()) {
               final Values dataValues = data.getValues(updateLink.getKey());
               if (dataValues.values().stream().allMatch(object -> object instanceof Values)) {
                    for (int i = 0; i < dataValues.size(); i++) {
                        final String updatedRecordId = cascadeUpdateSubForms(dataValues.getValues(i), updateLink, recordID);
                        if (updatedRecordId != null) {
                            updatedRecords.add(updatedRecordId);
                        }
                    }
               } else {
                   final String updatedRecordId = cascadeUpdateSubForms(dataValues, updateLink, recordID);
                   if (updatedRecordId != null) {
                       updatedRecords.add(updatedRecordId);
                   }
               }
                recordsLinkID.removeAll(updatedRecords);
                affectedValues.set(updateLink.getKey(), (affectedValues.getInt(updateLink.getKey(), 0) + updatedRecords.size()));
                if (!updatedRecords.isEmpty()) {
                    for (String linkId : recordsLinkID) {
                        if (getBuilder().delete(updateLink.getKey(), linkId).getStatusType() == DataItem.StatusType.Ok) {
                            affectedValues.set(updateLink.getKey(), (affectedValues.getInt(updateLink.getKey(), 0) + 1));
                        }
                    }
                }
            }
        }
        if (query.isDebug()) {
            logger.warn("Number of rows updated in these forms: {}", affectedValues.toJSON());
        }
        return affectedValues;
    }

    public Values insert(Values data, Operation query) {
        if (data == null || data.isEmpty()) {
            throw new ResourceException("Data values cannot be null or empty");
        }
        DataItem dataItem = getBuilder().insert(query.getFormName(), validDataValues(data));
        checkDataItemErrors(dataItem, "insert");
        return new Values().set("id", dataItem.getId());
    }

    public Values cascadeInsert(Values insertLinks, Values data, Operation query) {
        var newRecords = new Values();
        DataItem formMainDataItem = getBuilder().insert(query.getFormName(), validDataValues(data));
        checkDataItemErrors(formMainDataItem, "insert");
        final String formMainDataId = formMainDataItem.getId();
        newRecords.set(query.getFormName(), formMainDataId);
        for (Map.Entry<String, Object> insertLink : insertLinks.entrySet()) {
            Values dataLink = data.getValues(insertLink.getKey());
            if (dataLink.values().stream().allMatch(object -> object instanceof Values)) {
                var newLinkRecords = new Values();
                for (int i = 0; i < dataLink.size() ; i++) {
                    Values dataLinkValues = dataLink.getValues(i);
                    dataLinkValues.set(insertLink.getValue().toString(), formMainDataId);
                    DataItem linkDataItem = getBuilder().insert(insertLink.getKey(), validDataValues(dataLinkValues));
                    if (linkDataItem.getStatusType() == DataItem.StatusType.Ok) {
                        newLinkRecords.add(linkDataItem.getId());
                    }
                }
                newRecords.set(insertLink.getKey(), newLinkRecords);
            } else {
                dataLink.set(insertLink.getValue().toString(), formMainDataId);
                DataItem dataItemLink = getBuilder().insert(insertLink.getKey(), validDataValues(dataLink));
                if (dataItemLink.getStatusType() == DataItem.StatusType.Ok) {
                    newRecords.set(insertLink.getKey(), dataItemLink.getId());
                }
            }
        }
        return newRecords;
    }

    public Values validDataValues(Values data) {
        data.set("active", !data.containsKey("active") || data.getBoolean("active"));
        return data;
    }

    public void checkDataItemErrors(DataItem dataItem, String action) {
        switch (dataItem.getStatus()) {
            case NotFound -> throw new ResourceException("No records found in the form " + dataItem.getFormName());
            case Error -> throw new ResourceException("Impossible to " + action + " record in the form " + dataItem.getFormName());
            case Exists -> throw new ResourceException("Already exists a record in the " + dataItem.getFormName() + "."+ dataItem.getFieldName() +" with this data.");
        }
    }
}

