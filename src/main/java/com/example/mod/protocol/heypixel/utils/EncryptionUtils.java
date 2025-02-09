package com.example.mod.protocol.heypixel.utils;



import com.example.mod.protocol.heypixel.HeypixelHandler;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class EncryptionUtils {
    public static String decodeBase64(HeypixelHandler manager, String str) throws NoSuchPaddingException, IllegalBlockSizeException, UnsupportedEncodingException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        return decryptBytes(manager, Base64.getDecoder().decode(str));
    }

    public static PublicKey generatePublicKey(HeypixelHandler manager, byte[] bArr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        var mode = manager.getEncryptMode();
        return KeyFactory.getInstance(mode).generatePublic(new X509EncodedKeySpec(bArr));
    }

    public static String encryptString(HeypixelHandler manager, String str) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException {
        PublicKey publicKey = generatePublicKey(manager, manager.key2);
        Cipher serverCipher = Cipher.getInstance(manager.getEncryptMode());
        serverCipher.init(1, publicKey);
        return Base64.getEncoder().encodeToString(serverCipher.doFinal(str.getBytes()));
    }

    public static PrivateKey generatePrivateKey(HeypixelHandler manager, byte[] bArr) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance(manager.getEncryptMode()).generatePrivate(new PKCS8EncodedKeySpec(bArr));
    }

    public static String decryptBytes(HeypixelHandler manager, byte[] bArr) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, UnsupportedEncodingException, InvalidKeySpecException {
        PrivateKey privateKey = generatePrivateKey(manager, manager.key3);
        Cipher serverCipher = Cipher.getInstance(manager.getEncryptMode());
        serverCipher.init(2, privateKey);
        return new String(serverCipher.doFinal(bArr), StandardCharsets.UTF_8);
    }
}
