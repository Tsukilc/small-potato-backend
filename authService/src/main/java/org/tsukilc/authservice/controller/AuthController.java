package org.tsukilc.authservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.tsukilc.api.auth.AuthService;
import org.tsukilc.api.auth.dto.AccessResponse;
import org.tsukilc.common.core.Result;
import org.tsukilc.authservice.service.AuthServiceImpl;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/api/auth/access")
    public Result<AccessResponse> accessCheck(@RequestHeader(value = "Authorization",required = false) String token, HttpServletRequest req,HttpServletResponse response) {
        Result<AccessResponse> result = authService.accessCheck(token,req,response);
        if(!result.isSuccess()){
            // 设置http响应401
            response.setStatus(401);
        }
        return result;
    }

}
