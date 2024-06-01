package com.jingmin.blog.keyword.extract;

import com.jingmin.blog.keyword.extract.baidu.BaiduApi;
import com.jingmin.blog.keyword.extract.util.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class ExtractKeywordApplication {

    private Properties properties;

    private String accessKey;

    private String secretKey;

    private String accessToken;

    public ExtractKeywordApplication() throws IOException {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("application.properties");
             Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            properties = new Properties();
            properties.load(reader);
            accessKey = System.getenv("BAIDU_ACCESS_KEY") != null ? System.getenv("BAIDU_ACCESS_KEY"): System.getProperty("baidu.accessKey", properties.getProperty("baidu.bce.nlp.blog-summary.api-key"));
            secretKey = System.getenv("BAIDU_SECRET_KEY") != null ? System.getenv("BAIDU_SECRET_KEY"): System.getProperty("baidu.secretKey", properties.getProperty("baidu.bce.nlp.blog-summary.secret-key"));
            accessToken = System.getenv("BAIDU_ACCESS_TOKEN") != null? System.getenv("BAIDU_ACCESS_TOKEN"): System.getProperty("baidu.accessToken");
        }
    }

    public static void main(String[] args) throws Exception {
        ExtractKeywordApplication application = new ExtractKeywordApplication();
        application.run(args);
    }

    //@Override
    public void run(String... args) throws Exception {
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

        //使用百度智能云-获取token
        String accessToken = this.accessToken != null? this.accessToken: BaiduApi.getToken(accessKey, secretKey);
        if(printAccessToken) {
            //stdout 第一行输出accessToken
            System.out.println(accessToken);
        }
        //使用百度智能云-自然语言处理技术NLP-文章标签接口
        BaiduApi.Keywords keywords = BaiduApi.getKeywords(content, title, accessToken);
        if(keywords != null) {
            //依次打印
            for (BaiduApi.Keywords.Item item : keywords.getItems()) {
                System.out.printf("%s %f\n",item.getTag(), item.getScore());
            }
        }
    }

}
