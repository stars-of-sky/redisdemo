package com.rp.redisdemo.utils;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

/**
 * yml文件转为properties文件
 * Create By yrp.
 * Date:2020/5/30
 */
public class TransferUtils {

    private static final String ENCODING = "utf-8";

    public static void yml2Properties(String path) {
        final String DOT = ".";
        List<String> lines = new LinkedList<>();
        try {
            YAMLFactory yamlFactory = new YAMLFactory();
            YAMLParser parser = yamlFactory.createParser(
                    new InputStreamReader(new FileInputStream(path), Charset.forName(ENCODING)));

            String key = "";
            String value = null;
            JsonToken token = parser.nextToken();
            while (token != null) {
                if (JsonToken.START_OBJECT.equals(token)) {
                    // do nothing
                } else if (JsonToken.FIELD_NAME.equals(token)) {
                    if (key.length() > 0) {
                        key = key + DOT;
                    }
                    key = key + parser.getCurrentName();

                    token = parser.nextToken();
                    if (JsonToken.START_OBJECT.equals(token)) {
                        continue;
                    }
                    value = parser.getText();
                    lines.add(key + "=" + value);

                    int dotOffset = key.lastIndexOf(DOT);
                    if (dotOffset > 0) {
                        key = key.substring(0, dotOffset);
                    }
                    value = null;
                } else if (JsonToken.END_OBJECT.equals(token)) {
                    int dotOffset = key.lastIndexOf(DOT);
                    if (dotOffset > 0) {
                        key = key.substring(0, dotOffset);
                    } else {
                        key = "";
                        lines.add("");
                    }
                }
                token = parser.nextToken();
            }
            parser.close();

            for (String line : lines) {
                System.out.println(line);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        TransferUtils.yml2Properties("d:hehe/src/main/resources/application-dev.yml");
    }
}
