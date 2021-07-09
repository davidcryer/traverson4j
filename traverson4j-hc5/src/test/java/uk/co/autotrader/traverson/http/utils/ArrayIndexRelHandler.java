package uk.co.autotrader.traverson.http.utils;

import com.alibaba.fastjson.JSONArray;

import java.util.function.Function;
import java.util.regex.Pattern;

class ArrayIndexRelHandler extends RelHandler {
    private static final Pattern REL_BY_ARRAY_INDEX_REGEX = Pattern.compile("(.*)\\[(\\d+)\\]");
    private final String baseUrl;

    ArrayIndexRelHandler(String baseUrl, RelHandler nextHandler) {
        super(nextHandler);
        this.baseUrl = baseUrl;
    }

    @Override
    String handle(String rel, int relIndex) {
        var regexMatcher = REL_BY_ARRAY_INDEX_REGEX.matcher(rel);
        if (regexMatcher.matches()) {
            Function<String, String> jsonArrayElementProvider = s -> String.format("{\"href\":\"%1$s/%2$s\"}", baseUrl, s);
            var array = regexMatcher.group(0);
            var index = Integer.parseInt(regexMatcher.group(1));
            var jsonArray = new JSONArray();
            for (int i = 0; i < index; i++) {
                jsonArray.add(jsonArrayElementProvider.apply("do-not-follow-" + i));
            }
            jsonArray.add(jsonArrayElementProvider.apply(String.valueOf(relIndex)));
            return String.format("{\"_links\":{\"%1$s\":%2$s}}", array, jsonArray.toJSONString());
        }
        return delegateToNextHandler(rel, relIndex);
    }
}
