package org.tsukilc.likeservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.tsukilc.likeservice.entity.LikeRecord;

import java.util.List;

/**
 * 点赞记录Mapper接口
 */
@Mapper
public interface LikeRecordMapper extends BaseMapper<LikeRecord> {
    
    /**
     * 查询用户的点赞列表
     *
     * @param page   分页参数
     * @param userId 用户ID
     * @param type   点赞类型
     * @return 分页后的点赞记录
     */
    IPage<LikeRecord> selectUserLikeList(Page<LikeRecord> page, @Param("userId") Long userId, @Param("type") Integer type);
    
    /**
     * 批量插入点赞记录
     *
     * @param records 点赞记录列表
     * @return 插入成功的数量
     */
    int batchInsert(@Param("list") List<LikeRecord> records);
}