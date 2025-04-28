package org.tsukilc.authservice.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.tsukilc.api.auth.AuthService;
import org.tsukilc.api.auth.dto.AccessResponse;
import org.tsukilc.common.core.Result;
import org.tsukilc.authservice.util.JwtUtil;

@DubboService
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtUtil jwtUtil;

    /**
     * http
     * @param token
     * @return
     */
    @Override
    public Result<AccessResponse> accessCheck(String token, HttpServletRequest req,HttpServletResponse resp) {

        String path = req.getHeader("X-Forwarded-Uri");

        if(path == null){
            // todo:全局异常处理
            return Result.error(401, "X-Forwarded-Uri is null");
        }

        // 登录注册放 todo:后续要上casbin，satoken
        if (path.contains("login") || path.contains("register")) {
            return Result.success();
        }

        // 验证令牌
        if (!jwtUtil.validateToken(token)) {
            resp.setStatus(401);
            return Result.error(401, "无效的令牌");
        }
        
        // 从令牌中获取用户ID
        String userId = jwtUtil.getUserIdFromToken(token);
        
        // 生成新的令牌（刷新令牌）
        String newToken = jwtUtil.generateToken(userId);

        resp.setHeader("x-user-id", userId);

        // 构建响应
        AccessResponse response = AccessResponse.builder()
                .userId(userId)
                .newToken(newToken)
                .build();
                
        return Result.success(response);
    }

    @Override
    public String generateToken(String userId) {
        return jwtUtil.generateToken(userId);
    }

    @Override
    public String getUserIdFromToken(String token) {
        return jwtUtil.getUserIdFromToken(token);
    }
} 