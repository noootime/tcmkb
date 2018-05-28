package com.kiki.worker;

import com.kiki.constants.Constants;
import com.kiki.constants.UrlConstants;
import com.kiki.http.HttpGet;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SmartWorker extends AbstractWorker {

    public SmartWorker(CloseableHttpClient client) {
        super(client);
    }

    @Override
    public void work() {
        BufferedReader br = null;
        BufferedWriter bw = null;
        FileWriter fw = null;
        int lineNum = 0;
        try {
            File thirdTitleCsv = new File("third_title.csv");
            br = new BufferedReader(new InputStreamReader(new FileInputStream(thirdTitleCsv), "GBK"));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data.csv"), Charset.forName("GBK")));
            Set<String> fieldsSet = new HashSet<>();
            String line = null;
            // 标题行先读了
            br.readLine();
            StringBuilder sb = null;
            bw.write("title");
            bw.write(",");
            bw.write("methodName");
            bw.write(",");
            bw.write("url");
            bw.write(",");
            for (String field : Constants.FIELDS) {
                bw.write(field);
                bw.write(",");
            }
            bw.write("\n");
            while ((line = br.readLine()) != null) {
                String[] properties = line.split(",");
                String title = properties[0];
                String methodName = properties[1];
                String url = properties[2];
                HttpGet httpGet = new HttpGet(UrlConstants.HOST + url);
                CloseableHttpResponse response;
                response = client.execute(httpGet);
                System.out.println(response.getStatusLine());
                String s = EntityUtils.toString(response.getEntity(), "UTF-8");
                Document doc = Jsoup.parse(s);
                Elements trElements = doc.select("table.table > tbody > tr");
                sb = new StringBuilder();
                int j = 0;
                Map<String, String> tempMap = new HashMap<>();
                for (Element trElement : trElements) {
                    Elements tds = trElement.children();
                    if (tds.size() >= 2) {
                        String k = tds.get(0).text().trim();
                        String v = tds.get(1).text().trim();
                        // 将k,v写入csv
                        fieldsSet.add(k);
                        tempMap.put(k, v);
                    } else {
                        System.out.println("表格td格式有变: " + trElement);
                    }
                }
                for (String field : Constants.FIELDS) {
                    sb.append(tempMap.getOrDefault(field, " ")).append(",");
                }
                bw.write(title);
                bw.write(",");
                bw.write(methodName);
                bw.write(",");
                bw.write(url);
                bw.write(",");
                bw.write(sb.toString());
                bw.write("\n");
            }
            System.out.println(fieldsSet);
            System.out.println(fieldsSet.size());
            fw = new FileWriter("temp");
            fw.write(fieldsSet.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fw != null) {
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
