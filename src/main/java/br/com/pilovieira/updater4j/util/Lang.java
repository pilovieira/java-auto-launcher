package br.com.pilovieira.updater4j.util;

import java.util.Locale;
import java.util.ResourceBundle;

public enum Lang {

    English("en"),
    Portuguese("pt");

    public static ResourceBundle lang;

    public static void initialize(Lang lang) {
        Lang.lang = ResourceBundle.getBundle("lang/Messages", new Locale(lang.locale));
    }

    public String locale;

    Lang(String locale) {
        this.locale = locale;
    }

    public static String msg(String key) {
        return lang.getString(key);
    }
}