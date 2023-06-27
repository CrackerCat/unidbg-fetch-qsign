# unidbg-fetch-qsign

获取QQSign参数通过Unidbg，开放HTTP API。

# 部署方法

系统安装jdk或者jre，版本1.8或以上。如果报错找不到类，请尝试1.8或略靠近1.8的版本

>解压后cd到解压目录，执行以下命令启动程序。非Windows系统在执行启动命令时，请使用自己系统平台的文件分割符替换library路径 例如`txlib/8.9.63`
```shell
bash bin/unidbg-fetch-qsign --port=8080  --count=1 --library=txlib\8.9.63
```

 - --port=你的端口
 - --count=unidbg实例数量
 - --library=核心so文件位置

# 使用

### energy api

```http request
# http://127.0.0.1:8080/energy?salt=[SALT HEX]&data=[DATA]
```

### sign api

```http request
# http://127.0.0.1:8080/sign?uin=[UIN]&qua=V1_AND_SQ_8.9.63_4188_HDBM_T&cmd=[CMD]&seq=[SEQ]&buffer=[BUFFER]
```