package org.tsukilc.noteservice.service;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tsukilc.api.note.NoteService;
import org.tsukilc.api.note.dto.CreateNoteRequest;
import org.tsukilc.api.note.dto.NoteDTO;
import org.tsukilc.common.core.PageResult;
import org.tsukilc.common.core.Result;
import org.tsukilc.noteservice.mapper.NoteImageMapper;
import org.tsukilc.noteservice.mapper.NoteMapper;
import org.tsukilc.noteservice.mapper.NoteTagMapper;
import org.tsukilc.noteservice.mapper.TagMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        // 模拟实现，返回假数据
        List<NoteDTO> mockNotes = createMockNoteList();
        
        return Result.success(PageResult.of(10L, mockNotes, page, pageSize));
    }

    @Override
    public Result<NoteDTO> getNoteDetail(String id) {
        // 模拟实现，返回假数据
        NoteDTO mockNote = createMockNote(id);
        
        return Result.success(mockNote);
    }

    @Override
    @Transactional
    public Result<NoteDTO> createNote(CreateNoteRequest request) {
        // 模拟实现，返回假数据
        NoteDTO mockNote = new NoteDTO();
        mockNote.setId("mock_note_id");
        mockNote.setTitle(request.getTitle());
        mockNote.setContent(request.getContent());
        mockNote.setImages(request.getImages());
        mockNote.setTags(request.getTags());
        mockNote.setUserId("mock_user_id");
        mockNote.setUsername("用户昵称");
        mockNote.setAvatar("https://via.placeholder.com/150");
        mockNote.setLikeCount(0);
        mockNote.setCommentCount(0);
        mockNote.setCollectCount(0);
        mockNote.setIsLiked(false);
        mockNote.setIsCollected(false);
        mockNote.setCreatedAt(LocalDateTime.now());
        mockNote.setUpdatedAt(LocalDateTime.now());
        
        return Result.success(mockNote);
    }

    @Override
    @Transactional
    public Result<Void> deleteNote(String id) {
        // 模拟实现，直接返回成功
        return Result.success();
    }

    @Override
    public Result<Void> likeNote(String id) {
        // 模拟实现，直接返回成功
        return Result.success();
    }

    @Override
    public Result<Void> unlikeNote(String id) {
        // 模拟实现，直接返回成功
        return Result.success();
    }

    @Override
    public Result<Void> collectNote(String id) {
        // 模拟实现，直接返回成功
        return Result.success();
    }

    @Override
    public Result<Void> uncollectNote(String id) {
        // 模拟实现，直接返回成功
        return Result.success();
    }
    
    // 创建模拟笔记数据
    private NoteDTO createMockNote(String id) {
        NoteDTO note = new NoteDTO();
        note.setId(id);
        note.setTitle("模拟笔记标题");
        note.setContent("这是一个模拟的笔记内容，用于测试接口返回值。");
        note.setImages(List.of("https://via.placeholder.com/800x600", "https://via.placeholder.com/800x600"));
        note.setTags(List.of("旅行", "美食", "生活"));
        note.setUserId("mock_user_id");
        note.setUsername("用户昵称");
        note.setAvatar("https://via.placeholder.com/150");
        note.setLikeCount(123);
        note.setCommentCount(45);
        note.setCollectCount(67);
        note.setIsLiked(true);
        note.setIsCollected(false);
        note.setCreatedAt(LocalDateTime.now().minusDays(2));
        note.setUpdatedAt(LocalDateTime.now().minusDays(1));
        
        return note;
    }
    
    // 创建模拟笔记列表
    private List<NoteDTO> createMockNoteList() {
        List<NoteDTO> notes = new ArrayList<>();
        
        for (int i = 1; i <= 5; i++) {
            NoteDTO note = new NoteDTO();
            note.setId("mock_note_id_" + i);
            note.setTitle("模拟笔记标题 " + i);
            note.setContent("这是模拟笔记内容 " + i + "，用于测试接口返回值。");
            note.setImages(List.of("https://via.placeholder.com/800x600"));
            note.setTags(List.of("旅行", "美食"));
            note.setUserId("mock_user_id");
            note.setUsername("用户昵称");
            note.setAvatar("https://via.placeholder.com/150");
            note.setLikeCount(100 + i);
            note.setCommentCount(40 + i);
            note.setCollectCount(60 + i);
            note.setIsLiked(i % 2 == 0);
            note.setIsCollected(i % 3 == 0);
            note.setCreatedAt(LocalDateTime.now().minusDays(i));
            note.setUpdatedAt(LocalDateTime.now().minusDays(i));
            
            notes.add(note);
        }
        
        return notes;
    }
} 