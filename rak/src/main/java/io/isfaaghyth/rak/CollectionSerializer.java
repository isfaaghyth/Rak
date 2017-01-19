package io.isfaaghyth.rak;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by isfaaghyth on 18/1/17.
 */

public class CollectionSerializer extends com.esotericsoftware.kryo.serializers.CollectionSerializer {
    @Override
    protected Collection create(Kryo kryo, Input input, Class<Collection> type) {
        return new ArrayList();
    }
}
