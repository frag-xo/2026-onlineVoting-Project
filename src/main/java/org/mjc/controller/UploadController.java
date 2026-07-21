package org.mjc.controller;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.mjc.dto.DTO;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

/**
 * @Description
 * @Author sgl
 * @Date 2018-05-15 14:04
 */
@CrossOrigin
@RestController
@RequestMapping("/api")
public class UploadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    @GetMapping("/upload")
    public String upload() {
        return "upload";
    }

    @PostMapping("/uploadImg")  //等价下面这条注解
    //@RequestMapping(name="/uploadImg",method = RequestMethod.POST)
    @ResponseBody
    public DTO<String> upload(@RequestParam("file") MultipartFile[] files, HttpServletRequest request, HttpServletResponse response) {//要求前端提交多表单 mutipartForm
        DTO dto = new DTO(403, "");
        boolean flag = true;
        String imageNames = "";
        if (files.length==0) {
            dto.setMsg("上传失败，请选择文件");
        } else {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");// 设置日期格式
            String newFileName = "";
            for(MultipartFile file:files) {
                  newFileName = df.format(new Date());
                newFileName += Math.abs(new Random().nextInt() % 1000);
                try {
                    String fileName = file.getOriginalFilename();
                    String model = Optional.ofNullable(request.getParameter("model")).orElse("temp");//不同的图片 存放不同的目录
                    String ext = StrUtil.split(fileName, ".", 0, true, true).get(1);//例如2.jpg 按.分割 成为一个数组 数组的第2部分就是后缀名
                    newFileName += "." + ext;
                    
                    // 使用当前项目文件夹下的upload目录（相对路径）
                    String projectPath = System.getProperty("user.dir");
                    String parentfile = "c:/upload/" + model;
                    
                    File newfile = new File(parentfile);
                    if (!newfile.exists()) {//model文件夹是否存在不存在 创建
                        newfile.mkdirs();
                    }

                    String fullNewFile = parentfile + "/" + newFileName;
                    //*********************************存放文件到磁盘********************************
                    file.transferTo(new File(fullNewFile));

                    // 返回可通过HTTP访问的URL路径
                    String imageUrl = "/upload/" + model + "/" + newFileName;
                    imageNames += imageUrl + "";
                    System.out.println("上传成功，图片URL: " + imageUrl);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    flag= false;
                    e.printStackTrace();

                }
            }
            if(files.length < 1 ) {
                if (newFileName != null) {
                    System.out.println(newFileName);
                    dto.setMsg("上传成功");
                    dto.setObj(newFileName);
                    dto.setCode(200);
                }
            }else{
                if(flag){
                    System.out.println(imageNames);
                    dto.setMsg("上传成功");
                    dto.setObj(imageNames);
                    dto.setCode(200);
                }
            }
        }
        return dto;
    }


}