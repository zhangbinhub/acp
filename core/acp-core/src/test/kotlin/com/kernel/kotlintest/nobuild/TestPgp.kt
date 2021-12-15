package com.kernel.kotlintest.nobuild

import org.bouncycastle.bcpg.ArmoredOutputStream
import org.bouncycastle.bcpg.HashAlgorithmTags
import org.bouncycastle.openpgp.PGPEncryptedData
import org.bouncycastle.openpgp.PGPSignature
import org.junit.jupiter.api.Test
import com.github.zhangbinhub.acp.core.security.PgpEncrypt
import java.io.FileOutputStream

class TestPgp {
    //    private val secretKeyFileName = "D:\\test\\pgp\\pgpSecretKey.asc"
    private val secretKeyFileName = "D:\\test\\pgp\\BankCQ_secret.key"
    private val publicKeyFileName = "D:\\test\\pgp\\BankCQ_public.key"

    @Test
    fun generatePgpSecretKey() {
        val secretKey = PgpEncrypt.generateSecretKey("zhangbin", "123456", 2048,
                PGPSignature.DEFAULT_CERTIFICATION, PGPEncryptedData.CAST5, HashAlgorithmTags.SHA1)
        ArmoredOutputStream(FileOutputStream(secretKeyFileName)).use { stream ->
            secretKey.encode(stream)
        }
    }

    @Test
    fun encryptFile() {
        val srcFileName = "D:\\test\\test.txt"
        val encFileName = "D:\\test\\enc_test.txt"
        PgpEncrypt.encryptFile(srcFileName, encFileName, PgpEncrypt.readSecretKey(secretKeyFileName).publicKey,
                armor = true, withIntegrityCheck = true)
        PgpEncrypt.encryptFile(srcFileName, encFileName, PgpEncrypt.readPublicKey(publicKeyFileName),
                armor = true, withIntegrityCheck = true)
    }

    @Test
    fun decryptFile() {
        val encFileName = "D:\\test\\enc_test.txt"
        val disFileName = "D:\\test\\out_test.txt"
        PgpEncrypt.decryptFile(encFileName, disFileName, secretKeyFileName, "123456")
    }
}