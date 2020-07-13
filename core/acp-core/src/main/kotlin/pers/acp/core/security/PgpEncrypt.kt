package pers.acp.core.security

import org.bouncycastle.bcpg.ArmoredOutputStream
import org.bouncycastle.bcpg.CompressionAlgorithmTags
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openpgp.*
import org.bouncycastle.openpgp.PGPPublicKey
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory
import org.bouncycastle.openpgp.operator.jcajce.*
import org.bouncycastle.util.io.Streams
import java.io.*
import java.security.*
import java.util.*


/**
 * @author zhang by 26/05/2020
 * @since JDK 11
 */
object PgpEncrypt {
    @Throws(Exception::class)
    private fun generateKeyPair(rsaWidth: Int): PGPKeyPair {
        val kpg: KeyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC") //获取密钥对生成器实例
        kpg.initialize(rsaWidth) //设定RSA位宽
        val kp: KeyPair = kpg.generateKeyPair() //生成RSA密钥对
        return JcaPGPKeyPair(PGPPublicKey.RSA_GENERAL, kp, Date()) //返回根据日期，密钥对生成的PGP密钥对
    }

    /**
     * 生成密钥
     * @param identity 密钥ID也就是key值，可以用来标记密钥属于谁
     * @param passWord 密钥的密码，用来解出私钥
     * @param rsaWidth RSA位宽
     * @param certificationLevel the type of certification to be added.
     * @param encAlgorithm encryption algorithm to use.
     * @param hashAlgorithm hash algorithm to use.
     * @return PGP密钥
     */
    @JvmStatic
    @Throws(Exception::class)
    fun generateSecretKey(identity: String, passWord: String, rsaWidth: Int,
                          certificationLevel: Int, encAlgorithm: Int, hashAlgorithm: Int): PGPSecretKey {
        Security.addProvider(BouncyCastleProvider())
        val passPhrase = passWord.toCharArray() //将passWord转换成字符数组
        val keyPair = generateKeyPair(rsaWidth) //生成//生成RSA密钥对
        val hash = JcaPGPDigestCalculatorProviderBuilder().build()[hashAlgorithm] //使用SHA1作为证书的散列算法
        return PGPSecretKey(certificationLevel,
                keyPair,
                identity,
                hash,
                null,
                null,
                JcaPGPContentSignerBuilder(keyPair.publicKey.algorithm, hashAlgorithm),
                JcePBESecretKeyEncryptorBuilder(encAlgorithm, hash).setProvider("BC").build(passPhrase))
    }

    @Throws(IOException::class)
    private fun compressFile(fileName: String, algorithm: Int = CompressionAlgorithmTags.ZIP): ByteArray {
        val bOut = ByteArrayOutputStream()
        val comData = PGPCompressedDataGenerator(algorithm)
        PGPUtil.writeFileToLiteralData(comData.open(bOut), PGPLiteralData.BINARY, File(fileName))
        comData.close()
        return bOut.toByteArray()
    }

    @Throws(PGPException::class, NoSuchProviderException::class)
    private fun findSecretKey(pgpSec: PGPSecretKeyRingCollection, keyID: Long, pass: CharArray): PGPPrivateKey? {
        val pgpSecKey = pgpSec.getSecretKey(keyID) ?: return null
        return pgpSecKey.extractPrivateKey(JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(pass))
    }

    /**
     * 从文件中读取公钥
     * @param keyFileName 公钥文件
     */
    @JvmStatic
    @Throws(IOException::class, PGPException::class)
    fun readPublicKey(keyFileName: String): PGPPublicKey {
        val keyIn: InputStream = BufferedInputStream(FileInputStream(keyFileName))
        val pubKey = readPublicKey(keyIn)
        keyIn.close()
        return pubKey
    }

    /**
     * 从文件中读取公钥
     */
    @JvmStatic
    @Throws(PGPException::class, IOException::class)
    fun readPublicKey(input: InputStream): PGPPublicKey {
        val keyRingIter = PGPPublicKeyRingCollection(PGPUtil.getDecoderStream(input), JcaKeyFingerprintCalculator())
                .keyRings
        while (keyRingIter.hasNext()) {
            val keyRing = keyRingIter.next()
            val keyIter = keyRing.publicKeys
            while (keyIter.hasNext()) {
                val key = keyIter.next() as PGPPublicKey
                if (key.isEncryptionKey) {
                    return key
                }
            }
        }
        throw IllegalArgumentException("Can't find encryption key in key ring.")
    }

    /**
     * 从文件中读取密钥
     * @param keyFileName 密钥文件
     */
    @JvmStatic
    @Throws(IOException::class, PGPException::class)
    fun readSecretKey(keyFileName: String): PGPSecretKey {
        val keyIn: InputStream = BufferedInputStream(FileInputStream(keyFileName))
        val secKey = readSecretKey(keyIn)
        keyIn.close()
        return secKey
    }

    /**
     * 从文件中读取密钥
     */
    @JvmStatic
    @Throws(IOException::class, PGPException::class)
    fun readSecretKey(input: InputStream): PGPSecretKey {
        val keyRingIter = PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(input), JcaKeyFingerprintCalculator())
                .keyRings
        while (keyRingIter.hasNext()) {
            val keyRing = keyRingIter.next()
            val keyIter = keyRing.secretKeys
            while (keyIter.hasNext()) {
                val key = keyIter.next() as PGPSecretKey
                if (key.isSigningKey) {
                    return key
                }
            }
        }
        throw java.lang.IllegalArgumentException("Can't find signing key in key ring.")
    }

    /**
     * 文件加密
     * @param inputFileName 原文件
     * @param outputFileName 加密后的文件
     * @param publicKey 公钥对象
     * @param armor
     * @param withIntegrityCheck
     */
    @JvmStatic
    @JvmOverloads
    @Throws(PGPException::class, NoSuchProviderException::class, IOException::class)
    fun encryptFile(inputFileName: String, outputFileName: String, publicKey: PGPPublicKey, armor: Boolean = true, withIntegrityCheck: Boolean = true) {
        Security.addProvider(BouncyCastleProvider())
        val outputStream = BufferedOutputStream(FileOutputStream(outputFileName)).let {
            if (armor) {
                ArmoredOutputStream(it)
            } else {
                it
            }
        }
        outputStream.use { stream ->
            val bytes: ByteArray = compressFile(inputFileName)
            val encGen = PGPEncryptedDataGenerator(JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5)
                    .setWithIntegrityPacket(withIntegrityCheck).setSecureRandom(SecureRandom()).setProvider("BC"))
            encGen.addMethod(JcePublicKeyKeyEncryptionMethodGenerator(publicKey).setProvider("BC"))
            val cOut = encGen.open(stream, bytes.size.toLong())
            cOut.write(bytes)
            cOut.close()
        }
    }

    /**
     * PGP文件解密
     * @param inputFileName 加密的文件
     * @param outputFileName 解密后的文件
     * @param keyFileName 密钥文件
     * @param passWord 密码
     */
    @JvmStatic
    @Throws(PGPException::class, NoSuchProviderException::class, IOException::class)
    fun decryptFile(inputFileName: String, outputFileName: String, keyFileName: String, passWord: String) {
        Security.addProvider(BouncyCastleProvider())
        val inputStream = BufferedInputStream(FileInputStream(inputFileName))
        val keyStream = BufferedInputStream(FileInputStream(keyFileName))
        val outputStream = BufferedOutputStream(FileOutputStream(outputFileName))
        try {
            val pgpF = JcaPGPObjectFactory(PGPUtil.getDecoderStream(inputStream))
            val o = pgpF.nextObject()
            val enc = if (o is PGPEncryptedDataList) {
                o
            } else {
                pgpF.nextObject() as PGPEncryptedDataList
            }
            val it = enc.encryptedDataObjects
            var sKey: PGPPrivateKey? = null
            var pbe: PGPPublicKeyEncryptedData? = null
            val pgpSec = PGPSecretKeyRingCollection(PGPUtil.getDecoderStream(keyStream), JcaKeyFingerprintCalculator())
            while (sKey == null && it.hasNext()) {
                pbe = it.next() as PGPPublicKeyEncryptedData
                sKey = findSecretKey(pgpSec, pbe.keyID, passWord.toCharArray())
            }
            if (sKey == null) {
                throw IllegalArgumentException("Secret key for message not found.")
            }
            val plainFact = JcaPGPObjectFactory(pbe!!.getDataStream(JcePublicKeyDataDecryptorFactoryBuilder().setProvider("BC").build(sKey)))
            var message = plainFact.nextObject()
            if (message is PGPCompressedData) {
                message = JcaPGPObjectFactory(message.dataStream).nextObject()
            }
            when (message) {
                is PGPLiteralData -> {
                    Streams.pipeAll(message.inputStream, outputStream)
                }
                is PGPOnePassSignatureList -> {
                    throw PGPException("Encrypted message contains a signed message - not literal data.")
                }
                else -> {
                    throw PGPException("Message is not a simple encrypted file - type unknown.")
                }
            }
            if (pbe.isIntegrityProtected) {
                if (!pbe.verify()) {
                    throw PGPException("Message failed integrity check")
                }
            }
        } finally {
            outputStream.close()
            keyStream.close()
            inputStream.close()
        }
    }
}