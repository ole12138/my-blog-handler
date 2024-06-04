# my-blog-handler

文章提取关键字,
处理yaml front matter信息,
markdown导出为html,
文章上传到指定博客网站等

## 文章提取关键字

```shell
/usr/lib/jvm/java-22-openjdk/bin/java -jar ./extract-keyword/target/extract-keyword-1.0-SNAPSHOT.jar ./test.md
```

## 获取markdown中yaml front matter中的指定key

注意: 暂不支持对应的值有换行的情况

```shell
/usr/lib/jvm/java-22-openjdk/bin/java -jar ./handle-front-matter/get-front-matter/target/get-front-matter-1.0-SNAPSHOT.jar -k uuid ./test.md 
/usr/lib/jvm/java-22-openjdk/bin/java -jar ./handle-front-matter/get-front-matter/target/get-front-matter-1.0-SNAPSHOT.jar -k keywords ./test.md
/usr/lib/jvm/java-22-openjdk/bin/java -jar ./handle-front-matter/get-front-matter/target/get-front-matter-1.0-SNAPSHOT.jar -k XXXXO ./test.md 
```

## 设置markdown中yaml front matter中的指定key

```shell
# 设置key
/usr/lib/jvm/java-22-openjdk/bin/java -jar ./handle-front-matter/set-front-matter/target/set-front-matter-1.0-SNAPSHOT.jar -k testKey -v value1 -v value2 -v 312 ./test.md
# 查看key
/usr/lib/jvm/java-22-openjdk/bin/java -jar ./handle-front-matter/get-front-matter/target/get-front-matter-1.0-SNAPSHOT.jar ./test.md -k testKey
# 清理key
/usr/lib/jvm/java-22-openjdk/bin/java -jar ./handle-front-matter/set-front-matter/target/set-front-matter-1.0-SNAPSHOT.jar -k testKey ./test.md
```

## markdown 导出为html

使用第三方工具pandoc, 需要下载安装, 放置到可执行路径下

```shell
temp=./test.md
mkdir -p out/${temp%/*}
# 默认是不加head/footer和样式的, 若有需要,可以添加 --standalone标记
pandoc --mathjax -f markdown -t html $temp -o out/${temp%.*}.html
```

## 文章上传到wordpress

````shell
/usr/lib/jvm/java-22-jdk/bin/java -jar ./upload-blog/upload-wordpress/target/upload-wordpress-1.0-SNAPSHOT.jar -k keyword1 -k keyword2 -k keyword3 -u 60754177-c85b-4458-a69f-dafffb9c1610 ./out/test.html 2>/dev/null

````


## 启动整个工具链

```shell
./script/test.sh ./ out
```