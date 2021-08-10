package org.netuno.tritao.util;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.resource.Lang;

public class DataLabel {

    public static String form(Proteu proteu, Hili hili, Values table) {
        return form(new Lang(proteu, hili), table);
    }

    public static String form(Lang lang, Values table) {
        return lang.getOrDefault(
                "app.form."+ table.getString("name"),
                table.getString("displayname")
        );
    }

    public static String formField(Proteu proteu, Hili hili, Values table, Values field) {
        return formField(new Lang(proteu, hili), table, field);
    }

    public static String formField(Lang lang, Values table, Values field) {
        return lang.getOrDefault(
                "app.form."+ table.getString("name")
                        + "." + field.getString("name"),
                field.getString("displayname")
        );
    }

}
