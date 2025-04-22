package org.tsukilc.api.note;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.*;
import org.tsukilc.api.note.dto.CreateNoteRequest;
import org.tsukilc.api.note.dto.NoteDTO;
import org.tsukilc.common.core.PageResult;
import org.tsukilc.common.core.Result;

@DubboService
@RequestMapping("/api/notes")
public interface NoteService {

    /**
     * 获取笔记列表
     */
    @GetMapping
    Result<PageResult<NoteDTO>> getNotes(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String keyword,
            @RequestHeader(value = "Authorization", required = false) String token);

    /**
     * 获取笔记详情
     */
    @GetMapping("/{id}")
    Result<NoteDTO> getNoteDetail(
            @PathVariable("id") String id,
            @RequestHeader(value = "Authorization", required = false) String token);

    /**
     * 创建笔记
     */
    @PostMapping
    Result<NoteDTO> createNote(
            @RequestBody CreateNoteRequest request
);

    /**
     * 删除笔记
     */
    @DeleteMapping("/{id}")
    Result<Void> deleteNote(
            @PathVariable("id") String id
);

    /**
     * 点赞笔记
     */
    @PostMapping("/{id}/like")
    Result<Void> likeNote(
            @PathVariable("id") String id
);

    /**
     * 取消点赞
     */
    @PostMapping("/{id}/unlike")
    Result<Void> unlikeNote(
            @PathVariable("id") String id
);

    /**
     * 收藏笔记
     */
    @PostMapping("/{id}/collect")
    Result<Void> collectNote(
            @PathVariable("id") String id
);

    /**
     * 取消收藏
     */
    @PostMapping("/{id}/uncollect")
    Result<Void> uncollectNote(
            @PathVariable("id") String id
);
} 