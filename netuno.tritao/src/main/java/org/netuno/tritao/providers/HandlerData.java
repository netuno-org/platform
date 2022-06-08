package org.netuno.tritao.providers;

import org.netuno.proteu.Proteu;
import org.netuno.psamata.Values;
import org.netuno.tritao.config.Hili;

import java.util.HashMap;

public class HandlerData {
    private static HashMap<String, Values> pendingRegisters = new HashMap<String, Values>();
    public static void addPendingRegister(String key, Values data){
        pendingRegisters.put(key, data);
    }
    public static boolean hasSecret(String key){
        return pendingRegisters.containsKey(key);
    }
    public static Values getUser(String key){
        return pendingRegisters.get(key);
    }
}
