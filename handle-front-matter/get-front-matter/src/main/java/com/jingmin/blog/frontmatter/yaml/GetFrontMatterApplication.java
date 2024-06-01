package com.jingmin.blog.frontmatter.yaml;

import com.jingmin.blog.frontmatter.yaml.util.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.io.FileUtils;
import org.commonmark.Extension;
import org.commonmark.ext.front.matter.YamlFrontMatterExtension;
import org.commonmark.ext.front.matter.YamlFrontMatterVisitor;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.markdown.MarkdownRenderer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class GetFrontMatterApplication {

    private static final List<Extension> EXTENSIONS = Collections.singletonList(YamlFrontMatterExtension.create());
    private static final Parser PARSER = Parser.builder().extensions(EXTENSIONS).build();
    private static final MarkdownRenderer RENDERER = MarkdownRenderer.builder().extensions(EXTENSIONS).build();


    private Properties properties;

    //private String accessKey;
    //
    //private String secretKey;
    //
    //private String accessToken;

    public GetFrontMatterApplication() throws IOException {
        try (InputStream is = this.getClass().getClassLoader().getResourceAsStream("application.properties");
             Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            properties = new Properties();
            properties.load(reader);
            //accessKey = System.getenv("BAIDU_ACCESS_KEY") != null ? System.getenv("BAIDU_ACCESS_KEY"): System.getProperty("baidu.accessKey", properties.getProperty("baidu.bce.nlp.blog-summary.api-key"));
            //secretKey = System.getenv("BAIDU_SECRET_KEY") != null ? System.getenv("BAIDU_SECRET_KEY"): System.getProperty("baidu.secretKey", properties.getProperty("baidu.bce.nlp.blog-summary.secret-key"));
            //accessToken = System.getenv("BAIDU_ACCESS_TOKEN") != null? System.getenv("BAIDU_ACCESS_TOKEN"): System.getProperty("baidu.accessToken");
        }
    }

    public static void main(String[] args) throws Exception {
        GetFrontMatterApplication application = new GetFrontMatterApplication();
        application.run(args);
    }

    //@Override
    public void run(String... args) throws Exception {
        CommandLine commandLine = CommandLineParser.parseArgs(args);
        String yamlKey = null;
        for (Option option : commandLine.getOptions()) {
            String key = option.getKey();
            String value = option.getValue(key);
            if ("k".equals(key)) {
                yamlKey = value;
            }
        }
        String filePath = commandLine.getArgs()[0];
        File file = new File(filePath);
        handleFile(file, yamlKey);

    }

    /**
     * 处理单个markdown文件
     */
    private void handleFile(File file, String yamlKey) throws IOException {
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

        //解析
        Node document = PARSER.parse(content);

        //空文档，忽略
        if (document == null || document.getFirstChild() == null) {
            return;
        }

        if(yamlKey == null || yamlKey.isEmpty()){
            System.err.println("YAML Key is empty");
        }

        YamlFrontMatterVisitor visitor = new YamlFrontMatterVisitor();
        document.accept(visitor);

        Map<String, List<String>> yamlFrontMatter = visitor.getData();

        List<String> strings = yamlFrontMatter.get(yamlKey);
        if(strings != null){
            for(String str : strings) {
                //todo 这里逻辑有点问题, 暂不支持带换行符的内容
                System.out.println(str);
            }
        }
    }

}
