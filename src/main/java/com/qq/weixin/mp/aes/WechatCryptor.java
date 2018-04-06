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

    private static final int INITIAL_CRYPTOR_SIZE = 3;

    private WechatCryptor(String appid, String token, String encodingAesKey) throws AesException {
        this.wxBizMsgCrypt = new WXBizMsgCrypt(token, encodingAesKey, appid);
    }

    private WechatCryptor() {
    }


    private static Map<String, WechatCryptor> cryptorMap = new HashMap<>(INITIAL_CRYPTOR_SIZE);

    public static WechatCryptor get(String appid) {
        return cryptorMap.get(appid);
    }

    /**
     * 如果不存在加解密器则构造加解密器
     *
     * @param appid
     * @param token
     * @param encodingAesKey
     * @return
     * @throws AesException
     */
    public static WechatCryptor buildIfNotExists(String appid, String token, String encodingAesKey) throws AesException {
        if (get(appid) == null) {
            synchronized (WechatCryptor.class) {
                if (get(appid) == null) {
                    return put(appid, token, encodingAesKey);
                }
            }
        }
        return get(appid);
    }


    public static WechatCryptor put(String appid, String token, String encodingAesKey) throws AesException {
        WechatCryptor wechatCryptor = new WechatCryptor(appid, token, encodingAesKey);
        cryptorMap.put(appid, wechatCryptor);
        return wechatCryptor;
    }

    /**
     * 解密
     *
     * @return
     */
    public String decode(InputStream inputStream, String msgSignature, String timestamp, String nonce) {
        StringBuilder sb = new StringBuilder();
        try (
                InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader br = new BufferedReader(isr)
        ) {
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
        } catch (IOException | AesException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    /**
     * 加密
     *
     * @param replyMsg
     * @return
     */
    public String encode(String replyMsg) {
        if (log.isDebugEnabled()) {
            log.debug("encode reply message => {}", replyMsg);
        }
        try {
            String cryptoReplyMsg = this.wxBizMsgCrypt.encryptMsg(replyMsg, String.valueOf(System.currentTimeMillis()), UUID.randomUUID().toString().replace("-", ""));
            if (log.isDebugEnabled()) {
                log.debug("crypto reply message => {}", cryptoReplyMsg);
            }
            return cryptoReplyMsg;
        } catch (AesException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

}
