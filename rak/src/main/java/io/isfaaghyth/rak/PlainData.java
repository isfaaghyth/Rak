package io.isfaaghyth.rak;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;

import org.objenesis.strategy.StdInstantiatorStrategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import de.javakaffee.kryoserializers.ArraysAsListSerializer;
import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
import de.javakaffee.kryoserializers.UUIDSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;

/**
 * Created by isfaaghyth on 17/1/17.
 */

public class PlainData implements Storage {

    private String TAG = "RAK";

    private final HashMap<Class, Serializer> mCustomSerializers;
    private final Context context;
    private final String dbName;
    private String filesDir;
    private boolean RakDir;

    public PlainData(Context context, String dbName, HashMap<Class, Serializer> mCustomSerializers) {
        this.mCustomSerializers = mCustomSerializers;
        this.context = context;
        this.dbName = dbName;
    }

    private static boolean sync(FileOutputStream stream) {
        try {
            if (stream != null) stream.getFD().sync();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public List<String> getAll() {
        initializeAsset();

        File rakDirektory = new File(filesDir);
        String[] names = rakDirektory.list();

        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                names[i] = names[i].replace(".rak", "");
            }
            return Arrays.asList(names);
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public synchronized <E> void insert(String key, E value) {
        final RakTable<E> table = new RakTable<>(value);
        File originalRak = getOriginalFile(key);
        File backup = createBackup(originalRak);

        initializeAsset();

        if (originalRak.exists()) {
            if (!backup.exists()) {
                if (!originalRak.renameTo(backup)) {
                    throw new RuntimeException("Tidak bisa mengubah nama file " + originalRak + " ke " + backup);
                }
            } else {
                originalRak.delete();
            }
        }

        writeTable(key, table, originalRak.getAbsoluteFile(), backup);
    }

    @Override
    public synchronized <E> E select(String key) {
        File originalRak = getOriginalFile(key);
        File backup = createBackup(originalRak);

        initializeAsset();

        if (backup.exists()) {
            originalRak.delete();
            backup.renameTo(originalRak);
        }

        if (!isExist(key)) {
            return null;
        }

        return readTable(key, originalRak);
    }

    @Override
    public boolean isExist(String key) {
        initializeAsset();
        File originalRak = getOriginalFile(key);
        return originalRak.exists();
    }

    @Override
    public synchronized void removeIfExist(String key) {
        File originalRak = getOriginalFile(key);
        boolean deleteRak = originalRak.delete();
        initializeAsset();

        if (!originalRak.exists()) {
            return;
        }

        if (!deleteRak) {
            throw new RuntimeException("Tidak bisa menghapus file " + originalRak + " untuk table " + key);
        }
    }

    @Override
    public synchronized void removeAll() {
        initializeAsset();

        String dbLocation = getCurrentDirectory(context, dbName);
        if (!deleteDirectory(dbLocation)) {
            Log.e(TAG, "Tidak bisa menghapus direktori " + dbLocation);
        }

        RakDir = false;
    }

    private static boolean deleteDirectory(String dirLocation) {
        File dir = new File(dirLocation);
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (null != files) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file.toString());
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return dir.delete();
    }

    private void initializeAsset() {
        if (!RakDir) {
            createRakDirectory();
            RakDir = true;
        }
    }

    private String getCurrentDirectory(Context context, String dbName) {
        return context.getFilesDir() + File.separator + dbName;
    }

    private File getOriginalFile(String key) {
        final String tablePath = File.separator + key + ".rak";
        return new File(context.getFilesDir() + tablePath);
    }

    private File createBackup(File originalFile) {
        return new File(originalFile.getPath() + ".bak");
    }

    private void createRakDirectory() {
        filesDir = getCurrentDirectory(context, dbName);
        if (!new File(filesDir).exists()) {
            boolean isExist = new File(filesDir).mkdirs();
            if (!isExist) {
                throw new RuntimeException("Tidak bisa membuat direktori: " + filesDir);
            }
        }
    }

    private <E> void writeTable(String key, RakTable<E> paperTable, File originalFile, File backupFile) {
        try {
            FileOutputStream file = new FileOutputStream(originalFile);
            final Output kryoOutput = new Output(file);
            getKryo().writeObject(kryoOutput, paperTable);
            kryoOutput.flush();
            file.flush();
            sync(file);
            kryoOutput.close();
            backupFile.delete();
        } catch (IOException e) {
            if (originalFile.exists()) {
                if (!originalFile.delete()) {
                    throw new RuntimeException("Tidak bisa menghapus");
                }
            }
            throw new RuntimeException("Tidak bisa save table " + key, e);
        }
    }

    private <E> E readTable(String key, File originalFile) {
        return readTable(key, originalFile, false);
    }

    private <E> E readTable(String key, File originalFile,
                                boolean v1CompatibilityMode) {
        try {
            final Input i = new Input(new FileInputStream(originalFile));
            final Kryo kryo = getKryo();
            if (v1CompatibilityMode) {
                kryo.getFieldSerializerConfig().setOptimizedGenerics(true);
            }
            final RakTable<E> paperTable = kryo.readObject(i, RakTable.class);
            i.close();
            if (v1CompatibilityMode) {
                kryo.getFieldSerializerConfig().setOptimizedGenerics(false);
            }
            return paperTable.content;
        } catch (FileNotFoundException | KryoException | ClassCastException e) {
            if (!v1CompatibilityMode) {
                return readTable(key, originalFile, true);
            }
            if (originalFile.exists()) {
                if (!originalFile.delete()) {
                    throw new RuntimeException("tidak bisa menghapus file "
                            + originalFile, e);
                }
            }
            String errorMessage = "tidak bisa menbaca file "
                    + originalFile + " untuk table " + key;
            throw new RuntimeException(errorMessage, e);
        }
    }

    private Kryo getKryo() {
        return mKryo.get();
    }

    private final ThreadLocal<Kryo> mKryo = new ThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            return createKryoInstance();
        }
    };

    private Kryo createKryoInstance() {
        Kryo kryo = new Kryo();

        kryo.register(RakTable.class);
        kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
        kryo.setReferences(false);

        kryo.register(Collections.singletonList("").getClass(), new ArraysAsListSerializer());
        UnmodifiableCollectionsSerializer.registerSerializers(kryo);
        SynchronizedCollectionsSerializer.registerSerializers(kryo);
        kryo.addDefaultSerializer(new ArrayList<>().subList(0, 0).getClass(), new CollectionSerializer());
        kryo.addDefaultSerializer(new LinkedList<>().subList(0, 0).getClass(), new CollectionSerializer());
        kryo.register(UUID.class, new UUIDSerializer());

        for (Class<?> clazz : mCustomSerializers.keySet()) {
            kryo.register(clazz, mCustomSerializers.get(clazz));
        }

        kryo.setInstantiatorStrategy(
                new Kryo.DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));

        return kryo;
    }
}
