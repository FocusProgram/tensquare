package com.tensquare.friend.interceptor;

import com.tensquare.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 前置拦截
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("测试经过了拦截器");
        //拦截器拦截原理：无论任何都要放行，具体操作由业务层决定，拦截器只负责把请求头中包含的token的令牌进行解析验证

        String header = request.getHeader("Authorization");
        //判断请求头是否为空
        if (!StringUtils.isEmpty(header)){
            //判断请求头是以Beare 开头
            if (header.startsWith("Beare ")){
                //得到token
                String token = header.substring(6);
                //验证token
                try {
                    Claims claims = jwtUtil.parseJWT(token);
                    String roles = (String) claims.get("roles");
                    //将验证符合权限admin的token放置在request中
                    if (roles != null && roles.equals("admin")){
                        request.setAttribute("clamis_admin",claims);
                    }
                    //将验证符合权限user的token放置在request中
                    if (roles != null && roles.equals("user")){
                        request.setAttribute("clamis_user",claims);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("token不正确");

                }
            }
        }
        return true; //true表示释放，false表示拦截
    }
}
