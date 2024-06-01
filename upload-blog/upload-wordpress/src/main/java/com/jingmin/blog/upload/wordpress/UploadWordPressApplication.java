package com.jingmin.blog.upload.wordpress;

import com.jingmin.blog.upload.wordpress.util.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class UploadWordPressApplication {

    private Properties properties;

    private String accessKey;

    private String secretKey;

    private String accessToken;

    public static void main(String[] args) throws IOException {
        UploadWordPressApplication application = new UploadWordPressApplication();
        application.run(args);
    }

    public void run(String[] args) throws IOException {
        CommandLine commandLine = CommandLineParser.parseArgs(args);
        boolean printAccessToken = false;
        for (Option option : commandLine.getOptions()) {
            String key = option.getKey();
            String value = option.getValue(key);
            if ("p".equals(key)) {
                printAccessToken = true;
            }
        }
        String filePath = commandLine.getArgs()[0];
        File file = new File(filePath);
        handleFile(file, printAccessToken);

    }

    /**
     * 处理单个markdown文件
     */
    private void handleFile(File file, boolean printAccessToken) throws IOException {
        if (!file.exists()) {
            System.err.println("File does not exist");
            return;
        }
        if (!file.isFile()) {
            System.err.println("File is not a file");
        }

        //读取文章内容
        String content = FileUtils.readFileToString(file, "UTF-8");

        //标题
        String title = file.getName().replaceAll("\\.md$", "").replaceAll("\\.markdown$", "");


    }

}
