package org.tsukilc.api.auth;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.tsukilc.api.auth.dto.AccessResponse;
import org.tsukilc.common.core.Result;

@RequestMapping("/api/auth")
public interface AuthService {

   /**
    * 验证访问令牌并返回用户信息
    */
   @PostMapping("/access")
   Result<AccessResponse> accessCheck(@RequestHeader(value = "Authorization",required = false) String token);
   
   /**
    * 生成JWT令牌
    * 
    * @param userId 用户ID
    * @return JWT令牌
    */
   @PostMapping("/generate-token")
   String generateToken(String userId);
   
   /**
    * 解析JWT令牌并提取用户ID
    * 
    * @param token JWT令牌
    * @return 用户ID
    */
   String getUserIdFromToken(String token);
}
