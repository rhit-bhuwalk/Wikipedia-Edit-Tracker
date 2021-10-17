import java.util.Base64;    
import javax.crypto.Cipher;  
import javax.crypto.KeyGenerator;   
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;  
public class AESDecrypter {  
    static Cipher cipher;  
    // used https://stackoverflow.com/questions/10303767/encrypt-and-decrypt-in-java for reference
    public static String decrypt(String encryptedText)
            throws Exception {
     	byte[] key = {-98, 49, -113, -17, 45, 52, 96, -65, -86, 67, -67, -49, 31, -124, 27, 118}; //key
    	SecretKey secretKey = new SecretKeySpec(key, "AES"); 
        cipher = Cipher.getInstance("AES"); //SunJCE provider AES algorithm, mode(optional) and padding schema(optional)  
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] encryptedTextByte = decoder.decode(encryptedText);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedByte = cipher.doFinal(encryptedTextByte);
        String decryptedText = new String(decryptedByte);
        return decryptedText;
    }
}

   // public static void main(String[] args) throws Exception {
        /* 
         create key 
         If we need to generate a new key use a KeyGenerator
         If we have existing plaintext key use a SecretKeyFactory
        */ 

        /*
          Cipher Info
          Algorithm : for the encryption of electronic data
          mode of operation : to avoid repeated blocks encrypt to the same values.
          padding: ensuring messages are the proper length necessary for certain ciphers 
          mode/padding are not used with stream cyphers.  
         */
    	
   
//        String plainText = "ExtraEsotericPasswordTellNo1";
//        System.out.println("Plain Text Before Encryption: " + plainText);

//        String encryptedText = encrypt(plainText, secretKey);
//        System.out.println("Encrypted Text After Encryption: " + encryptedText);

//        String decryptedText = decrypt(encryptedText, secretKey);
//        System.out.println("Decrypted Text After Decryption: " + decryptedText);
 //   }

//    public static String encrypt(String plainText, SecretKey secretKey)
//            throws Exception {
//        byte[] plainTextByte = plainText.getBytes();
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
//        byte[] encryptedByte = cipher.doFinal(plainTextByte);
//        Base64.Encoder encoder = Base64.getEncoder();
//        String encryptedText = encoder.encodeToString(encryptedByte);
//        return encryptedText;
//    }

 
