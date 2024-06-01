---
keywords: 
  - neo4j
  - match
  - test
  - https
scores: 
  - 0.8909031748771668
  - 0.8830794095993042
  - 0.8486291766166687
  - 0.8131684064865112
uuid: 60754177-c85b-4458-a69f-dafffb9c1610
title: test
testKey: 
---
# neo4j基本使用

转载: [手把手教你快速入门知识图谱 - Neo4J教程](https://zhuanlan.zhihu.com/p/88745411)

neo4j-desktop 启动本地库之后, 也有入门guide, 可以一步步学习.

### 前言

今天，我们来聊一聊知识图谱中的Neo4J。首先，什么是知识图谱？先摘一段百度百科：

> 知识图谱（Knowledge Graph），在图书情报界称为知识域可视化或知识领域映射地图，是显示知识发展进程与结构关系的一系列各种不同的图形，用 可视化技术描述知识资源及其载体，挖掘、分析、 构建、绘制和显示知识及它们之间的相互联系。 知识图谱是通过将应用数学、 图形学、信息可视化技术、 信息科学等学科的理论与方法与计量学引文分析、共现分析等方法结合，并利用可视化的图谱形象地展示学科的核心结构、发展历史、 前沿领域以及整体知识架构达到多学科融合目的的现代理论。它能为学科研究提供切实的、有价值的参考。

简单说来，知识图谱就是通过不同知识的关联性形成一个网状的知识结构，而这个知识结构，恰好就是人工智能AI的基石。当前AI领域热门的计算机图像、语音识别甚至是NLP，其实都是AI的`感知`能力，真正AI的`认知`能力，就要靠知识图谱。

知识图谱目前的应用主要在搜索、智能问答、推荐系统等方面。知识图谱的建设，一般包括数据获取、实体识别和关系抽取、数据存储、图谱应用都几个方面。本文着眼于数据存储这块，给大家一个Neo4J的快速教程。

___

#### Neo4J简介

知识图谱由于其数据包含实体、属性、关系等，常见的关系型数据库诸如MySQL之类不能很好的体现数据的这些特点，因此知识图谱数据的存储一般是采用图数据库（Graph Databases）。而[Neo4j](https://link.zhihu.com/?target=https%3A//neo4j.com/)是其中最为常见的图数据库。

#### Neo4J安装

首先在 [https://neo4j.com/download/](https://link.zhihu.com/?target=https%3A//neo4j.com/download/) 下载Neo4J。Neo4J分为社区版和企业版，企业版在横向扩展、权限控制、运行性能、HA等方面都比社区版好，适合正式的生产环境，普通的学习和开发采用免费社区版就好。

在Mac或者Linux中，安装好jdk后，直接解压下载好的Neo4J包，运行`bin/neo4j start`即可

### Neo4J使用

Neo4J提供了一个用户友好的web界面，可以进行各项配置、写入、查询等操作，并且提供了可视化功能。类似ElasticSearch一样，我个人非常喜欢这种开箱即用的设计。

打开浏览器，输入`http://127.0.0.1:7474/browser/`，如下图所示，界面最上方就是交互的输入框。

![img](https://picgo12138.oss-cn-hangzhou.aliyuncs.com/md/2024-05-25-22-17-54-a2886f9c73c6086a07eb64d97b122249-v2-943bff526d4b0fdfe80d3fca706a7b64_720w-f9135e.webp)

### Cypher查询语言

Cypher是Neo4J的声明式图形查询语言，允许用户不必编写图形结构的遍历代码，就可以对图形数据进行高效的查询。Cypher的设计目的类似SQL，适合于开发者以及在数据库上做点对点模式（ad-hoc）查询的专业操作人员。其具备的能力包括： - 创建、更新、删除节点和关系 - 通过模式匹配来查询和修改节点和关系 - 管理索引和约束等

___

### Neo4J实战教程

直接讲解Cypher的语法会非常枯燥，本文通过一个实际的案例来一步一步教你使用Cypher来操作Neo4J。

这个案例的节点主要包括人物和城市两类，人物和人物之间有朋友、夫妻等关系，人物和城市之间有出生地的关系。

1. 首先，我们删除数据库中以往的图，确保一个空白的环境进行操作：

```text
MATCH (n) DETACH DELETE n
```

这里，`MATCH`是**匹配**操作，而小括号()代表一个**节点**node（可理解为括号类似一个圆形），括号里面的n为**标识符**。

2. 接着，我们创建一个人物节点：

```text
CREATE (n:Person {name:'John'}) RETURN n
```

`CREATE`是**创建**操作，`Person`是**标签**，代表节点的类型。花括号{}代表节点的**属性**，属性类似Python的字典。这条语句的含义就是创建一个标签为Person的节点，该节点具有一个name属性，属性值是John。

如图所示，在Neo4J的界面上可以看到创建成功的节点。

![img](https://picgo12138.oss-cn-hangzhou.aliyuncs.com/md/2024-05-25-22-18-00-3de3c257ca2c06bcab7555e15dd52a4d-v2-4ebdfd370af2f4145fb9142760da66e1_720w-75a7f1.webp)

3. 我们继续来创建更多的人物节点，并分别命名：

```text
CREATE (n:Person {name:'Sally'}) RETURN n
CREATE (n:Person {name:'Steve'}) RETURN n
CREATE (n:Person {name:'Mike'}) RETURN n
CREATE (n:Person {name:'Liz'}) RETURN n
CREATE (n:Person {name:'Shawn'}) RETURN n
```

如图所示，6个人物节点创建成功

![img](https://picgo12138.oss-cn-hangzhou.aliyuncs.com/md/2024-05-25-22-18-04-31d73d84df176721b70bbafca892404c-v2-0dfa4f82a0f1b710f814d653bad85568_720w-f2f5d7.webp)

4. 接下来创建地区节点

```text
CREATE (n:Location {city:'Miami', state:'FL'})
CREATE (n:Location {city:'Boston', state:'MA'})
CREATE (n:Location {city:'Lynn', state:'MA'})
CREATE (n:Location {city:'Portland', state:'ME'})
CREATE (n:Location {city:'San Francisco', state:'CA'})
```

可以看到，节点类型为Location，属性包括city和state。

如图所示，共有6个人物节点、5个地区节点，Neo4J贴心地使用不用的颜色来表示不同类型的节点。

![img](https://picgo12138.oss-cn-hangzhou.aliyuncs.com/md/2024-05-25-22-18-09-8956f599fbbf4b7de8b87c6f11ff2f29-v2-39890877ca54748f24ab4c3211d11510_720w-5e84b7.webp)

5. 接下来创建关系

```text
MATCH (a:Person {name:'Liz'}), 
      (b:Person {name:'Mike'}) 
MERGE (a)-[:FRIENDS]->(b)
```

这里的方括号`[]`即为关系，`FRIENDS`为关系的类型。注意这里的箭头`-->`是有方向的，表示是从a到b的关系。 如图，Liz和Mike之间建立了`FRIENDS`关系，通过Neo4J的可视化很明显的可以看出：

![img](https://picgo12138.oss-cn-hangzhou.aliyuncs.com/md/2024-05-25-22-18-15-f9c1d846f5a1d7294ea2008d426ab6dd-v2-dcc171700a1fd012878d52606188f89a_720w-31d97b.webp)

6. 关系也可以增加属性

```text
MATCH (a:Person {name:'Shawn'}), 
      (b:Person {name:'Sally'}) 
MERGE (a)-[:FRIENDS {since:2001}]->(b)
```

在关系中，同样的使用花括号{}来增加关系的属性，也是类似Python的字典，这里给FRIENDS关系增加了since属性，属性值为2001，表示他们建立朋友关系的时间。

7. 接下来增加更多的关系

```text
MATCH (a:Person {name:'Shawn'}), (b:Person {name:'John'}) MERGE (a)-[:FRIENDS {since:2012}]->(b)
MATCH (a:Person {name:'Mike'}), (b:Person {name:'Shawn'}) MERGE (a)-[:FRIENDS {since:2006}]->(b)
MATCH (a:Person {name:'Sally'}), (b:Person {name:'Steve'}) MERGE (a)-[:FRIENDS {since:2006}]->(b)
MATCH (a:Person {name:'Liz'}), (b:Person {name:'John'}) MERGE (a)-[:MARRIED {since:1998}]->(b)
```

如图，人物关系图已建立好，有点图谱的意思了吧？

![img](https://picgo12138.oss-cn-hangzhou.aliyuncs.com/md/2024-05-25-22-18-20-5f4aa80d506c263a37ceb3b776d03440-v2-955b7f17c2c3e4be847726228006a005_720w-119123.webp)

8. 然后，我们需要建立不同类型节点之间的关系-人物和地点的关系

```text
MATCH (a:Person {name:'John'}), (b:Location {city:'Boston'}) MERGE (a)-[:BORN_IN {year:1978}]->(b)
```

这里的关系是BORN\_IN，表示出生地，同样有一个属性，表示出生年份。

如图，在人物节点和地区节点之间，人物出生地关系已建立好。

![img](https://picgo12138.oss-cn-hangzhou.aliyuncs.com/md/2024-05-25-22-18-24-de75b95de6095e5feb0f896dc3011264-v2-b968613a93fdcddceb860e386a953699_720w-bd9e28.webp)

9. 同样建立更多人的出生地

```text
MATCH (a:Person {name:'Liz'}), (b:Location {city:'Boston'}) MERGE (a)-[:BORN_IN {year:1981}]->(b)
MATCH (a:Person {name:'Mike'}), (b:Location {city:'San Francisco'}) MERGE (a)-[:BORN_IN {year:1960}]->(b)
MATCH (a:Person {name:'Shawn'}), (b:Location {city:'Miami'}) MERGE (a)-[:BORN_IN {year:1960}]->(b)
MATCH (a:Person {name:'Steve'}), (b:Location {city:'Lynn'}) MERGE (a)-[:BORN_IN {year:1970}]->(b)
```

建好以后，整个图如下

![img](https://picgo12138.oss-cn-hangzhou.aliyuncs.com/md/2024-05-25-22-18-29-08f10317a7c55062d4b4bbf9023d6647-v2-ecbdeac21cc6dff47fb1b824757b7d17_720w-50a937.webp)

10. 至此，知识图谱的数据已经插入完毕，可以开始做查询了。我们查询下所有在Boston出生的人物

```text
MATCH (a:Person)-[:BORN_IN]->(b:Location {city:'Boston'}) RETURN a,b
```

结果如图

![img](https://picgo12138.oss-cn-hangzhou.aliyuncs.com/md/2024-05-25-22-18-35-c5fb05163ee6035dd60a43671bbca34e-v2-fa8ab8074aaa811f7c1b95f7bf96f1dd_720w-8f360d.webp)

11. 查询所有对外有关系的节点

```text
MATCH (a)-->() RETURN a
```

注意这里箭头的方向，返回结果不含任何地区节点，因为地区并没有指向其他节点（只是被指向）

![img](https://picgo12138.oss-cn-hangzhou.aliyuncs.com/md/2024-05-25-22-18-40-4375be738c8b4c69cbbf4dcff5f0f95d-v2-d82d57aa81aecc42612d8c464368b75b_720w-051da9.webp)

12. 查询所有有关系的节点

```text
MATCH (a)--() RETURN a
```

结果如图

![img](https://picgo12138.oss-cn-hangzhou.aliyuncs.com/md/2024-05-25-22-18-45-1b268c6b16fc05afffadd33cf80eb304-v2-e34f0e9adb7cefd59e26c16eec88e422_720w-c366f3.webp)

13. 查询所有对外有关系的节点，以及关系类型

```text
MATCH (a)-[r]->() RETURN a.name, type(r)
```

结果如图

![img](https://picgo12138.oss-cn-hangzhou.aliyuncs.com/md/2024-05-25-22-18-49-9a3ed8bc9bd88092a39faf62a9a78607-v2-ac2a3c82ddfef52ee487b7efb59c9548_720w-332ca4.webp)

14. 查询所有有结婚关系的节点

```text
MATCH (n)-[:MARRIED]-() RETURN n
```

结果如图

![img](https://picgo12138.oss-cn-hangzhou.aliyuncs.com/md/2024-05-25-22-18-54-dab9619f312e698c94f34548e7ec8286-v2-e7850b5800fe28e628eda23b25ded3c6_720w-7d5072.webp)

15. 创建节点的时候就建好关系

```text
CREATE (a:Person {name:'Todd'})-[r:FRIENDS]->(b:Person {name:'Carlos'})
```

结果如图

![img](https://picgo12138.oss-cn-hangzhou.aliyuncs.com/md/2024-05-25-22-18-58-8ae6ea4dc4f5ad9545931ded6cf1cade-v2-e0222d0cc457d186ba5a1355f1b3990f_720w-928db2.webp)

16. 查找某人的朋友的朋友

```text
MATCH (a:Person {name:'Mike'})-[r1:FRIENDS]-()-[r2:FRIENDS]-(friend_of_a_friend) RETURN friend_of_a_friend.name AS fofName
```

返回Mike的朋友的朋友：

![img](https://picgo12138.oss-cn-hangzhou.aliyuncs.com/md/2024-05-25-22-19-02-cb18a100500f86e905c5832a4e04105c-v2-8630e2f27bb7bc7551a9699a4a4f6d73_720w-7471e2.webp)

从图上也可以看出，Mike的朋友是Shawn，Shawn的朋友是John和Sally

![img](https://picgo12138.oss-cn-hangzhou.aliyuncs.com/md/2024-05-25-22-19-07-ceaca8dbb4a50c1f5b3d5141dc11e1c7-v2-76ac9c4df2a24228f7d74be0b6bd2ace_720w-ad6e19.webp)

17. 增加/修改节点的属性

```text
MATCH (a:Person {name:'Liz'}) SET a.age=34
MATCH (a:Person {name:'Shawn'}) SET a.age=32
MATCH (a:Person {name:'John'}) SET a.age=44
MATCH (a:Person {name:'Mike'}) SET a.age=25
```

这里，SET表示`修改`操作

18. 删除节点的属性

```text
MATCH (a:Person {name:'Mike'}) SET a.test='test'
MATCH (a:Person {name:'Mike'}) REMOVE a.test
```

删除属性操作主要通过`REMOVE`

19. 删除节点

```text
MATCH (a:Location {city:'Portland'}) DELETE a
```

删除节点操作是`DELETE`

20. 删除有关系的节点

```text
MATCH (a:Person {name:'Todd'})-[rel]-(b:Person) DELETE a,b,rel
```

___

### 总结

本文重点针对常见的知识图谱图数据库Neo4J进行了介绍，并且采用一个实际的案例来说明Neo4J的查询语言Cypher的使用方法。

当然，类似MySQL一样，在实际的生产应用中，除了简单的查询操作会在Neo4J的web页面进行外，一般还是使用Python、Java等的driver来在程序中实现。后续会继续介绍编程语言如何操作Neo4J。

# neo4j cypher语法

官方文档： https://neo4j.com/docs/cypher-manual/5/introduction/
