package org.tsukilc.api.note;

import org.springframework.web.bind.annotation.*;
import org.tsukilc.api.note.dto.CreateNoteRequest;
import org.tsukilc.api.note.dto.NoteDTO;
import org.tsukilc.common.core.PageResult;
import org.tsukilc.common.core.Result;

@RequestMapping("/api/notes")
public interface NoteService {

    /**
     * 获取笔记列表
     */
    @GetMapping("")
    Result<PageResult<NoteDTO>> getNotes(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "keyward", required = false) String keyword);

    /**
     * 获取笔记详情
     */
    @GetMapping("/{getId}")
    Result<NoteDTO> getNoteDetail(
            @PathVariable("getId") String id);

    /**
     * 创建笔记
     */
    @PostMapping("")
    Result<NoteDTO> createNote(
            @RequestBody CreateNoteRequest request
    );

    /**
     * 删除笔记
     */
    @DeleteMapping("/{delId}")
    Result<Void> deleteNote(
            @PathVariable("delId") String id
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