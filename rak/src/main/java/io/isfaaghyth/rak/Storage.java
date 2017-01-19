package io.isfaaghyth.rak;

import java.util.List;

/**
 * Created by isfaaghyth on 17/1/17.
 */

public interface Storage {
    List<String> getAll();
    <E> void insert(String key, E value);
    <E> E select(String key);
    boolean isExist(String key);
    void removeIfExist(String key);
    void removeAll();
}
