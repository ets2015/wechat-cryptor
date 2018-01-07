# wechat-cryptor
微信消息加解密库

### 安装依赖
- git clone 项目
- mvn clean install
- 添加以下依赖到你的项目：
```xml
        <dependency>
            <groupId>com.qq</groupId>
            <artifactId>wechat-cryptor</artifactId>
            <version>1.8</version>
        </dependency>
```

### 使用方法
- 构造加解密器
```java
  public static WechatCryptor buildIfNotExists(String appid, String token, String encodingAesKey) throws AesException;
```
- 解密
```java
public String decode(InputStream inputStream, String msgSignature, String timestamp, String nonce) throws IOException, AesException;
```
-加密
```java
 public String encode(String replyMsg) throws AesException;
```

