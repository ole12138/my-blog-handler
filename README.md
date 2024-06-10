# my-blog-handler

文章提取关键字,
处理yaml front matter信息,
markdown导出为html,
文章上传到指定博客网站等

## 文章提取关键字

```shell
# build
mvn clean package
# run
/usr/lib/jvm/java-22-openjdk/bin/java -jar ./extract-keyword/target/extract-keyword-1.0-SNAPSHOT.jar ./test.md
```

Of course, You could build a docker image.

```shell
# build
docker build -t w784319947/blog-extract-keyword --target extract-keyword .
# ls
docker image ls
# run
docker run --rm -v ./test.md:/test.md w784319947/blog-extract-keyword /test.md

# push (Optional)
docker login
#your hub.docker.com username
#your hub.docker.com password

# create a repository on hub.docker.com, name with: my-blog-handler
docker image push w784319947/blog-extract-keyword
```

## 获取markdown中yaml front matter中的指定key

注意: 暂不支持对应的值有换行的情况

```shell
# build
mvn clean package
# run
/usr/lib/jvm/java-22-openjdk/bin/java -jar ./handle-front-matter/get-front-matter/target/get-front-matter-1.0-SNAPSHOT.jar -k uuid ./test.md 
/usr/lib/jvm/java-22-openjdk/bin/java -jar ./handle-front-matter/get-front-matter/target/get-front-matter-1.0-SNAPSHOT.jar -k keywords ./test.md
/usr/lib/jvm/java-22-openjdk/bin/java -jar ./handle-front-matter/get-front-matter/target/get-front-matter-1.0-SNAPSHOT.jar -k XXXXO ./test.md 
```

Of course, You could build a docker image.

```shell
# build
docker build -t w784319947/blog-get-front-matter --target get-front-matter .
# ls
docker image ls
# run
docker run --rm -v ./test.md:/test.md w784319947/blog-get-front-matter -k uuid /test.md
# push(Optional)
docker image push w784319947/blog-get-front-matter
```

## 设置markdown中yaml front matter中的指定key

```shell
# build
mvn clean package
# run
# 设置key
/usr/lib/jvm/java-22-openjdk/bin/java -jar ./handle-front-matter/set-front-matter/target/set-front-matter-1.0-SNAPSHOT.jar -k testKey -v value1 -v value2 -v 312 ./test.md
# 查看key
/usr/lib/jvm/java-22-openjdk/bin/java -jar ./handle-front-matter/get-front-matter/target/get-front-matter-1.0-SNAPSHOT.jar ./test.md -k testKey
# 清理key
/usr/lib/jvm/java-22-openjdk/bin/java -jar ./handle-front-matter/set-front-matter/target/set-front-matter-1.0-SNAPSHOT.jar -k testKey ./test.md
```

Of course, You could build a docker image.

```shell
# build
docker build -t w784319947/blog-set-front-matter --target set-front-matter .
# ls
docker image ls
# run
docker run --rm -v ./test.md:/test.md w784319947/blog-set-front-matter -k key1 -v value1 -v value2 /test.md
# push(Optional)
docker image push w784319947/blog-set-front-matter
```

## markdown 导出为html

使用第三方工具pandoc, 需要下载安装, 放置到可执行路径下

```shell
temp=./test.md
mkdir -p out/${temp%/*}
# 默认是不加head/footer和样式的, 若有需要,可以添加 --standalone标记
pandoc --mathjax -f markdown -t html $temp -o out/${temp%.*}.html
```

pandoc is an open source tool. There are already official docker images.

Reference: https://pandoc.org/installing.html#docker
Reference: https://github.com/pandoc/dockerfiles

Just:

```shell
docker run --rm --volume ./test.md:/test.md --volume ./out:/out pandoc/latex -f markdown -t html /test.md -o /out/test.html
```

## 文章上传到wordpress

````shell
# build
mvn clean package
# run
WORDPRESS_HOST=your.wordpress.domain \
WORDPRESS_USER=your_your_name \
WORDPRESS_PASSWORD=your_password \
/usr/lib/jvm/java-22-jdk/bin/java -jar ./upload-blog/upload-wordpress/target/upload-wordpress-1.0-SNAPSHOT.jar -k keyword1 -k keyword2 -k keyword3 -u 60754177-c85b-4458-a69f-dafffb9c1610 ./out/test.html 2>/dev/null

````


Of course, You could build a docker image.

```shell
# build
docker build -t w784319947/blog-upload-wordpress --target upload-wordpress .
# ls
docker image ls
# run
docker run --rm -e WORDPRESS_HOST=your.wordpress.cn -v ./out:/out w784319947/blog-upload-wordpress  -k test1 -k test2 -u 60754177-c85b-4458-a69f-dafffb9c1610 /out/test.html
# push(Optional)
docker image push w784319947/blog-upload-wordpress
```

## 启动整个工具链

```shell
# build
mvn clean package
# run
WORDPRESS_HOST=your.wordpress.domain \
WORDPRESS_USER=your_your_name \
WORDPRESS_PASSWORD=your_password \
./script/test.sh path_of_md_dir_or_file temp_path_of_html_dir
```



Of course, You could build a docker image. All in one image:

```shell
# build
#docker build --build-arg HTTP_PROXY=http://192.168.1.7:8889 --build-arg HTTPS_PROXY=http://192.168.1.7:8889 -t w784319947/blog-handler .
docker build -t w784319947/blog-handler .
# ls
docker image ls
# run
docker run --rm -e WORDPRESS_HOST=your.wordpress.domain -v /home/wangjm/Nextcloud/mynotebook:/workdir/input w784319947/blog-handler
# push(Optional)
docker image push w784319947/blog-handler
```

//todo It seems that ctrl+c would not be handled. Should be fixed.


