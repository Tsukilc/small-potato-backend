package org.tsukilc.api.user;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.*;
import org.tsukilc.api.user.dto.LoginRequest;
import org.tsukilc.api.user.dto.RegisterRequest;
import org.tsukilc.api.user.dto.UserDTO;
import org.tsukilc.common.core.Result;

@DubboService
@RequestMapping("/api/user")
public interface UserService {

    /**
     * 用户登录
     */
    @PostMapping("/login")
    Result<UserDTO> login(@RequestBody LoginRequest request);

    /**
     * 用户注册
     */
    @PostMapping("/register")
    Result<UserDTO> register(@RequestBody RegisterRequest request);

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current")
    Result<UserDTO> getCurrentUser();

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    Result<UserDTO> getUserInfo(@PathVariable("id") String id);

    /**
     * 关注用户
     */
    @PostMapping("/follow/{userId}")
    Result<Void> followUser(@PathVariable("userId") String userId);

    /**
     * 取消关注
     */
    @PostMapping("/unfollow/{userId}")
    Result<Void> unfollowUser(@PathVariable("userId") String userId);

    /**
     * 获取用户关注列表
     */
    @GetMapping("/{userId}/followings")
    Result<UserDTO> getUserFollowings(@PathVariable("userId") String userId, 
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "20") Integer pageSize);

    /**
     * 获取用户粉丝列表
     */
    @GetMapping("/{userId}/followers")
    Result<UserDTO> getUserFollowers(@PathVariable("userId") String userId, 
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "20") Integer pageSize);
} 