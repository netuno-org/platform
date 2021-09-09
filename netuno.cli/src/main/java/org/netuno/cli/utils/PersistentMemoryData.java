package org.netuno.cli.utils;

import org.netuno.psamata.Values;

public class PersistentMemoryData  {
    private static Values appsMemoryData = new Values();

    public static Values forApp(String appName) {
        Values appMemoryData = appsMemoryData.getValues(appName);
        if (appMemoryData == null) {
            appMemoryData = new Values();
            appsMemoryData.set(appName, appMemoryData);
        }
        return appMemoryData;
    }
}
