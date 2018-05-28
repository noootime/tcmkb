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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SmartWorker extends AbstractWorker {

    private static Logger logger = LoggerFactory.getLogger(SmartWorker.class);

    public SmartWorker(CloseableHttpClient client) {
        super(client);
    }

    @Override
    public void work(WorkerChain chain) {
        BufferedReader br = null;
        BufferedWriter bw = null;
        int lineNum = 0;
        StringBuilder finalData = new StringBuilder();
        try {
            File thirdTitleCsv = new File("third_title.csv");
            br = new BufferedReader(new InputStreamReader(new FileInputStream(thirdTitleCsv), "GBK"));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("data.csv"), Charset.forName("GBK")));
            Set<String> fieldsSet = new HashSet<>();
            String line;
            // 标题行先读了
            br.readLine();
            StringBuilder sb;
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
                if (response.getStatusLine().getStatusCode() != 200) {
                    logger.warn("请求数据失败: " + url);
                    continue;
                }
                String s = EntityUtils.toString(response.getEntity(), "UTF-8");
                Document doc = Jsoup.parse(s);
                Elements trElements = doc.select("table.table > tbody > tr");
                sb = new StringBuilder().append(title).append(",").append(methodName).append(",").append(url).append(",");
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
                        logger.error("表格td格式有变: " + trElement);
                    }
                }
                for (String field : Constants.FIELDS) {
                    sb.append(tempMap.getOrDefault(field, " ")).append(",");
                }
                finalData.append(sb).append("\n");
            }
            logger.info("共解析到字段: " + fieldsSet.toString());
            chain.doFilter();
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
                    bw.write(finalData.toString());
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
