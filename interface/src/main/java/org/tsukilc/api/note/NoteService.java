package org.tsukilc.api.note;

import org.springframework.web.bind.annotation.*;
import org.tsukilc.api.note.dto.CreateNoteRequest;
import org.tsukilc.api.note.dto.NoteDTO;
import org.tsukilc.common.core.PageResult;
import org.tsukilc.common.core.Result;

import javax.ws.rs.*;

@Path("/api")
public interface NoteService {

    /**
     * 获取笔记列表
     */
    @GET
    @Path("/notes")
    Result<PageResult<NoteDTO>> getNotes(
            @QueryParam("page") Integer page,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("userId") String userId,
            @QueryParam("tag") String tag,
            @QueryParam("keyward") String keyword);

    /**
     * 获取笔记详情
     */
    @GET
    @Path("/notes/{id}")
    Result<NoteDTO> getNoteDetail(
            @PathParam("id") String id);

    /**
     * 创建笔记
     */
    @POST
    @Path("/notes")
    Result<NoteDTO> createNote(
            @RequestBody CreateNoteRequest request
);

    /**
     * 删除笔记
     */
    @DELETE
    @Path("/notes/{id}")
    Result<Void> deleteNote(
            @PathParam("id") String id
);

    /**
     * 点赞笔记
     */
    @POST
    @Path("/notes/{id}/like")
    Result<Void> likeNote(
             @PathParam("id") String id
);

    /**
     * 取消点赞
     */
    @Path("/notes/{id}/unlike")
    @POST
    Result<Void> unlikeNote(
            @PathParam("id") String id
);

    /**
     * 收藏笔记
     */
    @POST
    @Path("/notes/{id}/collect")
    Result<Void> collectNote(
            @PathParam("id") String id
);

    /**
     * 取消收藏
     */
    @POST
    @Path("/notes/{id}/uncollect")
    Result<Void> uncollectNote(
            @PathParam("id") String id
);
} 