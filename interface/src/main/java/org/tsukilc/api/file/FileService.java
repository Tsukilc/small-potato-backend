package org.tsukilc.api.file;

import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tsukilc.api.file.dto.FileUploadResult;
import org.tsukilc.common.core.Result;

@DubboService
@RequestMapping("/api/upload")
public interface FileService {
    
    /**
     * 上传笔记图片
     */
    @PostMapping("/note-image")
    Result<FileUploadResult> uploadNoteImage(
            @RequestParam("file") MultipartFile file
);
    
    /**
     * 上传用户头像
     */
    @PostMapping("/avatar")
    Result<FileUploadResult> uploadAvatar(
            @RequestParam("file") MultipartFile file
);
    
    /**
     * 上传视频
     */
    @PostMapping("/video")
    Result<FileUploadResult> uploadVideo(
            @RequestParam("file") MultipartFile file
);
} 