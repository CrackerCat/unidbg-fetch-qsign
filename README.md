# unidbg-fetch-qsign

获取QQSign参数通过Unidbg，开放HTTP API。

# 部署方法

解压之后来到解压目录，执行以下命令
```shell
bash bin/unidbg-fetch-qsign --port=8080  --count=1 --library=txlib\8.9.63
```

 - --port=你的端口
 - --count=unidbg实例数量
 - --library=核心so文件位置

# 使用

### 原始energy

```kotlin
# http://127.0.0.1:8080/custom_energy?salt=[SALT HEX]&data=[DATA]
```

### sign

```kotlin
# http://127.0.0.1:8080/sign?uin=[UIN]&qua=V1_AND_SQ_8.9.63_4188_HDBM_T&cmd=[CMD]&seq=[SEQ]&buffer=[BUFFER]
```

### 登录包energy(tlv544)

下面这个只是个例子

```kotlin
# http://127.0.0.1:8080/energy?&version=6.0.0.2534&uin=1234567&guid=ABCDABCDABCDABCDABCDABCDABCDABCD&data=810_f
```