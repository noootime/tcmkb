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
        loginParams.add(new BasicNameValuePair("username", loginUser.getUsername()));
        loginParams.add(new BasicNameValuePair("password", loginUser.getPassword()));
        CloseableHttpResponse response = null;
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(loginParams);
            loginPost.setEntity(entity);
            loginPost.setHeader("X-Requested-With", "XMLHttpRequest");
            response = client.execute(loginPost);
            if (response.getStatusLine().getStatusCode() == 200) {
                logger.info("登陆成功: " + EntityUtils.toString(response.getEntity(), "UTF-8"));
            } else {
                logger.error("登陆失败: " + EntityUtils.toString(response.getEntity(), "UTF-8"));
            }
        } catch (IOException e) {
            logger.error("", e);
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
