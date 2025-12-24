package com.li.lostbackend.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.li.lostbackend.common.api.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/api/file")
public class FileController {

    // 获取当前项目根目录
    private static final String PROJECT_PATH = System.getProperty("user.dir");
    // 图片存储路径：项目目录/files/
    private static final String UPLOAD_PATH = PROJECT_PATH + File.separator + "files" + File.separator;

    /**
     * 文件上传接口
     * @param file 前端传来的文件对象
     * @return 文件的访问路径 (URL)
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam MultipartFile file) {
        if (file.isEmpty()) {
            return Result.failed("上传文件不能为空");
        }

        // 1. 获取原文件名和后缀
        String originalFilename = file.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);

        // 2. 生成唯一文件名 (防止文件名冲突覆盖)
        String fileName = IdUtil.simpleUUID() + "." + suffix;

        // 3. 创建文件夹 (如果不存在)
        File saveFile = new File(UPLOAD_PATH + fileName);
        if (!saveFile.getParentFile().exists()) {
            saveFile.getParentFile().mkdirs();
        }

        try {
            // 4. 保存文件到本地磁盘
            file.transferTo(saveFile);

            // 5. 返回文件的下载/访问 URL
            // 这里我们生成一个指向下面 download 接口的链接
            String url = "http://localhost:8088/api/file/download/" + fileName;
            return Result.success(url);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            return Result.failed("文件上传失败");
        }
    }

    /**
     * 文件下载/查看接口 (浏览器通过这个接口看图片)
     */
    @RequestMapping("/download/{fileName}")
    public void download(@org.springframework.web.bind.annotation.PathVariable String fileName, HttpServletResponse response) {
        try {
            // 读取本地文件
            byte[] bytes = FileUtil.readBytes(UPLOAD_PATH + fileName);
            // 写出给浏览器
            OutputStream os = response.getOutputStream();
            os.write(bytes);
            os.flush();
            os.close();
        } catch (Exception e) {
            log.error("文件读取失败", e);
        }
    }
}