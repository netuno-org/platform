package org.netuno.tritao.util;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.hili.Hili;
import org.netuno.tritao.resource.Lang;

public class Translation {

    public static String formTitle(Proteu proteu, Hili hili, Values table) {
        return formTitle(new Lang(proteu, hili), table);
    }

    public static String formTitle(Lang lang, Values table) {
        return lang.getOrDefault(
                "app.form."+ table.getString("name")
                        + ".title",
                table.getString("title")
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

    public static String formFieldTitle(Proteu proteu, Hili hili, Values table, Values field) {
        return formFieldTitle(new Lang(proteu, hili), table, field);
    }

    public static String formFieldTitle(Lang lang, Values table, Values field) {
        return lang.getOrDefault(
                "app.form."+ table.getString("name")
                        + "." + field.getString("name")
                        + ".title",
                field.getString("title")
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
