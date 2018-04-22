package com.school.teachermanage.filter;

import com.school.teachermanage.annotations.Access;
import com.school.teachermanage.bean.DataResult;
import com.school.teachermanage.constants.MsgConstants;
import com.school.teachermanage.enumeration.ErrorCode;
import com.school.teachermanage.enumeration.PermissionEnum;
import com.school.teachermanage.service.AdminService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author wudc
 * @Date 2017/11/16.
 */
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Method method = handlerMethod.getMethod();

        Access access = method.getAnnotation(Access.class);
        if (access == null) {
            return true;
        }
        BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        AdminService adminService = (AdminService) factory.getBean("adminService");
        final String token = request.getHeader("token");
        PermissionEnum authorities = access.authorities();
        DataResult result = new DataResult();
        boolean flag = adminService.isAuthorizationPermission(token, authorities.name());
        if (!flag) {
            result.setMsg(MsgConstants.OPT_FAIL);
            result.setDataMsg(ErrorCode.PERMISSION_DENY);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(JSONObject.fromObject(result).toString());
        }
        return flag;
    }
}
