package org.tsukilc.fileservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.tsukilc.api.file.dto.FileUploadResult;
import org.tsukilc.api.file.dto.UploadCallbackParams;
import org.tsukilc.api.file.dto.UploadUrlRequest;
import org.tsukilc.api.file.dto.UploadUrlResponse;
import org.tsukilc.common.core.Result;
import org.tsukilc.fileservice.service.FileServiceImpl;

/**
 * 文件服务控制器
 */
@RestController
@RequiredArgsConstructor
public class FileController {

    private final FileServiceImpl fileService;
    
    /**
     * 获取上传URL
     */
    @PostMapping("/api/file/upload/get-url")
    public Result<UploadUrlResponse> getUploadUrl(@RequestBody UploadUrlRequest request) {
        return fileService.getUploadUrl(request);
    }
    
    /**
     * 上传完成回调
     */
    @PostMapping("/api/file/upload/callback")
    public Result<FileUploadResult> uploadCallback(@RequestBody UploadCallbackParams params) {
        return fileService.uploadCallback(params);
    }
    
    /**
     * 获取文件访问URL（临时URL）
     */
    @GetMapping("/api/file/get-access-url")
    public Result<String> getFileAccessUrl(@RequestParam String filePath) {
        return fileService.getFileAccessUrl(filePath);
    }
} 