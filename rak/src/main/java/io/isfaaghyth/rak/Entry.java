package io.isfaaghyth.rak;

import android.content.Context;

import com.esotericsoftware.kryo.Serializer;

import java.util.HashMap;
import java.util.List;

/**
 * Created by isfaaghyth on 17/1/17.
 */

public class Entry {
    private final Storage storage;

    protected Entry(Context context, String dbName, HashMap<Class, Serializer> serializers) {
        storage = new PlainData(context.getApplicationContext(), dbName, serializers);
    }

    public void removeAll() {
        storage.removeAll();
    }

    public <T> Entry write(String key, T value) {
        if (value == null) {
            throw new RuntimeException("tidak bisa menulis ketika null");
        } else {
            storage.insert(key, value);
        }
        return this;
    }

    public <T> T read(String key) {
        return read(key, null);
    }

    public <T> T read(String key, T defaultValue) {
        T value = storage.select(key);
        return value == null ? defaultValue : value;
    }

    public boolean isExist(String key) {
        return storage.isExist(key);
    }

    public void remove(String key) {
        storage.removeIfExist(key);
    }

    public List<String> getAll() {
        return storage.getAll();
    }
}
