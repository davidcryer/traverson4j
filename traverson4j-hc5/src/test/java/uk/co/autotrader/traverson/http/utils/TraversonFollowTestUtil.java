package uk.co.autotrader.traverson.http.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.tomakehurst.wiremock.WireMockServer;

import java.util.function.Function;
import java.util.regex.Pattern;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;

public class TraversonFollowTestUtil {
    private static final Pattern REL_BY_ARRAY_INDEX_REGEX = Pattern.compile("(.*)\\[(\\d+)\\]");
    private static final Pattern REL_BY_ARRAY_PROPERTY_REGEX = Pattern.compile("(.*)\\[(.*):(.*)\\]");
    private final WireMockServer wireMockServer;

    public TraversonFollowTestUtil(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;
    }

    public void follow(String... rels) {
        for (int i = 0; i < rels.length; ++i) {
            var rel = rels[i];
            var stubUrl = "/" + (i == 0 ? "" : String.valueOf(i));
            var stubBody = getStubBody(rel, i);
            wireMockServer.stubFor(get(stubUrl).willReturn(okJson(stubBody)
                    .withHeader("Content-Type", "application/hal+json")));
        }
    }

    private String getStubBody(String rel, int relIndex) {
        var regexMatcher = REL_BY_ARRAY_INDEX_REGEX.matcher(rel);
        if (regexMatcher.matches()) {
            Function<String, String> jsonArrayElementProvider = s -> String.format("{\"href\":\"%1$s/%2$s\"}", wireMockServer.baseUrl(), s);
            var array = regexMatcher.group(0);
            var index = Integer.parseInt(regexMatcher.group(1));
            var jsonArray = new JSONArray();
            for (int i = 0; i < index; i++) {
                jsonArray.add(jsonArrayElementProvider.apply("do-not-follow-" + i));
            }
            jsonArray.add(jsonArrayElementProvider.apply(String.valueOf(relIndex)));
            return String.format("{\"_links\":{\"%1$s\":%2$s}}", array, jsonArray.toJSONString());
        }
        regexMatcher = REL_BY_ARRAY_PROPERTY_REGEX.matcher(rel);
        if (regexMatcher.matches()) {

            var array = regexMatcher.group(0);
            var key = regexMatcher.group(1);
            var value = regexMatcher.group(2);
            var jsonArray = new JSONArray();
            for (int i = 0; i < 2; i++) {
                jsonArray.add(getArrayPropertyElementJsonObject(key, value + " wrong " + i, "do-not-follow-" + i));
            }
            jsonArray.add(getArrayPropertyElementJsonObject(key, value, String.valueOf(relIndex)));
            return String.format("{\"_embedded\":{\"%1$s\":[%2$s]},\"_links\":{\"self\":{\"href\":\"%3$s/\"}}}", array, jsonArray.toJSONString(), wireMockServer.baseUrl());
        }
        return String.format("{\"_links\":{\"%1$s\":{\"href\":\"%2$s/%3$s\"}}}", rel, wireMockServer.baseUrl(), relIndex);
    }

    private JSONObject getArrayPropertyElementJsonObject(String key, String value, String hrefRel) {
        Function<String, String> jsonArrayElementProvider = s -> String.format("{\"self\":{\"href\":\"%1$s/%2$s\"}}", wireMockServer.baseUrl(), s);
        var jsonElement = new JSONObject();
        jsonElement.put(key, value);
        jsonElement.put("_links", jsonArrayElementProvider.apply(hrefRel));
        return jsonElement;
    }

}
