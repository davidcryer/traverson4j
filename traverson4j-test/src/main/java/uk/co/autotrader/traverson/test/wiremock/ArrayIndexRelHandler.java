package uk.co.autotrader.traverson.test.wiremock;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.function.Function;
import java.util.regex.Pattern;

class ArrayIndexRelHandler extends RelHandler {
    private static final Pattern REL_BY_ARRAY_INDEX_REGEX = Pattern.compile("(.*)\\[(\\d+)\\]");

    ArrayIndexRelHandler(RelHandler nextHandler) {
        super(nextHandler);
    }

    @Override
    String handle(String baseUrl, String rel, String nextUrl) {
        var regexMatcher = REL_BY_ARRAY_INDEX_REGEX.matcher(rel);
        if (regexMatcher.matches()) {
            Function<String, Object> jsonArrayElementProvider = s -> JSONObject.parse(String.format("{\"href\":\"%1$s%2$s\"}", baseUrl, s));
            var array = regexMatcher.group(1);
            var index = Integer.parseInt(regexMatcher.group(2));
            var jsonArray = new JSONArray();
            for (int i = 0; i < index; i++) {
                jsonArray.add(jsonArrayElementProvider.apply("/do-not-follow-" + i));
            }
            jsonArray.add(jsonArrayElementProvider.apply(nextUrl));
            return String.format("{\"_links\":{\"%1$s\":%2$s}}", array, jsonArray.toJSONString());
        }
        return delegateToNextHandler(baseUrl, rel, nextUrl);
    }
}
