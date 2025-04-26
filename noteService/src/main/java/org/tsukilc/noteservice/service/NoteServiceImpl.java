package org.tsukilc.noteservice.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.tsukilc.api.note.NoteService;
import org.tsukilc.api.note.dto.CreateNoteRequest;
import org.tsukilc.api.note.dto.NoteDTO;
import org.tsukilc.common.core.PageResult;
import org.tsukilc.common.core.Result;
import org.tsukilc.common.exception.BusinessException;
import org.tsukilc.common.util.UserDetail;
import org.tsukilc.noteservice.entity.Note;
import org.tsukilc.noteservice.entity.NoteImage;
import org.tsukilc.noteservice.entity.NoteTag;
import org.tsukilc.noteservice.entity.Tag;
import org.tsukilc.noteservice.mapper.NoteImageMapper;
import org.tsukilc.noteservice.mapper.NoteMapper;
import org.tsukilc.noteservice.mapper.NoteTagMapper;
import org.tsukilc.noteservice.mapper.TagMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@DubboService
@Service
public class NoteServiceImpl implements NoteService {

    @Autowired
    private NoteMapper noteMapper;
    
    @Autowired
    private NoteImageMapper noteImageMapper;
    
    @Autowired
    private NoteTagMapper noteTagMapper;
    
    @Autowired
    private TagMapper tagMapper;

    @Override
    public Result<PageResult<NoteDTO>> getNotes(Integer page, Integer pageSize, String userId, String tag, String keyword) {
        // 设置默认值
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 20;
        
        // 构建查询条件
        LambdaQueryWrapper<Note> queryWrapper = new LambdaQueryWrapper<>();
        
        // 如果指定了用户ID，则查询该用户的笔记
        if (StringUtils.hasText(userId)) {
            queryWrapper.eq(Note::getUserId, userId);
        }
        
        // 如果指定了关键词，则模糊查询标题和内容
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> 
                wrapper.like(Note::getTitle, keyword)
                       .or()
                       .like(Note::getContent, keyword)
            );
        }
        
        // 按创建时间倒序排序
        queryWrapper.orderByDesc(Note::getCreatedAt);
        
        // 分页查询
        Page<Note> notePage = new Page<>(page, pageSize);
        Page<Note> resultPage = noteMapper.selectPage(notePage, queryWrapper);
        
        // 查询结果为空，返回空结果
        if (resultPage.getRecords().isEmpty()) {
            return Result.success(null);
        }
        
        // 转换为DTO
        List<NoteDTO> noteDTOList = resultPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 如果指定了标签，进行过滤
        if (StringUtils.hasText(tag)) {
            noteDTOList = noteDTOList.stream()
                    .filter(noteDTO -> noteDTO.getTags() != null && noteDTO.getTags().contains(tag))
                    .collect(Collectors.toList());
        }
        
        return Result.success(PageResult.of(resultPage.getTotal(), noteDTOList, page, pageSize));
    }

    @Override
    public Result<NoteDTO> getNoteDetail(String id) {
        if (!StringUtils.hasText(id)) {
            return Result.fail("笔记ID不能为空");
        }
        
        // 查询笔记基本信息
        Note note = noteMapper.selectById(id);
        if (note == null) {
            return Result.fail("笔记不存在");
        }
        
        // 转换为DTO并返回
        NoteDTO noteDTO = convertToDTO(note);
        return Result.success(noteDTO);
    }

    @Override
    @Transactional
    public Result<NoteDTO> createNote(CreateNoteRequest request) {
        // 校验请求参数
        if (request == null) {
            return Result.fail("请求参数不能为空");
        }
        if (!StringUtils.hasText(request.getTitle())) {
            return Result.fail("笔记标题不能为空");
        }
        if (!StringUtils.hasText(request.getContent())) {
            return Result.fail("笔记内容不能为空");
        }
        
        // 创建笔记
        Note note = new Note();
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        
        // 设置用户ID，在实际环境中，应从当前登录用户中获取
        // 这里假设UserDetail.getUserId()能获取到当前用户ID
        String userId = UserDetail.getUserId();
        if (!StringUtils.hasText(userId)) {
            // 在测试环境中，可以使用一个假的用户ID
            userId = "test_user_id";
        }
        note.setUserId(userId);
        
        // 设置时间字段
        LocalDateTime now = LocalDateTime.now();
        note.setCreatedAt(now);
        note.setUpdatedAt(now);
        
        // 保存笔记
        noteMapper.insert(note);
        
        // 保存笔记图片
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            int order = 0;
            for (String imageUrl : request.getImages()) {
                if (StringUtils.hasText(imageUrl)) {
                    NoteImage noteImage = new NoteImage();
                    noteImage.setNoteId(note.getId());
                    noteImage.setUrl(imageUrl);
                    noteImage.setOrderNum(order++);
                    noteImage.setCreatedAt(now);
                    noteImageMapper.insert(noteImage);
                }
            }
        }
        
        // 保存笔记标签
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            for (String tagName : request.getTags()) {
                if (StringUtils.hasText(tagName)) {
                    // 查找或创建标签
                    Tag tag = getOrCreateTag(tagName);
                    
                    // 创建笔记与标签的关联
                    NoteTag noteTag = new NoteTag();
                    noteTag.setNoteId(note.getId());
                    noteTag.setTagId(tag.getId());
                    noteTag.setCreatedAt(now);
                    noteTagMapper.insert(noteTag);
                }
            }
        }
        
        // 转换为DTO并返回
        NoteDTO noteDTO = convertToDTO(note);
        return Result.success(noteDTO);
    }

    @Override
    @Transactional
    public Result<Void> deleteNote(String id) {
        if (!StringUtils.hasText(id)) {
            return Result.fail("笔记ID不能为空");
        }
        
        // 查询笔记是否存在
        Note note = noteMapper.selectById(id);
        if (note == null) {
            return Result.fail("笔记不存在");
        }
        
        // 验证当前用户是否有权限删除该笔记
        String currentUserId = UserDetail.getUserId();
        if (!StringUtils.hasText(currentUserId)) {
            currentUserId = "test_user_id"; // 测试环境下的假用户ID
        }
        
        if (!note.getUserId().equals(currentUserId)) {
            return Result.fail("无权删除该笔记");
        }
        
        // 删除笔记相关的图片
        LambdaQueryWrapper<NoteImage> imageQueryWrapper = new LambdaQueryWrapper<>();
        imageQueryWrapper.eq(NoteImage::getNoteId, id);
        noteImageMapper.delete(imageQueryWrapper);
        
        // 删除笔记相关的标签关联
        LambdaQueryWrapper<NoteTag> tagQueryWrapper = new LambdaQueryWrapper<>();
        tagQueryWrapper.eq(NoteTag::getNoteId, id);
        noteTagMapper.delete(tagQueryWrapper);
        
        // 删除笔记
        noteMapper.deleteById(id);
        
        return Result.success();
    }

    @Override
    public Result<Void> likeNote(String id) {
        // 点赞功能暂时不实现
        return Result.success();
    }

    @Override
    public Result<Void> unlikeNote(String id) {
        // 取消点赞功能暂时不实现
        return Result.success();
    }

    @Override
    public Result<Void> collectNote(String id) {
        // 收藏功能暂时不实现
        return Result.success();
    }

    @Override
    public Result<Void> uncollectNote(String id) {
        // 取消收藏功能暂时不实现
        return Result.success();
    }
    
    /**
     * 将Note实体转换为NoteDTO
     */
    private NoteDTO convertToDTO(Note note) {
        if (note == null) {
            return null;
        }
        
        NoteDTO noteDTO = new NoteDTO();
        noteDTO.setId(note.getId());
        noteDTO.setTitle(note.getTitle());
        noteDTO.setContent(note.getContent());
        noteDTO.setUserId(note.getUserId());
        noteDTO.setLikeCount(note.getLikeCount());
        noteDTO.setCommentCount(note.getCommentCount());
        noteDTO.setCollectCount(note.getCollectCount());
        noteDTO.setCreatedAt(note.getCreatedAt());
        noteDTO.setUpdatedAt(note.getUpdatedAt());
        
        // 查询笔记的图片
        LambdaQueryWrapper<NoteImage> imageQueryWrapper = new LambdaQueryWrapper<>();
        imageQueryWrapper.eq(NoteImage::getNoteId, note.getId());
        imageQueryWrapper.orderByAsc(NoteImage::getOrderNum);
        List<NoteImage> noteImages = noteImageMapper.selectList(imageQueryWrapper);
        if (noteImages != null && !noteImages.isEmpty()) {
            List<String> imageUrls = noteImages.stream()
                    .map(NoteImage::getUrl)
                    .collect(Collectors.toList());
            noteDTO.setImages(imageUrls);
        } else {
            noteDTO.setImages(new ArrayList<>());
        }
        
        // 查询笔记的标签
        List<String> tags = getTagsByNoteId(note.getId());
        noteDTO.setTags(tags);
        
        // TODO: 设置用户名、头像等信息，需要调用用户服务获取
        noteDTO.setUsername("用户昵称");
        noteDTO.setAvatar("https://static-cse.canva.cn/blob/239388/e1604019539295.jpg");
        
        // TODO: 设置是否点赞、收藏等状态，需要调用交互服务获取
        noteDTO.setIsLiked(false);
        noteDTO.setIsCollected(false);
        
        return noteDTO;
    }
    
    /**
     * 获取或创建标签
     */
    private Tag getOrCreateTag(String tagName) {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getName, tagName);
        Tag tag = tagMapper.selectOne(queryWrapper);
        
        if (tag == null) {
            // 标签不存在，创建新标签
            tag = new Tag();
            tag.setName(tagName);
            tag.setUseCount(1);
            tag.setCreatedAt(LocalDateTime.now());
            tag.setUpdatedAt(LocalDateTime.now());
            tagMapper.insert(tag);
        } else {
            // 标签存在，更新使用次数
            tag.setUseCount(tag.getUseCount() + 1);
            tag.setUpdatedAt(LocalDateTime.now());
            tagMapper.updateById(tag);
        }
        
        return tag;
    }
    
    /**
     * 根据笔记ID获取标签名称列表
     */
    private List<String> getTagsByNoteId(String noteId) {
        // 先查询笔记标签关联
        LambdaQueryWrapper<NoteTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(NoteTag::getNoteId, noteId);
        List<NoteTag> noteTags = noteTagMapper.selectList(queryWrapper);
        
        if (noteTags == null || noteTags.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 获取标签ID列表
        List<String> tagIds = noteTags.stream()
                .map(NoteTag::getTagId)
                .collect(Collectors.toList());
        
        // 查询标签名称
        LambdaQueryWrapper<Tag> tagQueryWrapper = new LambdaQueryWrapper<>();
        tagQueryWrapper.in(Tag::getId, tagIds);
        List<Tag> tags = tagMapper.selectList(tagQueryWrapper);
        
        if (tags == null || tags.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 提取标签名称
        return tags.stream()
                .map(Tag::getName)
                .collect(Collectors.toList());
    }
} 