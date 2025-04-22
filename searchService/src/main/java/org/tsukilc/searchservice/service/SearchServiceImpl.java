package org.tsukilc.searchservice.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;
import org.tsukilc.api.note.dto.NoteDTO;
import org.tsukilc.api.search.SearchService;
import org.tsukilc.api.search.dto.SearchSuggestionDTO;
import org.tsukilc.api.user.dto.UserDTO;
import org.tsukilc.common.core.PageResult;
import org.tsukilc.common.core.Result;
import org.tsukilc.searchservice.entity.HotSearch;
import org.tsukilc.searchservice.entity.SearchHistory;
import org.tsukilc.searchservice.mapper.HotSearchMapper;
import org.tsukilc.searchservice.mapper.SearchHistoryMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DubboService
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final SearchHistoryMapper searchHistoryMapper;
    private final HotSearchMapper hotSearchMapper;

    @Override
    public Result<PageResult<NoteDTO>> searchNotes(String keyword, Integer page, Integer pageSize) {
        // 模拟搜索笔记实现，返回假数据
        List<NoteDTO> mockNotes = createMockNoteList(keyword);
        return Result.success(PageResult.of(10L, mockNotes, page, pageSize));
    }

    @Override
    public Result<PageResult<UserDTO>> searchUsers(String keyword, Integer page, Integer pageSize) {
        // 模拟搜索用户实现，返回假数据
        List<UserDTO> mockUsers = createMockUserList(keyword);
        return Result.success(PageResult.of(5L, mockUsers, page, pageSize));
    }

    @Override
    public Result<List<String>> searchTags(String keyword, Integer limit) {
        // 模拟搜索标签实现，返回假数据
        List<String> mockTags = createMockTagList(keyword);
        return Result.success(mockTags);
    }

    @Override
    public Result<List<SearchSuggestionDTO>> getSuggestions(String keyword, Integer limit) {
        // 模拟获取搜索建议实现，返回假数据
        List<SearchSuggestionDTO> mockSuggestions = createMockSuggestionList(keyword);
        return Result.success(mockSuggestions);
    }
    
    // 创建模拟笔记列表
    private List<NoteDTO> createMockNoteList(String keyword) {
        List<NoteDTO> notes = new ArrayList<>();
        
        for (int i = 1; i <= 5; i++) {
            NoteDTO note = new NoteDTO();
            note.setId("mock_note_id_" + i);
            note.setTitle("模拟笔记标题 " + keyword + " " + i);
            note.setContent("这是模拟笔记内容 " + keyword + " " + i);
            note.setImages(List.of("https://via.placeholder.com/800x600"));
            note.setTags(List.of("旅行", keyword, "生活"));
            note.setUserId("mock_user_id");
            note.setUsername("用户昵称");
            note.setAvatar("https://via.placeholder.com/150");
            note.setLikeCount(100 + i);
            note.setCommentCount(40 + i);
            note.setCollectCount(60 + i);
            note.setIsLiked(false);
            note.setIsCollected(false);
            note.setCreatedAt(LocalDateTime.now().minusDays(i));
            note.setUpdatedAt(LocalDateTime.now().minusDays(i));
            
            notes.add(note);
        }
        
        return notes;
    }
    
    // 创建模拟用户列表
    private List<UserDTO> createMockUserList(String keyword) {
        List<UserDTO> users = new ArrayList<>();
        
        for (int i = 1; i <= 3; i++) {
            UserDTO user = new UserDTO();
            user.setId("mock_user_id_" + i);
            user.setUsername("user_" + keyword + i);
            user.setNickname(keyword + "用户" + i);
            user.setAvatar("https://via.placeholder.com/150");
            user.setBio("这是一个喜欢" + keyword + "的用户");
            user.setGender("保密");
            user.setFollowing(10 * i);
            user.setFollowers(20 * i);
            user.setNoteCount(5 * i);
            user.setLikeCount(30 * i);
            user.setIsFollowed(false);
            user.setCreatedAt(LocalDateTime.now().minusDays(30 * i));
            user.setUpdatedAt(LocalDateTime.now().minusDays(i));
            
            users.add(user);
        }
        
        return users;
    }
    
    // 创建模拟标签列表
    private List<String> createMockTagList(String keyword) {
        List<String> tags = new ArrayList<>();
        tags.add(keyword);
        tags.add(keyword + "旅行");
        tags.add(keyword + "美食");
        tags.add("小红书" + keyword);
        tags.add("推荐" + keyword);
        
        return tags;
    }
    
    // 创建模拟搜索建议列表
    private List<SearchSuggestionDTO> createMockSuggestionList(String keyword) {
        List<SearchSuggestionDTO> suggestions = new ArrayList<>();
        
        SearchSuggestionDTO userSuggestion = new SearchSuggestionDTO();
        userSuggestion.setKeyword(keyword + "用户");
        userSuggestion.setType("USER");
        suggestions.add(userSuggestion);
        
        SearchSuggestionDTO noteSuggestion = new SearchSuggestionDTO();
        noteSuggestion.setKeyword(keyword + "笔记");
        noteSuggestion.setType("NOTE");
        suggestions.add(noteSuggestion);
        
        SearchSuggestionDTO tagSuggestion = new SearchSuggestionDTO();
        tagSuggestion.setKeyword(keyword + "标签");
        tagSuggestion.setType("TAG");
        suggestions.add(tagSuggestion);
        
        return suggestions;
    }
} 