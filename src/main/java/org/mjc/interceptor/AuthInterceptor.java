package org.mjc.interceptor;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.mjc.dto.DTO;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 权限拦截器
 *
 * @author Online_Voting
 * @since 2026-07-22
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS请求（跨域预检）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 获取用户ID和角色（从请求头或参数中获取）
        String userId = request.getHeader("X-User-Id");
        String userRole = request.getHeader("X-User-Role");

        // 如果没有用户信息，返回401
        if (userId == null || userId.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSON.toJSONString(new DTO<>(401, "未登录，请先登录")));
            return false;
        }

        // 获取请求路径
        String uri = request.getRequestURI();
        String method = request.getMethod();

        // 管理员接口权限验证
        if (uri.startsWith("/api/admin/")) {
            if (!"ROLE_1".equals(userRole)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JSON.toJSONString(new DTO<>(403, "无权访问，需要管理员权限")));
                return false;
            }
        }

        // 用户接口权限验证（只能操作自己）
        if (uri.startsWith("/api/account/") && !uri.contains("/login") && !uri.contains("/register")) {
            // 这里可以添加更细粒度的权限控制
            // 比如检查用户是否只能修改自己的信息
        }

        // 投票管理接口权限验证（只有管理员可以创建/修改/删除投票）
        if (uri.startsWith("/api/vote/") && !uri.contains("/page") && !uri.contains("/result") &&
            !uri.contains("/hasVoted") && !uri.contains("/share")) {
            // 非查询类接口需要管理员权限
            if (!"GET".equalsIgnoreCase(method)) {
                if (!"ROLE_1".equals(userRole)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(JSON.toJSONString(new DTO<>(403, "无权访问，需要管理员权限")));
                    return false;
                }
            }
        }

        // 将用户信息放入请求属性，方便后续使用
        request.setAttribute("currentUserId", Long.parseLong(userId));
        request.setAttribute("currentUserRole", userRole);

        return true;
    }
}
