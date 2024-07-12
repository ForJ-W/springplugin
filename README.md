# 概述

通过动态加载spring ApplicationContext来绑定插件

# 使用方式

## 源码调试

1. 启动server
2. 打包demo中的jar
3. 打开swagger文档加载jar
4. 调用jar中的接口



## 命令行测试

``` shell
# 进入项目路径进行打包
mvn -DskipTests=true clean package
# 运行server
java -Dfile.encoding=UTF-8 -jar server/target/server.jar
# 加载mybatisplusdemo
curl -X POST -F "file=@./demo/mybatisplus-demo/springplugin-mybatisplus-demo-server/target/mybatisplusdemo.jar" localhost:8000/pm/load
# 调用curd接口
# 保存
curl -X POST -H "app-meta: mybatisplusdemo" -H "Content-Type: application/json" -d "{\"name\":\"信一子\",\"code\":\"xyz\"}" localhost:8000/mybatisplusdemo/save
# 列表
curl -X GET -H "app-meta: mybatisplusdemo" localhost:8000/mybatisplusdemo/list
# ...
```

