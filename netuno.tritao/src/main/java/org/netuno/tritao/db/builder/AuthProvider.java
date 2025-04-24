package org.netuno.tritao.db.builder;

import org.netuno.psamata.DB;
import org.netuno.psamata.Values;
import org.netuno.tritao.db.DataItem;

import java.util.List;

public interface AuthProvider extends BuilderBase {
    default int insertAuthProvider(String name, String code) {
        Values data = new Values()
                .set("name", name)
                .set("code", code);
        return insertAuthProvider(data);
    }

    default int insertAuthProvider(Values values) {
        if (values.hasKey("id") || values.getInt("id") > 0) {
            return 0;
        }
        DataItem dataItem = new DataItem(getProteu(), "0", "");
        dataItem.setTable("netuno_auth_provider");
        dataItem.setValues(values);
        dataItem.setStatus(DataItem.Status.Insert);
        getExecutor().scriptSave(getProteu(), getHili(), "netuno_auth_provider", dataItem);

        if (dataItem.isStatusAsError()) {
            return 0;
        }

        Values data = new Values();
        if (values.hasKey("name")) {
            data.set("name", "'" + DB.sqlInjection(values.getString("name")) + "'");
        }

        if (values.hasKey("code")) {
            data.set("code", "'" + DB.sqlInjection(values.getString("code")) + "'");
        }

        int id = insertInto("netuno_auth_provider", data);
        Values record = getAuthProviderById("" + id);
        dataItem.setRecord(getAuthProviderById("" + id));
        dataItem.setStatus(DataItem.Status.Inserted);
        dataItem.setId(record.getString("id"));
        getExecutor().scriptSaved(getProteu(), getHili(), "netuno_auth_provider", dataItem);
        return id;
    }

    default Values getAuthProviderByCode(String code) {
        String select = " * ";
        String from = " netuno_auth_provider ";
        String where = "where lower(code) = '" + DB.sqlInjection(code.toLowerCase()) + "'";
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        List<Values> rows = getExecutor().query(sql);
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    default List<Values> selectAuthProviderSearch(String term) {
        String select = " * ";
        String from = " netuno_auth_provider ";
        String where = "where 1 = 1 ";
        if (!term.isEmpty()) {
            where += "and lower(name) = '" + DB.sqlInjection(term.toLowerCase()) + "'";
        }
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        return getExecutor().query(sql);
    }

    default Values getAuthProviderById(String id) {
        if (id.isEmpty() || id.equals("0")) {
            return null;
        }
        String select = " * ";
        String from = " netuno_auth_provider ";
        String where = "where id = " + DB.sqlInjectionInt(id);
        String order = " order by name ";
        String sql = "select " + select + " from " + from + where + order;
        List<Values> results = getExecutor().query(sql);
        if (results.size() == 1) {
            return results.get(0);
        }
        return null;
    }

    default List<Values> allAuthProviderUserByUser(String userId) {
        String select = " netuno_auth_provider_user.*, netuno_auth_provider.code AS \"provider_code\", netuno_auth_provider.name AS \"provider_name\" ";
        String from = " netuno_auth_provider_user INNER JOIN netuno_auth_provider ON netuno_auth_provider_user.provider_id = netuno_auth_provider.id ";
        String where = " netuno_auth_provider_user.user_id = " + DB.sqlInjectionInt(userId);
        String sql = "SELECT " + select + " FROM " + from + " WHERE " + where;
        return getExecutor().query(sql);
    }

    default boolean isAuthProviderUserAssociate(Values values) {
        String select = " * ";
        String from = " netuno_auth_provider_user ";
        String where = "where user_id = '" + DB.sqlInjectionInt(values.getString("user_id")) + "'";
        where += " AND provider_id = '" + DB.sqlInjectionInt(values.getString("provider_id")) + "'";
        where += " AND code = '" + DB.sqlInjection(values.getString("code")) + "'";

        String sql = "select " + select + " from " + from + where;
        return getExecutor().query(sql).size() == 1;
    }

    default Values getAuthProviderUserById(String id) {
        if (id.isEmpty() || id.equals("0")) {
            return null;
        }
        String select = " netuno_auth_provider_user.*, netuno_auth_provider.code AS \"provider_code\", netuno_auth_provider.name AS \"provider_name\" ";
        String from = " netuno_auth_provider_user INNER JOIN netuno_auth_provider ON netuno_auth_provider_user.provider_id = netuno_auth_provider.id ";
        String where = "where 1 = 1 ";
        if (!id.isEmpty()) {
            where += " and netuno_auth_provider_user.id = " + DB.sqlInjectionInt(id);
        }
        String sql = "select " + select + " from " + from + where;
        List<Values> rows = getExecutor().query(sql);
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    default Values getAuthProviderUserByUser(String providerId, String userId) {
        String select = " netuno_auth_provider_user.*, netuno_auth_provider.code AS \"provider_code\", netuno_auth_provider.name AS \"provider_name\" ";
        String from = " netuno_auth_provider_user INNER JOIN netuno_auth_provider ON netuno_auth_provider_user.provider_id = netuno_auth_provider.id ";
        String where = " 1 = 1 "
                + " AND netuno_auth_provider_user.provider_id = " + DB.sqlInjection(providerId)
                + " AND netuno_auth_provider_user.user_id = " + DB.sqlInjectionInt(userId);
        String sql = "SELECT " + select + " FROM " + from + " WHERE " + where;
        List<Values> results = getExecutor().query(sql);
        if (results.size() == 1) {
            return results.get(0);
        }
        return null;
    }

    default boolean hasAuthProviderUserByUser(String providerId, String userId) {
        return getAuthProviderUserByUser(providerId, userId) != null;
    }

    default Values getAuthProviderUserByUid(String uid) {
        if (uid.isEmpty() || uid.equals("0")) {
            return null;
        }
        String select = " netuno_auth_provider_user.*, netuno_auth_provider.code AS \"provider_code\", netuno_auth_provider.name AS \"provider_name\" ";
        String from = " netuno_auth_provider_user INNER JOIN netuno_auth_provider ON netuno_auth_provider_user.provider_id = netuno_auth_provider.id ";
        String where = "WHERE 1 = 1 ";
        where += " AND netuno_auth_provider_user.uid = '" + DB.sqlInjection(uid) + "'";
        String sql = "SELECT " + select + " FROM " + from + where;
        List<Values> rows = getExecutor().query(sql);
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    default Values getAuthProviderUserByCode(String providerId, String code) {
        if (code.isEmpty()) {
            return null;
        }
        String select = " netuno_auth_provider_user.*, netuno_auth_provider.code AS \"provider_code\", netuno_auth_provider.name AS \"provider_name\" ";
        String from = " netuno_auth_provider_user INNER JOIN netuno_auth_provider ON netuno_auth_provider_user.provider_id = netuno_auth_provider.id ";
        String where = "WHERE 1 = 1 ";
        where += " AND netuno_auth_provider_user.provider_id = " + DB.sqlInjection(providerId);
        where += " AND netuno_auth_provider_user.code = '" + DB.sqlInjection(code) + "'";
        String sql = "SELECT " + select + " FROM " + from + where;
        List<Values> rows = getExecutor().query(sql);
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    default Values getAuthProviderUserByEmail(String providerId, String email) {
        if (email.isEmpty()) {
            return null;
        }
        String select = " netuno_auth_provider_user.*, netuno_auth_provider.code AS \"provider_code\", netuno_auth_provider.name AS \"provider_name\" ";
        String from = " netuno_auth_provider_user INNER JOIN netuno_auth_provider ON netuno_auth_provider_user.provider_id = netuno_auth_provider.id ";
        String where = "WHERE 1 = 1 ";
        where += " AND netuno_auth_provider_user.provider_id = " + DB.sqlInjection(providerId);
        where += " AND netuno_auth_provider_user.email = '" + DB.sqlInjection(email) + "'";
        String sql = "SELECT " + select + " FROM " + from + where;
        List<Values> rows = getExecutor().query(sql);
        if (rows.size() > 0) {
            return rows.get(0);
        }
        return null;
    }

    default int clearOldAuthProviderUser(String provider_id, String code) {
        return getExecutor().execute("DELETE FROM netuno_auth_provider_user WHERE provider_id = "+ DB.sqlInjectionInt(provider_id) +" AND code NOT LIKE '" + DB.sqlInjection(code) + "' ");
    }

    default int insertAuthProviderUser(Values values) {
        DataItem dataItem = new DataItem(getProteu(), "0", "");
        dataItem.setTable("netuno_auth_provider_user");
        dataItem.setValues(values);
        dataItem.setStatus(DataItem.Status.Insert);
        getExecutor().scriptSave(getProteu(), getHili(), "netuno_auth_provider_user", dataItem);

        if (dataItem.isStatusAsError()) {
            return 0;
        }

        Values data = new Values();

        if (values.hasKey("provider_id")) {
            data.set("provider_id", values.getInt("provider_id"));
        }
        if (values.hasKey("user_id")) {
            data.set("user_id", values.getInt("user_id"));
        }
        if (values.hasKey("uid")) {
            data.set("uid", "'" + DB.sqlInjection(values.getString("uid")) + "'");
        }
        if (values.hasKey("code")) {
            data.set("code", "'" + DB.sqlInjection(values.getString("code")) + "'");
        }
        if (values.hasKey("email")) {
            data.set("email", "'" + DB.sqlInjection(values.getString("email")) + "'");
        }
        if (values.hasKey("name")) {
            data.set("name", "'" + DB.sqlInjection(values.getString("name")) + "'");
        }
        if (values.hasKey("username")) {
            data.set("username", "'" + DB.sqlInjection(values.getString("username")) + "'");
        }
        if (values.hasKey("avatar")) {
            data.set("avatar", "'" + DB.sqlInjection(values.getString("avatar")) + "'");
        }

        int id = insertInto("netuno_auth_provider_user", data);
        Values record = getAuthProviderUserById(Integer.toString(id));
        dataItem.setRecord(record);
        dataItem.setStatus(DataItem.Status.Inserted);
        dataItem.setId(record.getString("id"));
        getExecutor().scriptSaved(getProteu(), getHili(), "netuno_auth_provider_user", dataItem);
        return id;
    }

    default boolean updateAuthProviderUser(Values values) {
        return updateAuthProviderUser(values.getString("id"), values);
    }

    default boolean updateAuthProviderUser(String id, Values values) {
        if (!isId(id)) {
            return false;
        }
        id = "" + DB.sqlInjectionInt(id);
        Values dataRecord = getAuthProviderUserById(id);
        if (dataRecord == null) {
            return false;
        }
        DataItem dataItem = new DataItem(getProteu(), id, dataRecord.getString("uid"));
        dataItem.setTable("netuno_auth_provider_user");
        dataItem.setRecord(dataRecord);
        dataItem.setValues(values);
        dataItem.setStatus(DataItem.Status.Update);
        getExecutor().scriptSave(getProteu(), getHili(), "netuno_auth_provider_user", dataItem);
        if (dataItem.isStatusAsError()) {
            return false;
        }
        String update = "";
        if (values.hasKey("provider_id")) {
            update += ", provider_id = " + DB.sqlInjectionInt(values.getString("provider_id"));
        }
        if (values.hasKey("user_id")) {
            update += ", user_id = " + DB.sqlInjectionInt(values.getString("user_id"));
        }
        if (values.hasKey("code")) {
            update += ", code = '" + DB.sqlInjection(values.getString("code")) + "'";
        }
        if (values.hasKey("uid")) {
            update += ", uid = '" + DB.sqlInjection(values.getString("uid")) + "'";
        }
        if (values.hasKey("email")) {
            update += ", email = '" + DB.sqlInjection(values.getString("email")) + "'";
        }
        if (values.hasKey("name")) {
            update += ", name = '" + DB.sqlInjection(values.getString("name")) + "'";
        }
        if (values.hasKey("username")) {
            update += ", username = '" + DB.sqlInjection(values.getString("username")) + "'";
        }
        if (values.hasKey("avatar")) {
            update += ", avatar = '" + DB.sqlInjection(values.getString("avatar")) + "'";
        }
        getExecutor().execute("update netuno_auth_provider_user set id = " + id + update + " where id = " + id);
        dataItem.setStatus(DataItem.Status.Updated);
        getExecutor().scriptSaved(getProteu(), getHili(), "netuno_auth_provider_user", dataItem);
        return true;
    }

    default boolean deleteAuthProviderUser(String id) {
        id = DB.sqlInjectionInt(id);
        Values dataRecord = getAuthProviderUserById(id);
        if (dataRecord == null) {
            return false;
        }

        DataItem dataItem = new DataItem(getProteu(), id, dataRecord.getString("id"));
        dataItem.setTable("netuno_auth_provider_user");
        dataItem.setRecord(dataRecord);
        dataItem.setStatus(DataItem.Status.Delete);
        getExecutor().scriptRemove(getProteu(), getHili(), "netuno_auth_provider_user", dataItem);
        if (dataItem.isStatusAsError()) {
            return false;
        }
        getExecutor().execute("delete from netuno_auth_provider_user where id = " + id);
        dataItem.setStatus(DataItem.Status.Deleted);
        getExecutor().scriptRemoved(getProteu(), getHili(), "netuno_auth_provider_user", dataItem);
        return true;
    }
}
