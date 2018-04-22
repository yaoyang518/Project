package com.school.teachermanage.filter;

import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.service.UserService;
import com.school.teachermanage.util.StringUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 令牌过滤器
 *
 * @author zhangsl
 * @date 2017-11-03
 */

public class JwtFilter extends GenericFilterBean {

    private String authHead = "token";
    private String secretKey = "xi0d4fycgudx6lexcvvcxewmkfjupjps";


    @Override
    public void doFilter(final ServletRequest req, final ServletResponse res, final FilterChain chain)
            throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;
        final String authHeader = request.getHeader(authHead);

        if (HttpMethod.OPTIONS.name().equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            chain.doFilter(req, res);
        } else {
            if (authHeader == null || StringUtil.isEmpty(authHeader)) {
                DataResult result = new DataResult();
                result.setMsg("令牌错误");
                result.setDataMsg(ErrorCode.TOKEN_ERROR);
                res.setContentType("application/json");
                res.setCharacterEncoding("UTF-8");
                res.getWriter().write(JSONObject.fromObject(result).toString());
                return;
            }
            final String token = authHeader;
            try {
                //这里要根据启动类配置
                List<String> adminUrls = new ArrayList<>();
                adminUrls.add("/back/*");
                adminUrls.add("/config/*");

                List<String> userUrls = new ArrayList<>();
                userUrls.add("/front/*");
                userUrls.add("/userApi/*");
                userUrls.add("/scoreRecordApi/*");
                userUrls.add("/bankAccountApi/*");
                userUrls.add("/balanceRecordApi/*");
                userUrls.add("/aliPayAccountApi/*");

                final Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
                String subject = claims.getSubject();
                String url = request.getRequestURI();
                boolean isBack = false;
                for (String adminUrl : adminUrls) {
                    if (url.startsWith(adminUrl.replace("/*", ""))) {
                        isBack = true;
                        break;
                    }
                }
                boolean isFront = false;
                if (!isBack){
                    for (String userUrl : userUrls) {
                        if (url.startsWith(userUrl.replace("/*", ""))) {
                            isFront = true;
                            break;
                        }
                    }
                }
                boolean isUser = "user".equalsIgnoreCase(subject);
                boolean isAdmin = "admin".equalsIgnoreCase(subject);
                boolean isNegative = (isBack && isUser) || (isFront && isAdmin);
                if (isNegative) {
                    DataResult result = new DataResult();
                    result.setMsg("令牌无效");
                    result.setDataMsg(ErrorCode.TOKEN_ERROR);
                    res.setContentType("application/json");
                    res.setCharacterEncoding("UTF-8");
                    res.getWriter().write(JSONObject.fromObject(result).toString());
                    return;
                }

                if (isUser){
                    BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
                    UserService userService = (UserService) factory.getBean("userService");
                    Long userId = userService.getUserId(token);
                    if (userService.isUserLocked(userId)){
                        DataResult result = new DataResult();
                        result.setMsg("用户锁定");
                        result.setDataMsg(ErrorCode.USER_IS_LOCK);
                        res.setContentType("application/json");
                        res.setCharacterEncoding("UTF-8");
                        res.getWriter().write(JSONObject.fromObject(result).toString());
                        return;
                    }
                }

            } catch (final SignatureException e) {
                DataResult result = new DataResult();
                result.setMsg("令牌无效");
                result.setDataMsg(ErrorCode.TOKEN_ERROR);
                res.setContentType("application/json");
                res.setCharacterEncoding("UTF-8");
                res.getWriter().write(JSONObject.fromObject(result).toString());
                return;
            } catch (ExpiredJwtException e) {
                DataResult result = new DataResult();
                result.setMsg("令牌无效");
                result.setDataMsg(ErrorCode.TOKEN_ERROR);
                res.setContentType("application/json");
                res.setCharacterEncoding("UTF-8");
                res.getWriter().write(JSONObject.fromObject(result).toString());
                return;
            }
            chain.doFilter(req, res);
        }
    }
}
