package org.tsukilc.api.file;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.tsukilc.api.file.dto.FileUploadResult;
import org.tsukilc.api.file.dto.UploadCallbackParams;
import org.tsukilc.api.file.dto.UploadUrlRequest;
import org.tsukilc.api.file.dto.UploadUrlResponse;
import org.tsukilc.common.core.Result;

/**
 * 文件服务接口
 */
@RequestMapping("/api/file")
public interface FileService {
    
    /**
     * 获取文件上传URL (预签名URL)
     * @param request 上传文件信息请求
     * @return 预签名URL响应
     */
    @PostMapping("upload/get-url")
    Result<UploadUrlResponse> getUploadUrl(@RequestBody UploadUrlRequest request);
    
    /**
     * 上传完成回调
     * @param params 上传回调参数
     * @return 处理结果
     */
    @PostMapping("upload/callback")
    Result<FileUploadResult> uploadCallback(@RequestBody UploadCallbackParams params);
} 