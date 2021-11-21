package org.netuno.tritao.util;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Hili;
import org.netuno.tritao.resource.Lang;

public class Translation {

    public static String formTitle(Proteu proteu, Hili hili, Values table) {
        return formTitle(new Lang(proteu, hili), table);
    }

    public static String formTitle(Lang lang, Values table) {
        return lang.getOrDefault(
                "app.form."+ table.getString("name")
                        + ".title",
                table.getString("displayname")
        );
    }

    public static String formDescription(Proteu proteu, Hili hili, Values table) {
        return formDescription(new Lang(proteu, hili), table);
    }

    public static String formDescription(Lang lang, Values table) {
        return lang.getOrDefault(
                "app.form."+ table.getString("name")
                + ".description",
                table.getString("description")
        );
    }

    public static String formFieldLabel(Proteu proteu, Hili hili, Values table, Values field) {
        return formFieldLabel(new Lang(proteu, hili), table, field);
    }

    public static String formFieldLabel(Lang lang, Values table, Values field) {
        return lang.getOrDefault(
                "app.form."+ table.getString("name")
                        + "." + field.getString("name")
                        + ".label",
                field.getString("displayname")
        );
    }

    public static String formFieldDescription(Proteu proteu, Hili hili, Values table, Values field) {
        return formFieldDescription(new Lang(proteu, hili), table, field);
    }

    public static String formFieldDescription(Lang lang, Values table, Values field) {
        return lang.getOrDefault(
                "app.form."+ table.getString("name")
                        + "." + field.getString("name")
                        + ".description",
                field.getString("description")
        );
    }
}
