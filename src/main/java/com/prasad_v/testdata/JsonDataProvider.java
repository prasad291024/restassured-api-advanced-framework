package com.prasad_v.testdata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;

import com.prasad_v.config.ConfigurationManager;
import com.prasad_v.exceptions.APIException;
import com.prasad_v.logging.CustomLogger;

/**
 * JsonDataProvider provides utilities to read test data from JSON files.
 * It supports reading data as JSON objects or as DataProvider for TestNG.
 */
public class JsonDataProvider {

    private static final CustomLogger logger = new CustomLogger(JsonDataProvider.class);
    private static final ConfigurationManager configManager = ConfigurationManager.getInstance();

    /**
     * Read JSON file and return as a JSONObject
     *
     * @param filePath Path to JSON file
     * @return JSONObject representing the file content
     * @throws APIException If there's an error reading the JSON file
     */
    public JSONObject readJsonFile(String filePath) throws APIException {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return new JSONObject(content);
        } catch (IOException | JSONException e) {
            logger.error("Error reading JSON file: " + filePath, e);
            throw new APIException("Failed to read JSON file: " + e.getMessage(), e);
        }
    }

    /**
     * Read JSON file and return as a JSONArray
     *
     * @param filePath Path to JSON file containing an array
     * @return JSONArray representing the file content
     * @throws APIException If there's an error reading the JSON file
     */
    public JSONArray readJsonArrayFile(String filePath) throws APIException {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return new JSONArray(content);
        } catch (IOException | JSONException e) {
            logger.error("Error reading JSON array file: " + filePath, e);
            throw new APIException("Failed to read JSON array file: " + e.getMessage(), e);
        }
    }

    /**
     * Get test data from JSON file as a list of maps
     *
     * @param filePath Path to JSON file containing an array of objects
     * @return List of Map with test data where each map represents a JSON object
     * @throws APIException If there's an error reading the JSON file
     */
    public List<Map<String, Object>> getTestDataFromJson(String filePath) throws APIException {
        List<Map<String, Object>> testDataList = new ArrayList<>();

        try {
            JSONArray jsonArray = readJsonArrayFile(filePath);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Map<String, Object> dataMap = jsonObjectToMap(jsonObject);
                testDataList.add(dataMap);
            }

            logger.info("Read " + testDataList.size() + " items of test data from: " + filePath);
            return testDataList;

        } catch (JSONException e) {
            logger.error("Error processing JSON data from file: " + filePath, e);
            throw new APIException("Failed to process JSON data: " + e.getMessage(), e);
        }
    }

    /**
     * Convert JSONObject to Map
     *
     * @param jsonObject JSONObject to convert
     * @return Map representation of the JSONObject
     */
    private Map<String, Object> jsonObjectToMap(JSONObject jsonObject) {
        Map<String, Object> map = new HashMap<>();

        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);

            // Handle nested objects
            if (value instanceof JSONObject) {
                value = jsonObjectToMap((JSONObject) value);
            }
            // Handle nested arrays
            else if (value instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) value;
                List<Object> list = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    Object item = jsonArray.get(i);
                    if (item instanceof JSONObject) {
                        list.add(jsonObjectToMap((JSONObject) item));
                    } else {
                        list.add(item);
                    }
                }
                value = list;
            }

            map.put(key, value);
        }

        return map;
    }

    /**
     * TestNG DataProvider that reads test data from JSON
     *
     * @param filePath Path to JSON file containing an array of objects
     * @return Object array for TestNG DataProvider
     * @throws APIException If there's an error reading the JSON file
     */
    @DataProvider(name = "jsonDataProvider")
    public Object[][] getDataFromJson(String filePath) throws APIException {
        List<Map<String, Object>> testDataList = getTestDataFromJson(filePath);

        Object[][] data = new Object[testDataList.size()][1];
        for (int i = 0; i < testDataList.size(); i++) {
            data[i][0] = testDataList.get(i);
        }

        return data;
    }

    /**
     * Filter test data by a condition
     *
     * @param testDataList Original test data list
     * @param filterKey Key to filter on
     * @param filterValue Value to match
     * @return Filtered test data list
     */
    public List<Map<String, Object>> filterTestData(List<Map<String, Object>> testDataList,
                                                    String filterKey, Object filterValue) {
        List<Map<String, Object>> filteredList = new ArrayList<>();

        for (Map<String, Object> data : testDataList) {
            if (data.containsKey(filterKey) && data.get(filterKey).equals(filterValue)) {
                filteredList.add(data);
            }
        }

        return filteredList;
    }

    /**
     * Get single test data object that matches a condition
     *
     * @param filePath Path to JSON file containing an array of objects
     * @param filterKey Key to filter on
     * @param filterValue Value to match
     * @return Map with matched test data or empty map if not found
     * @throws APIException If there's an error reading the JSON file
     */
    public Map<String, Object> getTestDataByFilter(String filePath,
                                                   String filterKey, Object filterValue) throws APIException {
        List<Map<String, Object>> testDataList = getTestDataFromJson(filePath);
        List<Map<String, Object>> filteredList = filterTestData(testDataList, filterKey, filterValue);

        if (!filteredList.isEmpty()) {
            return filteredList.get(0);
        }

        return new HashMap<>();
    }

    /**
     * Get a specific JSON object from a file by an identifier field
     *
     * @param fileName Name of the JSON file in the testdata directory
     * @param idField Field name to identify the object
     * @param idValue Value of the identification field
     * @return JSONObject that matches the criteria or null if not found
     * @throws APIException If there's an error reading or processing the JSON file
     */
    public JSONObject getJsonObjectById(String fileName, String idField, String idValue) throws APIException {
        String testDataDir = resolveTestDataPath();
        String filePath = testDataDir + File.separator + fileName;

        try {
            JSONArray jsonArray = readJsonArrayFile(filePath);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.has(idField) && jsonObject.getString(idField).equals(idValue)) {
                    return jsonObject;
                }
            }

            logger.warn("No JSON object found with " + idField + "=" + idValue + " in file: " + fileName);
            return null;

        } catch (JSONException e) {
            logger.error("Error finding JSON object by ID in file: " + filePath, e);
            throw new APIException("Failed to find JSON object by ID: " + e.getMessage(), e);
        }
    }

    /**
     * Backward-compatible TestNG data provider utility used by existing tests.
     *
     * @param fileName JSON file in test data directory
     * @param sectionName section under "testData" key
     * @return TestNG compatible 2D object array
     */
    public Object[][] getTestData(String fileName, String sectionName) {
        String filePath = resolveTestDataPath() + File.separator + fileName;
        JSONObject root = readJsonFile(filePath);
        JSONObject testData = root.optJSONObject("testData");
        if (testData == null) {
            throw new APIException("Missing 'testData' object in file: " + fileName);
        }

        JSONArray dataArray = testData.optJSONArray(sectionName);
        if (dataArray == null) {
            throw new APIException("Missing or non-array section '" + sectionName + "' in file: " + fileName);
        }

        Object[][] data = new Object[dataArray.length()][1];
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject = dataArray.getJSONObject(i);
            data[i][0] = jsonObjectToMap(jsonObject);
        }
        return data;
    }

    private String resolveTestDataPath() {
        return configManager.getConfigProperty("test.data.path",
                configManager.getConfigProperty("testdata.dir", "src/test/resources/testdata"));
    }
}
