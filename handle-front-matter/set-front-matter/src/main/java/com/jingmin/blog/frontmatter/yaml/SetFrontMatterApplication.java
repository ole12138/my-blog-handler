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
import java.nio.file.Path;
import java.util.*;


public class SetFrontMatterApplication {

    private static final List<Extension> EXTENSIONS = Collections.singletonList(YamlFrontMatterExtension.create());
    private static final Parser PARSER = Parser.builder().extensions(EXTENSIONS).build();
    private static final MarkdownRenderer RENDERER = MarkdownRenderer.builder().extensions(EXTENSIONS).build();


    private Properties properties;

    //private String accessKey;
    //
    //private String secretKey;
    //
    //private String accessToken;

    public SetFrontMatterApplication() throws IOException {
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
        SetFrontMatterApplication application = new SetFrontMatterApplication();
        application.run(args);
    }

    //@Override
    public void run(String... args) throws Exception {
        CommandLine commandLine = CommandLineParser.parseArgs(args);
        String yamlKey = null;
        List<String> yamlValues = new ArrayList<>();
        for (Option option : commandLine.getOptions()) {
            String key = option.getKey();
            String value = option.getValue(key);
            if ("k".equals(key)) {
                if(yamlKey != null) {
                    System.err.println("Only one key is allowed");
                    System.exit(1);
                }
                yamlKey = value;
            }
            if ("v".equals(key)) {
                yamlValues.add(value);
            }
        }

        if(yamlKey == null || yamlKey.isEmpty()){
            System.err.println("YAML Key is empty");
            System.exit(1);
        }

        String filePath = commandLine.getArgs()[0];
        File file = Path.of(filePath).toFile();
        handleFile(file, yamlKey, yamlValues);

    }

    /**
     * 处理单个markdown文件
     */
    private void handleFile(File file, String yamlKey, List<String> values) throws IOException {
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
        //String title = file.getName().replaceAll("\\.md$", "").replaceAll("\\.markdown$", "");

        //解析
        Node document = PARSER.parse(content);
        //空文档，忽略
        if (document == null || document.getFirstChild() == null) {
            return;
        }

        YamlFrontMatterVisitor visitor = new YamlFrontMatterVisitor();
        document.accept(visitor);
        Map<String, List<String>> yamlFrontMatter = visitor.getData();

        //向yaml front matter中添加 key-values
        yamlFrontMatter.put(yamlKey, values);

        String frontMatterStr = printYamlFrontMatter(yamlFrontMatter);

        String contentWithoutFrontMatter = RENDERER.render(document);
        FileUtils.writeStringToFile(file, frontMatterStr + contentWithoutFrontMatter, "UTF-8");
    }

    private String printYamlFrontMatter(Map<String, List<String>> yamlFrontMatter) {
        StringBuilder sb = new StringBuilder();
        sb.append("---\n");
        if(yamlFrontMatter != null) {
            for (Map.Entry<String, List<String>> entry : yamlFrontMatter.entrySet()) {
                String key = entry.getKey();
                List<String> values = entry.getValue();
                sb.append(key).append(": ");
                if (values == null) {
                    sb.append("\n");
                } else if (values.size() == 1) {
                    sb.append(values.get(0)).append("\n");
                } else {
                    sb.append("\n");
                    for (String value : values) {
                        sb.append("  - ").append(value).append("\n");
                    }
                }
            }
        }

        sb.append("---\n");
        return sb.toString();
    }

}
