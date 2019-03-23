package com.tensquare.manager.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.tensquare.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class ManagerFilter extends ZuulFilter {

    @Autowired
    private JwtUtil jwtUtil;


    /**
     * 在请求前pre或者后post执行
     * @return
     */
    @Override
    public String filterType() {
//        pre ：可以在请求被路由之前调用
//        route ：在路由请求时候被调用
//        post ：在route和error过滤器之后被调用
//        error ：处理请求时发生错误时被调用
        return "pre";
    }

    /**
     * 多个过滤器执行顺序，数字越小，表示越先执行
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * 当前过滤器是否开启，true表示开启
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return false;
    }

    /**
     * 过滤器内执行操作，return任何object的值都表示继续执行
     * setsendzullResponse（fasle）表示不再继续执行
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        //得到上下文
        RequestContext currentContext = RequestContext.getCurrentContext();
        //得到request域
        HttpServletRequest request = currentContext.getRequest();
        //第一次不携带请求头header
        if (request.getMethod().equals("OPTIONS")){
            return null;
        }
        //放行登陆请求
        if (request.getRequestURI().indexOf("login")>0){
            return null;
        }
        //得到头信息
        String header = request.getHeader("Authorization");
        //判断是否有头信息
        if (header!=null && !"".equals(header)){
            //判断请求头的开始部分
            if (header.startsWith("Bearer ")){
                String token = header.substring(6);
                try {
                    //验证token
                    Claims claims = jwtUtil.parseJWT(token);
                    String roles = (String) claims.get("roles");
                    if (roles.equals("admin")){
                        //转发请求头
                        currentContext.addZuulRequestHeader("Authorization",header);
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    currentContext.setSendZuulResponse(false);//停止运行
                }
            }
        }
        currentContext.setSendZuulResponse(false);//停止运行
        currentContext.setResponseStatusCode(403);//权限不足
        currentContext.setResponseBody("权限不足");
        currentContext.getResponse().setContentType("text/html;charset=UTF-8");
        return null;
    }
}
