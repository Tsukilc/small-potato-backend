package org.tsukilc.api.user;

import org.springframework.web.bind.annotation.*;
import org.tsukilc.api.user.dto.LoginRequest;
import org.tsukilc.api.user.dto.RegisterRequest;
import org.tsukilc.api.user.dto.UserDTO;
import org.tsukilc.common.core.Result;

@RequestMapping("/api")
public interface UserService {

    /**
     * 用户登录
     */
    @PostMapping("/user/login")
    Result<UserDTO> login(@RequestBody LoginRequest request);

    /**
     * 用户注册
     */
    @PostMapping("/user/register")
    Result<UserDTO> register(@RequestBody RegisterRequest request);

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/user/current")
    Result<UserDTO> getCurrentUser();

    /**
     * 获取用户信息
     */
    @GetMapping("/user/{id}")
    Result<UserDTO> getUserInfo(@PathVariable("id") String id);

    /**
     * 关注用户
     */
    @PostMapping("/user/follow/{userId}")
    Result<Void> followUser(@PathVariable("userId") String userId);

    /**
     * 取消关注
     */
    @PostMapping("/user/unfollow/{userId}")
    Result<Void> unfollowUser(@PathVariable("userId") String userId);

    /**
     * 获取用户关注列表
     */
    @GetMapping("/user/{userId}/followings")
    Result<UserDTO> getUserFollowings(
            @PathVariable("userId") String userId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize);

    /**
     * 获取用户粉丝列表
     */
    @GetMapping("/user/{userId}/followers")
    Result<UserDTO> getUserFollowers(
            @PathVariable("userId") String userId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize);
}
