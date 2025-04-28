package org.tsukilc.userservice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tsukilc.api.auth.AuthService;
import org.tsukilc.api.user.UserService;
import org.tsukilc.api.user.dto.LoginRequest;
import org.tsukilc.api.user.dto.RegisterRequest;
import org.tsukilc.api.user.dto.UserDTO;
import org.tsukilc.common.core.Result;
import org.tsukilc.common.exception.BusinessException;
import org.tsukilc.common.util.UserDetail;
import org.tsukilc.userservice.entity.User;
import org.tsukilc.userservice.entity.UserFollow;
import org.tsukilc.userservice.mapper.UserFollowMapper;
import org.tsukilc.userservice.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.*;

@DubboService
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserFollowMapper userFollowMapper;

    @DubboReference
    private AuthService authService;
    
    // 模拟生成token，实际应当使用JWT等方式
    private String generateToken(String userId) {
        return authService.generateToken(userId);
    }

    
    @Override
    public Result<UserDTO> login(LoginRequest request) {
        // 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(queryWrapper);
        
        // 校验密码
        if (user == null || !Objects.equals(user.getPassword(), request.getPassword())) {
            return Result.error(400, "用户名或密码错误");
        }
        
        // 生成token并返回用户信息
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        userDTO.setToken(generateToken(userDTO.getId()));
        
        // 补充统计数据（mock数据）
        userDTO.setFollowing(10);
        userDTO.setFollowers(20);
        userDTO.setNoteCount(5);
        userDTO.setLikeCount(30);
        userDTO.setIsFollowed(false);
        
        return Result.success(userDTO);
    }

    @Override
    public Result<UserDTO> register(RegisterRequest request) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, request.getUsername());
        User existUser = userMapper.selectOne(queryWrapper);
        if (existUser != null) {
            return Result.error(400, "用户名已存在");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setNickname(request.getNickname());
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        
        // 默认头像和简介
        user.setAvatar("https://via.placeholder.com/150");
        user.setBio("这个人很懒，什么都没留下");
        user.setGender("保密");
        
        userMapper.insert(user);
        
        // 返回用户信息
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        userDTO.setToken(generateToken(user.getId()));
        
        // 新用户统计数据初始化
        userDTO.setFollowing(0);
        userDTO.setFollowers(0);
        userDTO.setNoteCount(0);
        userDTO.setLikeCount(0);
        userDTO.setIsFollowed(false);
        
        return Result.success(userDTO);
    }

    @Override
    public Result<UserDTO> getCurrentUser() {
        try {
            String userId = UserDetail.getUserId();
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.error(404, "用户不存在");
            }
            
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            
            // 补充统计数据（mock数据）
            userDTO.setFollowing(10);
            userDTO.setFollowers(20);
            userDTO.setNoteCount(5);
            userDTO.setLikeCount(30);
            userDTO.setIsFollowed(false);
            
            return Result.success(userDTO);
        } catch (BusinessException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return Result.error("获取当前用户信息失败");
        }
    }

    @Override
    public Result<UserDTO> getUserInfo(String id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        
        // 补充统计数据（mock数据）
        userDTO.setFollowing(10);
        userDTO.setFollowers(20);
        userDTO.setNoteCount(5);
        userDTO.setLikeCount(30);
        userDTO.setIsFollowed(false);
        
        return Result.success(userDTO);
    }

    @Override
    public Result<Void> followUser(String userId) {
        try {
            String currentUserId = UserDetail.getUserId();
            
            // 检查目标用户是否存在
            User targetUser = userMapper.selectById(userId);
            if (targetUser == null) {
                return Result.error(404, "用户不存在");
            }
            
            // 不能关注自己
            if (currentUserId.equals(userId)) {
                return Result.error(400, "不能关注自己");
            }
            
            // 检查是否已关注
            LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserFollow::getUserId, currentUserId)
                    .eq(UserFollow::getFollowUserId, userId);
            
            if (userFollowMapper.selectCount(queryWrapper) > 0) {
                return Result.error(400, "已关注该用户");
            }
            
            // 添加关注记录
            UserFollow userFollow = new UserFollow();
            userFollow.setUserId(currentUserId);
            userFollow.setFollowUserId(userId);
            userFollow.setCreatedAt(LocalDateTime.now());
            
            userFollowMapper.insert(userFollow);
            
            return Result.success();
        } catch (BusinessException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return Result.error("关注用户失败");
        }
    }

    @Override
    public Result<Void> unfollowUser(String userId) {
        try {
            String currentUserId = UserDetail.getUserId();
            
            // 删除关注记录
            LambdaQueryWrapper<UserFollow> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserFollow::getUserId, currentUserId)
                    .eq(UserFollow::getFollowUserId, userId);
            
            userFollowMapper.delete(queryWrapper);
            
            return Result.success();
        } catch (BusinessException e) {
            return Result.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            return Result.error("取消关注失败");
        }
    }

    @Override
    public Result<UserDTO> getUserFollowings(String userId, Integer page, Integer pageSize) {
        // 模拟返回关注列表数据
        UserDTO mockUser1 = new UserDTO();
        mockUser1.setId("follow1");
        mockUser1.setUsername("followUser1");
        mockUser1.setNickname("关注用户1");
        mockUser1.setAvatar("https://via.placeholder.com/150");
        mockUser1.setIsFollowed(true);
        
        UserDTO mockUser2 = new UserDTO();
        mockUser2.setId("follow2");
        mockUser2.setUsername("followUser2");
        mockUser2.setNickname("关注用户2");
        mockUser2.setAvatar("https://via.placeholder.com/150");
        mockUser2.setIsFollowed(true);
        
        List<UserDTO> mockList = new ArrayList<>();
        mockList.add(mockUser1);
        mockList.add(mockUser2);
        
        return Result.success(mockUser1);
    }

    @Override
    public Result<UserDTO> getUserFollowers(String userId, Integer page, Integer pageSize) {
        // 模拟返回粉丝列表数据
        UserDTO mockUser1 = new UserDTO();
        mockUser1.setId("follower1");
        mockUser1.setUsername("followerUser1");
        mockUser1.setNickname("粉丝用户1");
        mockUser1.setAvatar("https://via.placeholder.com/150");
        mockUser1.setIsFollowed(false);
        
        UserDTO mockUser2 = new UserDTO();
        mockUser2.setId("follower2");
        mockUser2.setUsername("followerUser2");
        mockUser2.setNickname("粉丝用户2");
        mockUser2.setAvatar("https://via.placeholder.com/150");
        mockUser2.setIsFollowed(true);
        
        List<UserDTO> mockList = new ArrayList<>();
        mockList.add(mockUser1);
        mockList.add(mockUser2);
        
        return Result.success(mockUser1);
    }
} 