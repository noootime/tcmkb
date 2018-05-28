package com.kiki.worker;

import com.kiki.constants.Constants;
import com.kiki.constants.UrlConstants;
import com.kiki.entity.title.FirstTitle;
import com.kiki.entity.title.SecondTitle;
import com.kiki.entity.title.ThirdTitle;
import com.kiki.utils.IOUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * 数据源-worker
 * 用于爬取一/二/三级标题并将它们分别持久化到csv文件
 */
public class OriginWorker extends AbstractWorker {
    private static Logger logger = LoggerFactory.getLogger(OriginWorker.class);

    private static final Set<FirstTitle> FIRST_TITLE = new HashSet<>();

    private static final Set<SecondTitle> SECOND_TITLE = new HashSet<>();

    private static final Set<ThirdTitle> THIRD_TITLE = new HashSet<>();

    public OriginWorker(CloseableHttpClient client) {
        super(client);
    }

    @Override
    public void work() {
        try {
            pullFirstTitle();
            pullSecondTitle();
            pullThirdTitle();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        IOUtils.writeToCsv(Constants.FIRST_TITLE_CSV, FIRST_TITLE, FirstTitle.class);
        IOUtils.writeToCsv(Constants.SECOND_TITLE_CSV, SECOND_TITLE, SecondTitle.class);
        IOUtils.writeToCsv(Constants.THIRD_TITLE_CSV, THIRD_TITLE, ThirdTitle.class);
    }

    private void pullFirstTitle() {
        logger.info("正在拉取一级标题...");
        Document doc = IOUtils.executeGet(client, UrlConstants.HEALTH_URL);
        if (doc  == null) {
            logger.error("一级标题拉取失败，请求到的document为空");
            return;
        }
        Elements elements = doc.getElementsByClass("acrticle_cont");
        if (elements == null || elements.isEmpty()) {
            logger.error("一级标题拉取失败，无法从document中找到class[.article_cont]");
            return;
        }
        for (Element element : elements) {
            Elements classifications = element.getElementsByClass("cont_title");
            if (classifications == null || classifications.isEmpty()) {
                logger.warn("无法从class[.article_cont]中找到class[.cont_title]");
                continue;
            }
            String classification = Optional.of(classifications).map(Elements::first).map(x -> x.child(0)).map(Element::text).orElse(null); // TODO: NPE
            if (classification == null) {
                logger.warn("classification为空，所在classifications: " + classifications);
                continue;
            }
            Elements primaryTitle = Optional.of(element).map(x -> x.getElementsByClass("cont_main")).map(x -> x.select("div > p > a.lead")).orElse(null);
            if (primaryTitle == null || primaryTitle.isEmpty()) {
                logger.warn("primaryTitle为空，所在element: : " + element);
                continue;
            }
            for (Element e : primaryTitle) {
                FirstTitle firstTitle = new FirstTitle();
                firstTitle.setParentTitle(classification);
                firstTitle.setTitle(e.text().trim());
                firstTitle.setUrl(e.attr("href").trim());
                logger.info("获取一级标题: " + firstTitle.getParentTitle() + " - " + firstTitle.getTitle());
                FIRST_TITLE.add(firstTitle);
            }
        }
    }

    private void pullSecondTitle() {
        for (FirstTitle ft : FIRST_TITLE) {
            Document doc = IOUtils.executeGet(client, UrlConstants.HOST + ft.getUrl());
            if (doc == null) {
                logger.error("一级标题[" + ft.getTitle() + "]下的二级标题拉取失败: " + ft.getUrl());
                continue;
            }
            Elements lis = doc.getElementById("推荐").select("div.panel > div.panel-body > div.container > div.nav-pills > li");
            logger.info(ft.getParentTitle() + "-" + ft.getTitle() + "下的二级分类数量：" + lis.size());
            for (Element li : lis) {
                for (Element liChild : li.children()) {
                    SecondTitle secondTitle = new SecondTitle();
                    secondTitle.setParentTitle(ft.getTitle());
                    if (Objects.equals(liChild.tagName(), "a")) {
                        secondTitle.setUrl(liChild.attr("href"));
                        secondTitle.setTitle(liChild.select("font").first().text().trim());
                    }
                    if (secondTitle.getTitle() == null || secondTitle.getTitle().isEmpty()) {
                        secondTitle.setTitle(System.currentTimeMillis() + "");
                    }
                    SECOND_TITLE.add(secondTitle);
                }
            }
        }
    }

    private void pullThirdTitle() {
        for (SecondTitle st : SECOND_TITLE) {
            Document doc = IOUtils.executeGet(client, UrlConstants.HOST + st.getUrl());
            if (doc == null) {
                logger.error("二级标题[" + st.getTitle() + "]下的三级标题拉取失败: " + st.getUrl());
                continue;
            }
            Elements lis = doc.select("a.lead");
            if (lis == null || lis.isEmpty()) {
                logger.info("[" + st.getTitle() + "]下的三级标题为空");
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
                logger.info(st.getParentTitle() + "-" + st.getTitle() + "条数: " + count);
                double pages = Math.ceil(count / 10.0);
                if (pages > 1) {
                    for (int i = 2; i <= pages; i++) {
                        requestThirdTitle(UrlConstants.HOST + st.getUrl() + "&page=" + i, st);
                    }
                }
            }
        }
    }

    private void requestThirdTitle(String url, SecondTitle st) {
        logger.info("翻页请求: " + url);
        Document doc = IOUtils.executeGet(client, UrlConstants.HOST + url);
        if (doc == null) {
            logger.info("翻页继续拉取失败，document为空: " + url);
            return;
        }
        Elements lis = doc.select("a.lead");
        if (lis == null || lis.isEmpty()) {
            logger.info("[" + st.getTitle() + "]下的三级标题为空");
            return;
        }

        for (Element li : lis) {
            ThirdTitle thirdTitle = new ThirdTitle();
            thirdTitle.setParentTitle(st.getTitle());
            thirdTitle.setTitle(li.text().trim());
            thirdTitle.setUrl(li.attr("href").trim());
            THIRD_TITLE.add(thirdTitle);
        }
    }

}
