package com.kiki.utils;

import com.kiki.http.HttpGet;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.Set;

public class IOUtils {

    private static Logger logger = LoggerFactory.getLogger(IOUtils.class);

    public static Document executeGet(CloseableHttpClient client, String url) {
        Document doc = null;
        HttpGet httpGet = new HttpGet(url);
        try (CloseableHttpResponse response = client.execute(httpGet)) {
            String s = EntityUtils.toString(response.getEntity(), "UTF-8");
            doc = Jsoup.parse(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public static <T> void writeToCsv(String fileName, Set<T> obj, Class<T> clazz) {
        fileName = fileName + ".csv";
        BufferedWriter bw = null;
        try {
            Field[] fields = clazz.getDeclaredFields();
            Class<? super T> c = clazz.getSuperclass();
            Field[] titleFields = c.getDeclaredFields();
            Field.setAccessible(fields, true);
            Field.setAccessible(titleFields, true);
            Field[] allFields = ArrayUtils.addAll(fields, titleFields);

            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "GBK"));
            for (Field field : allFields) {
                bw.write(field.getName());
                bw.write(",");
            }
            bw.write("\n");
            for (T instance : obj) {
                for (Field field : allFields) {
                    bw.write(field.get(instance).toString());
                    bw.write(",");
                }
                bw.write("\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
