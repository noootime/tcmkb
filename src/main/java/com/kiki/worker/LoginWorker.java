package com.kiki.worker;

import com.kiki.http.LoginUser;
import com.kiki.utils.UrlUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.kiki.constants.UrlConstants.LOGIN_URL;

public class LoginWorker extends AbstractWorker {

    private static Logger logger = LoggerFactory.getLogger(LoginWorker.class);

    public LoginWorker(CloseableHttpClient client) {
        super(client);
    }

    private LoginUser loginUser;

    public LoginWorker(CloseableHttpClient client, LoginUser loginUser) {
        super(client);
        this.loginUser = loginUser;
    }

    @Override
    public void work() {
        logger.info("开始登陆: " + loginUser);
        HttpPost loginPost = new HttpPost(UrlUtils.encodeUrlParams(LOGIN_URL));
        List<NameValuePair> loginParams = new ArrayList<NameValuePair>();
        loginParams.add(new BasicNameValuePair("username", "nsynsy002"));
        loginParams.add(new BasicNameValuePair("password", "niu19920517"));
        CloseableHttpResponse response = null;
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(loginParams);
            loginPost.setEntity(entity);
            loginPost.setHeader("X-Requested-With", "XMLHttpRequest");
            response = client.execute(loginPost);
            logger.info("登陆响应: \n" + response.getEntity());
            logger.info("登陆状态: " + response.getStatusLine());
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
}
