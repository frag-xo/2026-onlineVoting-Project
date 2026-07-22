package org.mjc.interceptor;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.mjc.dto.DTO;
import org.mjc.utils.JwtUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;

/**
 * 权限拦截器（JWT版本）
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS请求（跨域预检）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从 Header 中获取 token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(new DTO<>(401, "未登录，请先登录")));
            return false;
        }

        // 解析 token
        String token = authHeader.substring(7);
        if (!jwtUtils.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(new DTO<>(401, "token无效或已过期")));
            return false;
        }

        // 从 token 中获取用户信息
        Long userId = jwtUtils.getUserId(token);
        String utype = jwtUtils.getUtype(token);

        // 获取请求路径
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 管理员接口权限验证
        if (uri.startsWith("/api/admin/")) {
            if (!"ROLE_1".equals(utype)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JSON.toJSONString(new DTO<>(403, "无权访问，需要管理员权限")));
                return false;
            }
        }

        // 投票管理接口权限验证（只有管理员可以创建/修改/删除投票）
        if (uri.startsWith("/api/vote/") && !uri.contains("/page") && !uri.contains("/result") &&
            !uri.contains("/hasVoted") && !uri.contains("/share") && !uri.contains("/vote")) {
            if (!"GET".equalsIgnoreCase(method)) {
                if (!"ROLE_1".equals(utype)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(JSON.toJSONString(new DTO<>(403, "无权访问，需要管理员权限")));
                    return false;
                }
            }
        }

        // 将用户信息放入请求属性，方便后续使用
        request.setAttribute("currentUserId", userId);
        request.setAttribute("currentUserRole", utype);

        return true;
    }
}
