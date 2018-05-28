import com.kiki.FirstTitle;
import com.kiki.SecondTitle;
import com.kiki.ThirdTitle;
import com.kiki.entity.Constants;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.*;

public class Main {

    final static String LOGIN_URL = "http://kb.tcmkb.cn:9104/member/user/ajax_login";
    final static String HOST = "http://kb.tcmkb.cn:9104";

    final static String _HOST = "http://kb.tcmkb.cn";
    final static int _PORT = 9104;
    /**
     * 中医养生知识库
     */
    final static String HEALTH_URL = "http://kb.tcmkb.cn:9104/article/lists?onto=health";

    private static CloseableHttpClient httpClient;

    private static final Set<FirstTitle> FIRST_TITLE = new HashSet<>();

    private static final Set<SecondTitle> SECOND_TITLE = new HashSet<>();

    private static final Set<ThirdTitle> THIRD_TITLE = new HashSet<>();

    private static void createHttpClient() {
        httpClient = HttpClients.createDefault();
    }

    private static void login() {
        HttpPost loginPost = new HttpPost(encodeUrl(LOGIN_URL));
        List<NameValuePair> loginParams = new ArrayList<NameValuePair>();
        loginParams.add(new BasicNameValuePair("username", "nsynsy001"));
        loginParams.add(new BasicNameValuePair("password", "niu19920517"));
        CloseableHttpResponse response = null;
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(loginParams);
            loginPost.setEntity(entity);
            loginPost.setHeader("X-Requested-With", "XMLHttpRequest");
            response = httpClient.execute(loginPost);
            System.out.println(EntityUtils.toString(response.getEntity()));
            System.out.println(response.getStatusLine());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解析一级标题
     * 存入常量+csv文件
     */
    private static void requestFirstTitle() {
        HttpGet httpGet = new HttpGet(encodeUrl(HEALTH_URL));
        CloseableHttpResponse homeResponse = null;
        try {
            homeResponse = httpClient.execute(httpGet);
            System.out.println(homeResponse.getStatusLine());
            String s = EntityUtils.toString(homeResponse.getEntity(), "UTF-8");
            Document doc = Jsoup.parse(s);
            Elements elements = doc.getElementsByClass("acrticle_cont");
            for (Element element : elements) {
                Elements classifications = element.getElementsByClass("cont_title");
                String classification = null;
                if (classifications != null && !classifications.isEmpty()) {
                    classification = classifications.first().child(0).text(); // TODO: NPE
                    System.out.println("一级标题: " + classification);
                }
                Elements primaryTitle = element.getElementsByClass("cont_main").select("div > p > a.lead");
                for (Element e : primaryTitle) {
                    FirstTitle firstTitle = new FirstTitle();
                    firstTitle.setClassification(classification);
                    firstTitle.setTitle(e.text().trim());
                    firstTitle.setUrl(e.attr("href").trim());
                    FIRST_TITLE.add(firstTitle);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (homeResponse != null) {
                    homeResponse.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String encodeUrl(String url) {
//        String u;
//        try {
//            u = URLEncoder.encode(url, "UTF-8");
//            System.out.println(u);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            return url;
//        }
        return url;
    }

    private static void requestSecondTitle() {
        for (FirstTitle ft : FIRST_TITLE) {
            HttpGet httpGet = new HttpGet(encodeUrl(HOST + ft.getUrl()));
            CloseableHttpResponse homeResponse = null;
            try {
                homeResponse = httpClient.execute(httpGet);
                System.out.println(homeResponse.getStatusLine());
                String s = EntityUtils.toString(homeResponse.getEntity(), "UTF-8");
                Document doc = Jsoup.parse(s);
                Elements lis = doc.getElementById("推荐").select("div.panel > div.panel-body > div.container > div.nav-pills > li");
                System.out.println(ft.getClassification() + "-" + ft.getTitle() + "下的二级分类数量：" + lis.size());
                for (Element li : lis) {
                    for (Element liChild : li.children()) {
                        SecondTitle secondTitle = new SecondTitle();
                        secondTitle.setParentTitle(ft.getTitle());
                        if (Objects.equals(liChild.tagName(), "a")) {
                            secondTitle.setUrl(liChild.attr("href"));
                            secondTitle.setTitle(liChild.select("font").first().text().trim());
                        } else {
                            System.out.println(liChild);
                        }
                        if (secondTitle.getTitle() == null || secondTitle.getTitle().isEmpty()) {
                            secondTitle.setTitle(System.currentTimeMillis() + "");
                        }
                        SECOND_TITLE.add(secondTitle);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void requestThirdTitle() {
        for (SecondTitle st : SECOND_TITLE) {
            HttpGet httpGet = new HttpGet(encodeUrl(HOST + st.getUrl()));
            CloseableHttpResponse response = null;
            try {
                response = httpClient.execute(httpGet);
                System.out.println(st.getUrl() + " : " + response.getStatusLine());
                String s = EntityUtils.toString(response.getEntity(), "UTF-8");
                Document doc = Jsoup.parse(s);

                Elements lis = doc.select("a.lead");
                if (lis == null || lis.isEmpty()) {
                    System.out.println("[" + st.getTitle() + "]下的三级标题为空");
                    continue;
                }

                for (Element li : lis) {
                    ThirdTitle thirdTitle = new ThirdTitle();
                    thirdTitle.setParentTitle(st.getTitle());
                    thirdTitle.setTitle(li.text().trim());
                    thirdTitle.setUrl(li.attr("href").trim());
                    THIRD_TITLE.add(thirdTitle);
                }

                Element listCount = Optional.ofNullable(doc.select("div.panel > div.panel-body > div.container > p > font")).map(Elements::first).orElse(null);
                if (listCount != null) {
                    String listCountLine = listCount.text().trim();
                    int count = 0;
                    try {
                        count = Integer.parseInt(listCountLine.substring(listCountLine.indexOf("获得") + 2, listCountLine.indexOf("条") - 1).trim());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    System.out.println(st.getParentTitle() + "-" + st.getTitle() + "条数: " + count);
                    double pages = Math.ceil(count / 10.0);
                    if (pages > 1) {
                        for (int i = 2; i <= pages; i++) {
                            requestThirdTitle(HOST + st.getUrl() + "&page=" + i, st);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void requestThirdTitle(String url, SecondTitle st) {
        System.out.println("翻页请求: " + url);
        try {
            HttpGet httpGet = new HttpGet(encodeUrl(HOST + url));
            CloseableHttpResponse response = null;
            response = httpClient.execute(httpGet);
            System.out.println(response.getStatusLine());
            String s = EntityUtils.toString(response.getEntity(), "UTF-8");
            Document doc = Jsoup.parse(s);

            Elements lis = doc.select("a.lead");
            if (lis == null || lis.isEmpty()) {
                System.out.println("[" + st.getTitle() + "]下的三级标题为空");
                return;
            }

            for (Element li : lis) {
                ThirdTitle thirdTitle = new ThirdTitle();
                thirdTitle.setParentTitle(st.getTitle());
                thirdTitle.setTitle(li.text().trim());
                thirdTitle.setUrl(li.attr("href").trim());
                THIRD_TITLE.add(thirdTitle);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            createHttpClient();
            login();
//            requestFirstTitle();
//            requestSecondTitle();
//            requestThirdTitle();
            pullData();
        } finally {
//            writeToCsv("first_title", FIRST_TITLE, FirstTitle.class);
//            writeToCsv("second_title", SECOND_TITLE, SecondTitle.class);
//            writeToCsv("third_title", THIRD_TITLE, ThirdTitle.class);
        }
    }

    private static <T> void writeToCsv(String fileName, Set<T> obj, Class<T> clazz) {
        fileName = fileName + ".csv";
        BufferedWriter bw = null;
        try {
            Field[] fields = clazz.getDeclaredFields();
            Field.setAccessible(fields, true);

            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "GBK"));
            for (Field field : fields) {
                bw.write(field.getName());
                bw.write(",");
                System.out.println(field);
            }
            bw.write("\n");
            for (T instance : obj) {
                for (Field field : fields) {
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

    private static void pullData() {
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
                HttpGet httpGet = new HttpGet(encodeUrl(HOST + url));
                CloseableHttpResponse response;
                response = httpClient.execute(httpGet);
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
