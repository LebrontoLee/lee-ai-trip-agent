package com.lee.leeaitripagent.tools;

import cn.hutool.core.io.FileUtil;
import com.lee.leeaitripagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 文件操作工具类（提供文件读写功能）
 */
public class FileOperationTool {
    private final String FILE_DIR = FileConstant.FILE_SAVE_PATH + "/file";

    @Tool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "Name of a file to read") String filename) {
        String filepath = FILE_DIR+"/"+filename;
        try{
            return FileUtil.readUtf8String(filepath);
        } catch(Exception e){
            return "Error reading file:" + e.getMessage();
        }
    }

    @Tool(description = "Write content to a file")
    public String writeFile(@ToolParam(description = "Name of the file to write") String filename,
                            @ToolParam(description = "Content to write to the file") String content) {
        String filepath = FILE_DIR+"/"+filename;
        try{
            // 创建目录
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content, filepath);
            return "Successfully wrote to the file:" + filepath;

        } catch(Exception e){
            return "Error writing to the file:" + e.getMessage();
        }
    }
}
