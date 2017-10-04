package com.lavidatec.template.pojo;

import com.lavidatec.template.pojo.RSAEncrption;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Calendar;
 
import javax.crypto.Cipher;
 
import org.bouncycastle.util.encoders.Base64;
 
public class RSA {
    private static String ALGORITHM = "RSA/ECB/PKCS1Padding";
     
    public KeyPair getRandomKeyPair(){
        KeyPair keyPair = null;
        try{
            // Generate key pair, and load it into public key and private key instances.
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = new SecureRandom();
            random.setSeed("test".getBytes());
            keygen.initialize(1024, random); // TODO Change length may cause the result incorrect.
            keyPair = keygen.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyPair;
    }
    
    
    public static void main(String[] args) {
        try {
            // Generate key pair, and load it into public key and private key instances.
            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = new SecureRandom();
            random.setSeed("test".getBytes());
            keygen.initialize(1024, random); // TODO Change length may cause the result incorrect.
            KeyPair keyPair = keygen.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
             
            // Tag for timer.
            long start, end;
 
            // Generate the plain text
            String plainText = "abc123!@#";
     
            System.out.println("Encryption:");
            start = Calendar.getInstance().getTimeInMillis();
 
            // Initiate the encryptor.
            RSAEncrption encryption = new RSAEncrption();
            // Initiate a variable for storing the encrypting result.
            byte[] result = null;
             
            try {
                // Encrypt
                result = encryption.cryptByRSA(plainText.getBytes("UTF-8"), publicKey, ALGORITHM, Cipher.ENCRYPT_MODE);
                end = Calendar.getInstance().getTimeInMillis();
                System.out.println("Encrypted result: " + Base64.toBase64String(result));
                System.out.println("Encrypted length: " + result.length);
                System.out.println("Encrypted time: " + (end-start) + "ms\n");
         
                // Decrypt
                System.out.println("Decryption:");
                start = Calendar.getInstance().getTimeInMillis();
                byte[] decryptResult = encryption.cryptByRSA(result, privateKey, ALGORITHM, Cipher.DECRYPT_MODE);
                end = Calendar.getInstance().getTimeInMillis();
                System.out.println("Decrypted string: " + new String(decryptResult, "UTF-8"));
                System.out.println("Decrypted time: " + (end-start) + "ms\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}