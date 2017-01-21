package io.isfaaghyth.rak;

import android.content.Context;

import com.esotericsoftware.kryo.Serializer;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by isfaaghyth on 18/1/17.
 */

public class Rak {

    static final String DEFAULT_DB_NAME = "rak.db";

    static Context mContext;

    private static final ConcurrentHashMap<String, Entry> entryMap = new ConcurrentHashMap<>();
    private static final HashMap<Class, Serializer> customSerializers = new HashMap<>();

    public static void initialize(Context context) {
        mContext = context.getApplicationContext();
    }

    public static Entry entry(String name) {
        if (name.equals(DEFAULT_DB_NAME)) throw new RuntimeException(DEFAULT_DB_NAME +
                " same as library name. try using different name");
        return getEntry(name);
    }

    public static Entry entry() {
        return getEntry(DEFAULT_DB_NAME);
    }

    public static <T> Entry entry(String key, T value) {
        return entry().write(key, value);
    }

    private static Entry getEntry(String name) {
        if (mContext == null) {
            throw new RuntimeException("Rak cant loaded");
        }
        synchronized (entryMap) {
            Entry entry = entryMap.get(name);
            if (entry == null) {
                entry = new Entry(mContext, name, customSerializers);
                entryMap.put(name, entry);
            }
            return entry;
        }
    }

    public static <T> T grab(String key) {
        return entry().read(key);
    }

    public static <T> T grab(String key, T defaultValue) {
        return entry().read(key, defaultValue);
    }

    public static boolean isExist(String key) {
        return entry().isExist(key);
    }

    public static void remove(String key) {
        entry().remove(key);
    }

    public static void removeAll(Context context) {
        initialize(context);
        entry().removeAll();
    }

    public static <T> void addSerialize(Class<T> clazz, Serializer<T> serializer) {
        if (!customSerializers.containsKey(clazz))
            customSerializers.put(clazz, serializer);
    }
}
