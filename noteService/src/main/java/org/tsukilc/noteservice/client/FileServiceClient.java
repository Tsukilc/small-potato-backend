package org.tsukilc.noteservice.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.tsukilc.api.file.FileService;
import org.tsukilc.common.core.Result;

/**
 * 文件服务客户端
 */
@Slf4j
@Component
public class FileServiceClient {

    @DubboReference
    private FileService fileService;
    
    /**
     * 获取文件的临时访问URL
     * @param filePath 文件路径
     * @return 临时访问URL，如果获取失败则返回原路径
     */
    public String getFileAccessUrl(String filePath) {
        if (!StringUtils.hasText(filePath)) {
            return filePath;
        }
        
        try {
            Result<String> result = fileService.getFileAccessUrl(filePath);
            if (result != null && result.isSuccess() && StringUtils.hasText(result.getData())) {
                return result.getData();
            }
        } catch (Exception e) {
            log.error("获取文件临时访问URL失败: {}", filePath, e);
        }
        
        // 如果获取失败，返回原路径
        return filePath;
    }
}