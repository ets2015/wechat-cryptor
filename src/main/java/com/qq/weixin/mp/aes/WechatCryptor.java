package com.qq.weixin.mp.aes;


import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 微信消息解密
 *
 * @author Shinez.
 */

@Slf4j
public class WechatCryptor {


    private WXBizMsgCrypt wxBizMsgCrypt;

    private WechatCryptor(String appid, String token, String encodingAesKey) throws AesException {
        this.wxBizMsgCrypt = new WXBizMsgCrypt(token, encodingAesKey, appid);
    }

    private WechatCryptor() {
    }


    private static Map<String, WechatCryptor> cryptorMap;

    public static WechatCryptor get(String appid) {
        if (cryptorMap == null) {
            return null;
        }
        return cryptorMap.get(appid);
    }

    public static WechatCryptor buildIfNotExists(String appid, String token, String encodingAesKey) throws AesException {
        WechatCryptor wechatCryptor = get(appid);
        if (wechatCryptor == null) {
            return put(appid, token, encodingAesKey);
        }
        return wechatCryptor;
    }


    public static WechatCryptor put(String appid, String token, String encodingAesKey) throws AesException {
        if (cryptorMap == null) {
            cryptorMap = new HashMap<>(5);
        }
        WechatCryptor wechatCryptor = new WechatCryptor(appid, token, encodingAesKey);
        cryptorMap.put(appid, wechatCryptor);
        return wechatCryptor;
    }

    /**
     * 解密
     *
     * @return
     */
    public String decode(InputStream inputStream, String msgSignature, String timestamp, String nonce) throws IOException, AesException {
        StringBuffer sb = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String s;
        while ((s = br.readLine()) != null) {
            sb.append(s);
        }
        String content = sb.toString();
        if (log.isDebugEnabled()) {
            log.debug("input stream content => {}", content);
        }
        String decryptResult = this.wxBizMsgCrypt.decryptMsg(msgSignature, timestamp, nonce, content);
        if (log.isDebugEnabled()) {
            log.debug("decrypt result => {}", decryptResult);
        }
        return decryptResult;
    }

    /**
     * 加密
     *
     * @param replyMsg
     * @return
     */
    public String encode(String replyMsg) throws AesException {
        if (log.isDebugEnabled()) {
            log.debug("encode reply message => {}", replyMsg);
        }
        String cryptoReplyMsg = this.wxBizMsgCrypt.encryptMsg(replyMsg, String.valueOf(System.currentTimeMillis()), UUID.randomUUID().toString().replace("-", ""));
        if (log.isDebugEnabled()) {
            log.debug("crypto reply message => {}", cryptoReplyMsg);
        }
        return cryptoReplyMsg;
    }

}
