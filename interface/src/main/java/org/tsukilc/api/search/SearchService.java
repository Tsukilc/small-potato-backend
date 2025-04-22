package org.tsukilc.api.search;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.*;
import org.tsukilc.api.note.dto.NoteDTO;
import org.tsukilc.api.search.dto.SearchSuggestionDTO;
import org.tsukilc.api.user.dto.UserDTO;
import org.tsukilc.common.core.PageResult;
import org.tsukilc.common.core.Result;

import java.util.List;

@DubboService
@RequestMapping("/api/search")
public interface SearchService {
    
    /**
     * 搜索笔记
     */
    @GetMapping("/notes")
    Result<PageResult<NoteDTO>> searchNotes(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize);
    
    /**
     * 搜索用户
     */
    @GetMapping("/users")
    Result<PageResult<UserDTO>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize);
    
    /**
     * 搜索标签
     */
    @GetMapping("/tags")
    Result<List<String>> searchTags(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") Integer limit);
    
    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    Result<List<SearchSuggestionDTO>> getSuggestions(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") Integer limit);
} 