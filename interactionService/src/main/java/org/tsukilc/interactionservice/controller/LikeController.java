package org.tsukilc.interactionservice.controller;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;
import org.tsukilc.api.like.dto.LikeCountRequest;
import org.tsukilc.api.like.dto.LikeRequest;
import org.tsukilc.api.like.dto.LikeResponse;
import org.tsukilc.api.like.dto.UserLikeListRequest;
import org.tsukilc.api.like.LikeService;

import java.util.List;
import java.util.Map;

/**
 * 点赞控制器
 */
@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @DubboReference
    private LikeService likeService;

    /**
     * 点赞或取消点赞
     */
    @PostMapping
    public LikeResponse like(@RequestBody LikeRequest request) {
        return likeService.like(request);
    }

    /**
     * 获取用户对特定目标的点赞状态
     */
    @GetMapping("/status")
    public Boolean getLikeStatus(
            @RequestParam("userId") Long userId,
            @RequestParam("targetId") Long targetId,
            @RequestParam("type") Integer type) {
        return likeService.getLikeStatus(userId, targetId, type);
    }

    /**
     * 获取用户的点赞列表
     */
    @PostMapping("/list")
    public List<LikeResponse> getUserLikeList(@RequestBody UserLikeListRequest request) {
        return likeService.getUserLikeList(request);
    }

    /**
     * 批量获取目标的点赞数量
     */
    @PostMapping("/count")
    public Map<Long, Long> batchGetLikeCount(@RequestBody LikeCountRequest request) {
        return likeService.batchGetLikeCount(request);
    }

    /**
     * 获取用户所有笔记的总点赞数
     */
    @GetMapping("/total/{userId}")
    public Long getUserTotalLikeCount(@PathVariable("userId") Long userId) {
        return likeService.getUserTotalLikeCount(userId);
    }
}