package uk.co.autotrader.traverson.http.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.function.Function;
import java.util.regex.Pattern;

class ArrayPropertyRelHandler extends RelHandler {
    private static final Pattern REL_BY_ARRAY_PROPERTY_REGEX = Pattern.compile("(.*)\\[(.*):(.*)\\]");
    private final String baseUrl;

    ArrayPropertyRelHandler(String baseUrl, RelHandler nextHandler) {
        super(nextHandler);
        this.baseUrl = baseUrl;
    }

    @Override
    String handle(String rel, int relIndex) {
        var regexMatcher = REL_BY_ARRAY_PROPERTY_REGEX.matcher(rel);
        if (regexMatcher.matches()) {

            var array = regexMatcher.group(0);
            var key = regexMatcher.group(1);
            var value = regexMatcher.group(2);
            var jsonArray = new JSONArray();
            for (int i = 0; i < 2; i++) {
                jsonArray.add(getArrayPropertyElementJsonObject(key, value + " wrong " + i, "do-not-follow-" + i));
            }
            jsonArray.add(getArrayPropertyElementJsonObject(key, value, String.valueOf(relIndex)));
            return String.format("{\"_embedded\":{\"%1$s\":[%2$s]},\"_links\":{\"self\":{\"href\":\"%3$s/\"}}}", array, jsonArray.toJSONString(), baseUrl);
        }
        return delegateToNextHandler(rel, relIndex);
    }

    private JSONObject getArrayPropertyElementJsonObject(String key, String value, String hrefRel) {
        Function<String, String> jsonArrayElementProvider = s -> String.format("{\"self\":{\"href\":\"%1$s/%2$s\"}}", baseUrl, s);
        var jsonElement = new JSONObject();
        jsonElement.put(key, value);
        jsonElement.put("_links", jsonArrayElementProvider.apply(hrefRel));
        return jsonElement;
    }
}
