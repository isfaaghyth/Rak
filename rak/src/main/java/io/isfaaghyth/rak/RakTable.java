package io.isfaaghyth.rak;

/**
 * Created by isfaaghyth on 17/1/17.
 */

public class RakTable<T> {

    T content;

    @SuppressWarnings("RakDeclaration") RakTable() {}

    RakTable(T content) {
        this.content = content;
    }
}
