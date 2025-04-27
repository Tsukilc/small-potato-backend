package org.tsukilc.authservice.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.remoting.http12.HttpRequest;
import org.apache.dubbo.remoting.http12.HttpResponse;
import org.apache.dubbo.rpc.RpcContext;
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

    @Override
    public Result<AccessResponse> accessCheck(String token) {

        HttpRequest request = (HttpRequest) RpcContext.getContext().getRequest();
        HttpResponse httpResponse = (HttpResponse) RpcContext.getContext().getResponse();
        String path = request.header("X-Forwarded-Uri");

        // 登录注册放 todo:后续要上casbin，satoken
        if (path.contains("login") || path.contains("register")) {
            return Result.success();
        }

        // 验证令牌
        if (!jwtUtil.validateToken(token)) {
            httpResponse.setStatus(401);
            return Result.error(401, "无效的令牌");
        }
        
        // 从令牌中获取用户ID
        String userId = jwtUtil.getUserIdFromToken(token);
        
        // 生成新的令牌（刷新令牌）
        String newToken = jwtUtil.generateToken(userId);

        httpResponse.setHeader("x-user-id", userId);
        
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