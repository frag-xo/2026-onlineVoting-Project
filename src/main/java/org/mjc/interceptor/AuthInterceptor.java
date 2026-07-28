package org.mjc.interceptor;

import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.mjc.dto.DTO;
import org.mjc.service.OnlineUserService;
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

    @Resource
    private OnlineUserService onlineUserService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行OPTIONS请求（跨域预检）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // ============================================================
        // ✅ 放行公开接口（无需登录）
        // ============================================================
        String uri = request.getRequestURI();
        System.out.println("🔐 拦截器拦截路径: " + uri);

        // 1. 放行登录和注册
        if (uri.contains("/account/login") || uri.contains("/account/register")) {
            System.out.println("✅ 放行登录/注册接口");
            return true;
        }

        // 2. 放行验证码
        if (uri.contains("/captcha")) {
            System.out.println("✅ 放行验证码接口");
            return true;
        }

        // 3. 放行分享链接和二维码
        if (uri.contains("/share")) {
            System.out.println("✅ 放行分享接口");
            return true;
        }

        // 4. ✅ 放行投票列表查询（访客可看）
        if (uri.contains("/vote/page")) {
            System.out.println("✅ 放行投票列表接口");
            return true;
        }

        // 5. ✅ 放行投票详情和结果（访客可看）
        if (uri.contains("/vote/result")) {
            System.out.println("✅ 放行投票结果接口");
            return true;
        }

        // ============================================================
        // 从 Header 中获取 token
        // ============================================================
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

        // 管理员专用接口权限验证
        boolean needAdmin = false;

        // 1. /api/admin/** 全部需要管理员
        if (uri.startsWith("/api/admin")) {
            needAdmin = true;
        }

        // 2. 投票管理（非查询操作）
        if (uri.equals("/api/vote") && !"GET".equalsIgnoreCase(method)) {
            needAdmin = true; // POST创建、PUT修改
        }
        if (uri.matches("/api/vote/\\d+") && "DELETE".equalsIgnoreCase(method)) {
            needAdmin = true; // DELETE删除
        }
        if (uri.startsWith("/api/vote/end/")) {
            needAdmin = true; // 结束投票
        }
        if (uri.startsWith("/api/vote/init/")) {
            needAdmin = true; // 生成随机数据
        }

        // 3. 选项管理全部需要管理员
        if (uri.startsWith("/api/vote/option")) {
            needAdmin = true;
        }

        // 4. 投票记录管理（删除操作需要管理员）
        if (uri.startsWith("/api/vote/record") && !"GET".equalsIgnoreCase(method)) {
            needAdmin = true;
        }

        // 5. 在线用户管理（除心跳外全部需要管理员）
        if (uri.startsWith("/api/online") && !uri.contains("/heartbeat")) {
            needAdmin = true;
        }

        // 6. 投票审核全部需要管理员
        if (uri.startsWith("/api/vote-audit")) {
            needAdmin = true;
        }

        // 7. 投票分组管理需要管理员
        if (uri.startsWith("/api/vote-group") && !"GET".equalsIgnoreCase(method)) {
            needAdmin = true;
        }

        // 8. 积分管理（添加积分需要管理员）
        if (uri.startsWith("/api/points/add")) {
            needAdmin = true;
        }

        if (needAdmin) {
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

        // 心跳检测：更新用户在线状态
        onlineUserService.heartbeat(userId);

        return true;
    }
}
