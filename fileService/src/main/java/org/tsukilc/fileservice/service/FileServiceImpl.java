package org.tsukilc.fileservice.service;

import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.tsukilc.api.file.FileService;
import org.tsukilc.api.file.dto.FileUploadResult;
import org.tsukilc.api.file.dto.UploadCallbackParams;
import org.tsukilc.api.file.dto.UploadUrlRequest;
import org.tsukilc.api.file.dto.UploadUrlResponse;
import org.tsukilc.common.core.Result;
import org.tsukilc.common.exception.BusinessException;
import org.tsukilc.common.util.UserDetail;
import org.tsukilc.fileservice.entity.FileInfo;
import org.tsukilc.fileservice.mapper.FileInfoMapper;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 文件服务实现
 */
@DubboService
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final S3Presigner s3Presigner;
    private final FileInfoMapper fileInfoMapper;

    @Value("${s3.bucket}")
    private String bucketName;
    
    @Value("${s3.url-expiration:600}")
    private long urlExpirationSeconds;
    
    @Value("${s3.public-endpoint}")
    private String publicEndpoint;
    
    @Value("${s3.access-url-expiration:3600}")
    private long accessUrlExpirationSeconds;

    @Override
    public Result<UploadUrlResponse> getUploadUrl(UploadUrlRequest request) {
        try {
            // 验证请求参数
            if (request.getFileName() == null || request.getFileType() == null || request.getFileSize() == null) {
                throw new BusinessException("文件信息不完整");
            }
            
            // 生成唯一文件路径
            String fileKey = generateFileKey(request);

            // 创建PUT预签名请求（而不是GET请求）
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(request.getFileType())
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(urlExpirationSeconds))
                    .putObjectRequest(putObjectRequest)
                    .build();

            // 获取预签名URL
            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
            String presignedUrl = presignedRequest.url().toString();

            // 构建响应
            UploadUrlResponse response = new UploadUrlResponse();
            response.setUploadUrl(presignedUrl);
            response.setFileUrl(String.format("%s/%s/%s", publicEndpoint, bucketName, fileKey));
            response.setFilePath(fileKey);
            response.setExpiresIn(urlExpirationSeconds);
            
            return Result.success(response);
        } catch (BusinessException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail("获取上传URL失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<FileUploadResult> uploadCallback(UploadCallbackParams params) {
        try {
            // 验证参数
            if (params.getFileUrl() == null) {
                return Result.fail("文件路径或URL不能为空");
            }
            
            // 创建文件信息记录
            FileInfo fileInfo = new FileInfo();
            fileInfo.setOriginalName(params.getOriginalFileName());
            fileInfo.setFileName(params.getOriginalFileName());
            fileInfo.setUserId(UserDetail.getUserId());
            fileInfo.setUrl(params.getFileUrl());
            fileInfo.setPath(params.getFilePath());  // 保存文件路径，用于后续获取临时URL
            fileInfo.setSize(params.getFileSize());
            fileInfo.setType(params.getFileType());
            fileInfo.setFileType(params.getCategory());
            fileInfo.setExtension(extractExtension(params.getOriginalFileName()));
            fileInfo.setCreatedAt(LocalDateTime.now());
            
            // 保存文件信息到数据库
            fileInfoMapper.insert(fileInfo);
            
            // 构建响应
            FileUploadResult result = new FileUploadResult();
            result.setUrl(params.getFileUrl());
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.fail("文件上传回调处理失败: " + e.getMessage());
        }
    }
    
    @Override
    public Result<String> getFileAccessUrl(String filePath) {
        try {
            if (filePath == null || filePath.trim().isEmpty()) {
                return Result.fail("文件路径不能为空");
            }
            
            // 创建用于获取对象的请求
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();
            
            // 创建预签名请求，设置有效期
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(accessUrlExpirationSeconds))
                    .getObjectRequest(getObjectRequest)
                    .build();
            
            // 获取预签名URL
            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            String presignedUrl = presignedRequest.url().toString();
            
            return Result.success(presignedUrl);
        } catch (Exception e) {
            return Result.fail("获取文件访问URL失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成唯一的文件键
     */
    private String generateFileKey(UploadUrlRequest request) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String filename = request.getFileName();
        String extension = "";
        
        // 获取文件扩展名
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex >= 0) {
            extension = filename.substring(lastDotIndex);
        }
        
        // 确定分类目录
        String category = request.getCategory();
        if (category == null || category.trim().isEmpty()) {
            category = "default";
        }
        
        return String.format("%s/%s%s", category, uuid, extension);
    }
    
    /**
     * 从文件名中提取扩展名
     */
    private String extractExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex >= 0) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }
} 