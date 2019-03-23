package com.tensquare.user.controller;

import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class QQController {

    /**
     * 跳转到QQ授权地址
     *
     * @param request
     * @return
     * @throws QQConnectException
     */
    @RequestMapping(value = "/locaQQLogin", method = RequestMethod.GET)
    public String locaQQLogin(HttpServletRequest request, HttpServletResponse response) throws QQConnectException {
        String authorizeURL = new Oauth().getAuthorizeURL(request);
        return authorizeURL;
    }

    /**
     * 实现授权成功回调获取用户信息
     * @param request
     * @param session
     * @param response
     * @return
     * @throws QQConnectException
     */
    @RequestMapping(value="/mycb",method=RequestMethod.GET)
    public String qqLoginCallback(HttpServletRequest request, HttpSession session, HttpServletResponse response) throws QQConnectException {
        // 1.获取授权码Code
        // 2.使用授权码Code获取accessToken
        AccessToken tokenByRequest = new Oauth().getAccessTokenByRequest(request);
        if (tokenByRequest == null) {
            request.setAttribute("error", "QQ授权失败");
            return null;
        }
        String accessToken = tokenByRequest.getAccessToken();
        if (tokenByRequest == null) {
            request.setAttribute("error", "accessToken为NULL");
            return null;
        }
        // 3.使用accessToken获取openId
        OpenID openID = new OpenID(accessToken);
        if (tokenByRequest == null) {
            request.setAttribute("error", "openID为NULL");
            return null;
        }
        String userOpenID = openID.getUserOpenID();
        if (StringUtils.isEmpty(userOpenID)) {
            request.setAttribute("error", "userOpenID为NULL");
            return null;
        }
        UserInfo qzoneUserInfo = new UserInfo(accessToken, userOpenID);
        UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
        System.out.println("<br/>");
        if (userInfoBean.getRet() == 0) {
            System.out.println(userInfoBean.getNickname() + "<br/>");
            System.out.println(userInfoBean.getGender() + "<br/>");
            System.out.println("黄钻等级： " + userInfoBean.getLevel() + "<br/>");
            System.out.println("会员 : " + userInfoBean.isVip() + "<br/>");
            System.out.println("黄钻会员： " + userInfoBean.isYellowYearVip() + "<br/>");
            System.out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL30() + "/><br/>");
            System.out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL50() + "/><br/>");
            System.out.println("<image src=" + userInfoBean.getAvatar().getAvatarURL100() + "/><br/>");
        } else {
            System.out.println("很抱歉，我们没能正确获取到您的信息，原因是： " + userInfoBean.getMsg());
        }
        return  null;
    }

}
