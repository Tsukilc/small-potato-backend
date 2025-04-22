package org.tsukilc.fileservice.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tsukilc.api.file.FileService;
import org.tsukilc.api.file.dto.FileUploadResult;
import org.tsukilc.common.core.Result;
import org.tsukilc.common.exception.BusinessException;
import org.tsukilc.fileservice.entity.FileInfo;
import org.tsukilc.fileservice.mapper.FileInfoMapper;

import java.time.LocalDateTime;
import java.util.UUID;

@DubboService
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileInfoMapper fileInfoMapper;
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    @Value("${file.cdn-domain}")
    private String cdnDomain;

    @Override
    public Result<FileUploadResult> uploadNoteImage(MultipartFile file) {
        // 模拟上传实现，返回假URL
        FileUploadResult result = new FileUploadResult();
        result.setUrl(cdnDomain + "/images/" + UUID.randomUUID() + ".jpg");
        return Result.success(result);
    }

    @Override
    public Result<FileUploadResult> uploadAvatar(MultipartFile file) {
        // 模拟上传实现，返回假URL
        FileUploadResult result = new FileUploadResult();
        result.setUrl(cdnDomain + "/avatars/" + UUID.randomUUID() + ".jpg");
        return Result.success(result);
    }

    @Override
    public Result<FileUploadResult> uploadVideo(MultipartFile file) {
        // 模拟上传实现，返回假URL
        FileUploadResult result = new FileUploadResult();
        result.setUrl(cdnDomain + "/videos/" + UUID.randomUUID() + ".mp4");
        return Result.success(result);
    }
    
    // 模拟保存文件信息
    private FileInfo saveFileInfo(MultipartFile file, String url, String fileType, String userId) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setOriginalName(file.getOriginalFilename());
        fileInfo.setFileName(UUID.randomUUID().toString());
        fileInfo.setUrl(url);
        fileInfo.setPath(uploadDir + "/" + fileInfo.getFileName());
        fileInfo.setSize(file.getSize());
        fileInfo.setType(file.getContentType());
        fileInfo.setExtension(getFileExtension(file.getOriginalFilename()));
        fileInfo.setMd5("mock_md5");
        fileInfo.setUserId(userId);
        fileInfo.setFileType(fileType);
        fileInfo.setCreatedAt(LocalDateTime.now());
        
        fileInfoMapper.insert(fileInfo);
        
        return fileInfo;
    }
    
    // 获取文件扩展名
    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }
} 