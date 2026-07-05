package com.blog.controller;

import com.blog.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {
    private static final Set<String> IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");
    private static final Set<String> ATTACHMENT_EXTENSIONS = Set.of(
            ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt", ".zip", ".rar", ".7z"
    );

    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    @Value("${file.upload.base-url:/uploads/}")
    private String baseUrl;

    /**
     * 图片上传
     */
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "images", IMAGE_EXTENSIONS);
    }

    /**
     * 附件上传
     */
    @PostMapping("/attachment")
    public Result<String> uploadAttachment(@RequestParam("file") MultipartFile file) {
        return uploadFile(file, "attachments", ATTACHMENT_EXTENSIONS);
    }

    /**
     * 读取文件，非限制，用于单点返回文件
     * 访问 /upload/file/uploads/images/2026-04-10/xxx.png → 返回该图片
     */
    @GetMapping("/file/**")
    public ResponseEntity<byte[]> serveFile(HttpServletRequest request) {
        String uri = request.getRequestURI();
        // uri: /upload/file/uploads/images/2026-04-10/xxx.png
        // uploadPath: uploads/  实际文件在: server/uploads/images/2026-04-10/xxx.png
        String afterPrefix = uri.substring("/upload/file".length()); // /uploads/images/2026-04-10/xxx.png
        if (!afterPrefix.startsWith("/uploads/")) {
            return ResponseEntity.notFound().build();
        }
        // 去掉 /uploads 前缀，得到相对路径
        String relative = afterPrefix.substring("/uploads".length()); // /images/2026-04-10/xxx.png
        Path root = Paths.get(uploadPath).toAbsolutePath().normalize();
        Path filePath = root.resolve(relative.replaceFirst("^/+", "")).normalize();
        if (!filePath.startsWith(root)) {
            return ResponseEntity.notFound().build();
        }
        File file = filePath.toFile();
        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.notFound().build();
        }
        try {
            byte[] data = Files.readAllBytes(file.toPath());
            String name = file.getName().toLowerCase();
            MediaType mt = MediaType.APPLICATION_OCTET_STREAM;
            if (name.endsWith(".png")) mt = MediaType.IMAGE_PNG;
            else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) mt = MediaType.IMAGE_JPEG;
            else if (name.endsWith(".gif")) mt = MediaType.IMAGE_GIF;
            else if (name.endsWith(".webp")) mt = MediaType.parseMediaType("image/webp");
            return ResponseEntity.ok().contentType(mt).body(data);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 通用文件上传
     */
    private Result<String> uploadFile(MultipartFile file, String subDir, Set<String> allowedExtensions) {
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        // 限制文件大小 10MB
        if (file.getSize() > 10 * 1024 * 1024) {
            return Result.error("文件大小不能超过10MB");
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase(Locale.ROOT);
        }
        if (!allowedExtensions.contains(extension)) {
            return Result.error("不支持的文件类型");
        }
        String filename = UUID.randomUUID().toString() + extension;

        // 保存文件
        try {
            String datePath = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            Path root = Paths.get(uploadPath).toAbsolutePath().normalize();
            Path dirPath = root.resolve(subDir).resolve(datePath).normalize();
            if (!dirPath.startsWith(root)) {
                return Result.error("上传路径不合法");
            }
            Files.createDirectories(dirPath);
            Path filePath = dirPath.resolve(filename).normalize();
            if (!filePath.startsWith(root)) {
                return Result.error("上传路径不合法");
            }
            Files.write(filePath, file.getBytes());
            String url = "/upload/file" + baseUrl + subDir + "/" + datePath + "/" + filename;
            return Result.success(url);
        } catch (IOException e) {
            log.warn("File upload failed", e);
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
