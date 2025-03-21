package helpers;

import java.util.Arrays;
import java.util.HashMap;

import helpers.models.Pair;

public class CollectionHelper {

    // helper method to reduce line of code to create hashMap for filtering rows from database
    public static HashMap<String, String> WithFilter(String columnName, String value) {
        var filter = new HashMap<String, String>();

        filter.put(columnName, value);

        return filter;
    }

    public static HashMap<String, String> WithFilters(Pair... pairs) {
        var filter = new HashMap<String, String>();

        for (var item : pairs) {
            filter.put(item.Key, item.Value);
        }

        return filter;
    }

}
