package ces.neighborhood.blind.app;

import lombok.extern.slf4j.Slf4j;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
public class JasyptTest {

    private final String key = "password1@";

    @DisplayName("Jasypt 테스트")
    @Test
    public void jasyptTest() {
        //log.info("enc = {}", jasyptEncrypt("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"));
        String decrypt = jasyptDecrypt("DKleP4Bs7OkPNCiWCfvgNw==");
        System.out.println("dec = " + decrypt);

        //assertEquals("abc", decrypt);
    }
    public String jasyptEncrypt(String enc) {
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword(key);
        return pbeEnc.encrypt(enc);
    }

    public String jasyptDecrypt(String dec) {
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword(key);
        return pbeEnc.decrypt(dec);
    }

}
