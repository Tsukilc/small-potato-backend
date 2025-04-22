package org.tsukilc.api.user;

import org.springframework.web.bind.annotation.*;
import org.tsukilc.api.user.dto.LoginRequest;
import org.tsukilc.api.user.dto.RegisterRequest;
import org.tsukilc.api.user.dto.UserDTO;
import org.tsukilc.common.core.PageResult;
import org.tsukilc.common.core.Result;

import javax.ws.rs.*;

@Path("/api")
public interface UserService {

    /**
     * 用户登录
     */
    @POST
    @Path("/user/login")
    Result<UserDTO> login(@RequestBody LoginRequest request);

    /**
     * 用户注册
     */
    @POST
    @Path("/user/register")
    Result<UserDTO> register(@RequestBody RegisterRequest request);

    /**
     * 获取当前登录用户信息
     */
    @GET
    @Path("/user/current")
    Result<UserDTO> getCurrentUser();

    /**
     * 获取用户信息
     */
    @GET
    @Path("/user/{id}")
    Result<UserDTO> getUserInfo(@PathParam("id") String id);

    /**
     * 关注用户
     */
    @POST
    @Path("/user/follow/{userId}")
    Result<Void> followUser(@PathParam("userId") String userId);

    /**
     * 取消关注
     */
    @POST
    @Path("/user/unfollow/{userId}")
    Result<Void> unfollowUser(@PathParam("userId") String userId);

    /**
     * 获取用户关注列表
     */
    @GET
    @Path("/user/{userId}/followings")
    Result<PageResult<UserDTO>> getUserFollowings(
            @PathParam("userId") String userId,
            @QueryParam("page") @DefaultValue("1") Integer page,
            @QueryParam("pageSize") @DefaultValue("20") Integer pageSize);

    /**
     * 获取用户粉丝列表
     */
    @GET
    @Path("/user/{userId}/followers")
    Result<PageResult<UserDTO>> getUserFollowers(
            @PathParam("userId") String userId,
            @QueryParam("page") @DefaultValue("1") Integer page,
            @QueryParam("pageSize") @DefaultValue("20") Integer pageSize);
}
