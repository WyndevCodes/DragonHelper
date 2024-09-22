package me.wyndev.dragonhelper.client.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Data for this mod.
 * @author PikiBanana - Taken from DungeonDodge+ and modified to use here since the config system there is awesome
 */
@SuppressWarnings("unchecked")
public class ModData {
    private static final String CONFIG_DIRECTORY = "config" + File.separator + "dragonhelper";
    private static final String FILE_NAME = "mod-data.json";
    private static final String FILE_PATH = CONFIG_DIRECTORY + File.separator + FILE_NAME;

    private Map<String, Object> dataMap = new HashMap<>();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Default values for each data type
    private static final int DEFAULT_INT = 0;
    private static final String DEFAULT_STRING = "";
    private static final boolean DEFAULT_BOOLEAN = false;
    private static final double DEFAULT_DOUBLE = 0.0;
    private static final List<Object> DEFAULT_LIST = new ArrayList<>();
    private static final Set<Object> DEFAULT_SET = new HashSet<>();
    private static final Map<String, Object> DEFAULT_MAP = new HashMap<>();

    private static ModData instance;

    public static void init() {
        instance = new ModData();
    }

    public ModData() {
        initFile();
    }

    public static ModData getInstance() {
        return instance;
    }

    private void initFile() {
        File dir = new File(CONFIG_DIRECTORY);
        if (!dir.exists()) dir.mkdirs();
        loadData();
    }

    // Methods for setting data

    public void setInt(String name, int value) {
        dataMap.put(name, value);
        saveData();
    }

    public void setString(String name, String value) {
        dataMap.put(name, value);
        saveData();
    }

    public void setBoolean(String name, boolean value) {
        dataMap.put(name, value);
        saveData();
    }

    public void setDouble(String name, double value) {
        dataMap.put(name, value);
        saveData();
    }

    public void setList(String name, List<Object> list) {
        dataMap.put(name, list);
        saveData();
    }

    public void setSet(String name, Set<Object> set) {
        dataMap.put(name, set);
        saveData();
    }

    public void setMap(String name, Map<String, Object> map) {
        dataMap.put(name, map);
        saveData();
    }

    // Methods for getting data with default values

    public int getInt(String name) {
        return getInt(name, DEFAULT_INT);
    }

    public int getInt(String name, int defaultValue) {
        Object value = dataMap.get(name);
        return (value instanceof Number) ? ((Number) value).intValue() : defaultValue;
    }

    public String getString(String name) {
        return getString(name, DEFAULT_STRING);
    }

    public String getString(String name, String defaultValue) {
        Object value = dataMap.get(name);
        return (value instanceof String) ? (String) value : defaultValue;
    }

    public boolean getBoolean(String name) {
        return getBoolean(name, DEFAULT_BOOLEAN);
    }

    public boolean getBoolean(String name, boolean defaultValue) {
        Object value = dataMap.get(name);
        return (value instanceof Boolean) ? (boolean) value : defaultValue;
    }

    public double getDouble(String name) {
        return getDouble(name, DEFAULT_DOUBLE);
    }

    public double getDouble(String name, double defaultValue) {
        Object value = dataMap.get(name);
        return (value instanceof Number) ? ((Number) value).doubleValue() : defaultValue;
    }

    public List<Object> getList(String name) {
        return getList(name, DEFAULT_LIST);
    }

    public List<Object> getList(String name, List<Object> defaultValue) {
        Object value = dataMap.get(name);
        return (value instanceof List) ? (List<Object>) value : defaultValue;
    }

    public Set<Object> getSet(String name) {
        return getSet(name, DEFAULT_SET);
    }

    public Set<Object> getSet(String name, Set<Object> defaultValue) {
        Object value = dataMap.get(name);
        return (value instanceof Set) ? (Set<Object>) value : defaultValue;
    }

    public Map<String, Object> getMap(String name) {
        return getMap(name, DEFAULT_MAP);
    }

    public Map<String, Object> getMap(String name, Map<String, Object> defaultValue) {
        Object value = dataMap.get(name);
        return (value instanceof Map) ? (Map<String, Object>) value : defaultValue;
    }

    // Methods for adding data (specific to int, double)

    public void addInt(String name, int value) {
        int currentValue = getInt(name);
        setInt(name, currentValue + value);
    }

    public void addDouble(String name, double value) {
        double currentValue = getDouble(name);
        setDouble(name, currentValue + value);
    }

    private void saveData() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            GSON.toJson(dataMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            try (FileReader reader = new FileReader(FILE_PATH)) {
                Type type = new TypeToken<HashMap<String, Object>>() {}.getType();
                dataMap = GSON.fromJson(reader, type);
                if (dataMap == null) dataMap = new HashMap<>();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //create a new file if no file exists
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
