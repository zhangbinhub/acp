package pers.acp.core.security.key

import org.apache.commons.text.CharacterPredicates
import org.apache.commons.text.RandomStringGenerator
import pers.acp.core.log.LogFactory
import pers.acp.core.tools.CommonUtils
import org.bouncycastle.util.encoders.Base64
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import pers.acp.core.security.HmacEncrypt

import javax.crypto.spec.SecretKeySpec
import java.io.*
import java.math.BigInteger
import java.security.*
import java.security.interfaces.DSAPrivateKey
import java.security.interfaces.DSAPublicKey
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.*

/**
 * @author zhang by 10/07/2019
 * @since JDK 11
 */
object KeyManagement {

    private val log = LogFactory.getInstance(KeyManagement::class.java)

    private const val ENCODE = CommonUtils.defaultCharset

    @JvmField
    val RANDOM_STR = 0

    @JvmField
    val RANDOM_CHAR = 1

    @JvmField
    val RANDOM_NUMBER = 2

    /**
     * 获取临时AES密钥，过期时间内再次获取则取到相同的值，剩余时间小于延迟时间则延迟过期；过期时间之后取到新的值
     *
     * @param traitId   申请者身份标识字符串
     * @return 临时密钥
     */
    @JvmStatic
    @Throws(Exception::class)
    fun getTempAESKey(traitId: String): Key =
            getEntity(keyType = KeyType.AES, traitId = traitId).key!!

    /**
     * 获取临时DES密钥，过期时间内再次获取则取到相同的值，剩余时间小于延迟时间则延迟过期；过期时间之后取到新的值
     *
     * @param traitId   申请者身份标识字符串
     * @return 临时密钥
     */
    @JvmStatic
    @Throws(Exception::class)
    fun getTempDESKey(traitId: String): Key =
            getEntity(keyType = KeyType.DES, traitId = traitId).key!!

    /**
     * 获取临时3DES密钥，过期时间内再次获取则取到相同的值，剩余时间小于延迟时间则延迟过期；过期时间之后取到新的值
     *
     * @param traitId   申请者身份标识字符串
     * @return 临时密钥
     */
    @JvmStatic
    @Throws(Exception::class)
    fun getTemp3DESKey(traitId: String): Key =
            getEntity(keyType = KeyType.DESede, traitId = traitId).key!!

    /**
     * 获取临时RSA公私钥对，过期时间内再次获取则取到相同的值，剩余时间小于延迟时间则延迟过期；过期时间之后取到新的值
     *
     * @param traitId   申请者身份标识字符串
     * @return Object[] [0]:RSAPublicKey,[1]:RSAPrivateKey
     */
    @JvmStatic
    @Throws(Exception::class)
    fun getTempRSAKeys(traitId: String): Array<Any> {
        val entity = getEntity(keyType = KeyType.RSA, traitId = traitId)
        return arrayOf(entity.rsaPublicKey!!, entity.rsaPrivateKey!!)
    }

    /**
     * 获取临时DSA公私钥对，过期时间内再次获取则取到相同的值，剩余时间小于延迟时间则延迟过期；过期时间之后取到新的值
     *
     * @param traitId   申请者身份标识字符串
     * @return Object[] [0]:DSAPublicKey,[1]:DSAPrivateKey
     */
    @JvmStatic
    @Throws(Exception::class)
    fun getTempDSAKeys(traitId: String): Array<Any> {
        val entity = getEntity(keyType = KeyType.DSA, traitId = traitId)
        return arrayOf(entity.dsaPublicKey!!, entity.dsaPrivateKey!!)
    }

    /**
     * 获取临时随机字符串，过期时间内再次获取则取到相同的值，剩余时间小于延迟时间则延迟过期；过期时间之后取到新的值
     *
     * @param traitId   申请者身份标识字符串
     * @param flag      类型：RANDOM_STR | RANDOM_CHAR | RANDOM_NUMBER
     * @param length    随机字符串的长度
     * @return 临时随机字符串
     */
    @JvmStatic
    @Throws(Exception::class)
    fun getTempRandomString(traitId: String, flag: Int, length: Int): String {
        val keyType = when (flag) {
            RANDOM_CHAR -> KeyType.RandomChar
            RANDOM_NUMBER -> KeyType.RandomNumber
            else -> KeyType.RandomStr
        }
        return getEntity(keyType = keyType, traitId = traitId, length = length).randomString!!
    }

    /**
     * 生成RSA公钥和私钥
     *
     * @return Object[] [0]:RSAPublicKey,[1]:RSAPrivateKey
     */
    @JvmStatic
    @Throws(NoSuchAlgorithmException::class)
    fun getRsaKeys(): Array<Any> {
        val keyPairGen = KeyPairGenerator.getInstance("RSA")
        keyPairGen.initialize(2048)
        val keyPair = keyPairGen.generateKeyPair()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey
        return arrayOf(publicKey, privateKey)
    }

    /**
     * 生成DSA公钥和私钥
     *
     * @return Object[] [0]:DSAPublicKey,[1]:DSAPrivateKey
     */
    @JvmStatic
    @Throws(NoSuchAlgorithmException::class, UnsupportedEncodingException::class)
    fun getDsaKeys(): Array<Any> {
        val keyPairGen = KeyPairGenerator.getInstance("DSA")
        val secureRandom = SecureRandom()
        secureRandom.setSeed(getRandomString(RANDOM_STR, 32).toByteArray(charset(ENCODE)))
        keyPairGen.initialize(1024, secureRandom)
        val keyPair = keyPairGen.generateKeyPair()
        val publicKey = keyPair.public as DSAPublicKey
        val privateKey = keyPair.private as DSAPrivateKey
        return arrayOf(publicKey, privateKey)
    }

    /**
     * 生成AES密钥
     *
     * @param keyStr 长度不超过16位的字符串
     * @return 密钥
     */
    @JvmStatic
    @Throws(UnsupportedEncodingException::class)
    fun getAESKey(keyStr: String): Key = getKey(keyStr, "AES")

    /**
     * 生成DES密钥
     *
     * @param keyStr 长度不超过24位的字符串
     * @return 密钥
     */
    @JvmStatic
    @Throws(UnsupportedEncodingException::class)
    fun getDESKey(keyStr: String): Key = getKey(keyStr, "DES")

    /**
     * 生成3DES密钥
     *
     * @param keyStr 长度不超过24位的字符串
     * @return 密钥
     */
    @JvmStatic
    @Throws(UnsupportedEncodingException::class)
    fun get3DESKey(keyStr: String): Key = getKey(keyStr, "DESede")

    /**
     * 生成密钥
     *
     * @param keyStr    密钥字符串
     * @param cryptType 加密算法
     * @return 密钥
     * @throws UnsupportedEncodingException 异常
     */
    @JvmStatic
    @Throws(UnsupportedEncodingException::class)
    fun getKey(keyStr: String, cryptType: String): Key = SecretKeySpec(keyStr.toByteArray(charset(ENCODE)), cryptType)

    /**
     * 使用模和指数生成RSA公钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA/None/NoPadding】
     *
     * @param modulus  模
     * @param exponent 指数
     * @param radix    基数 2，8，10，16
     * @return 公钥
     */
    @JvmStatic
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    @JvmOverloads
    fun getRSAPublicKey(modulus: String, exponent: String, radix: Int = 10): RSAPublicKey {
        val b1 = BigInteger(modulus, radix)
        val b2 = BigInteger(exponent, radix)
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = RSAPublicKeySpec(b1, b2)
        return keyFactory.generatePublic(keySpec) as RSAPublicKey
    }

    /**
     * 使用模和指数生成RSA私钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA/None/NoPadding】
     *
     * @param modulus  模
     * @param exponent 指数
     * @param radix    基数 2，8，10，16
     * @return 私钥
     */
    @JvmStatic
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    @JvmOverloads
    fun getRSAPrivateKey(modulus: String, exponent: String, radix: Int = 10): RSAPrivateKey {
        val b1 = BigInteger(modulus, radix)
        val b2 = BigInteger(exponent, radix)
        val keyFactory = KeyFactory.getInstance("RSA")
        val keySpec = RSAPrivateKeySpec(b1, b2)
        return keyFactory.generatePrivate(keySpec) as RSAPrivateKey
    }

    /**
     * 解析DER编码的公钥证书
     *
     * @param filePath 证书文件绝对路径
     * @return RSA公钥对象
     */
    @JvmStatic
    @Throws(InvalidKeySpecException::class, NoSuchAlgorithmException::class, IOException::class)
    fun getRSAPublicKeyForDER(filePath: String): RSAPublicKey {
        val key = getFileContent(filePath)
        val keySpec = X509EncodedKeySpec(key)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec) as RSAPublicKey
    }

    /**
     * 解析DER编码的私钥证书
     *
     * @param filePath 证书文件绝对路径
     * @return RSA私钥对象
     */
    @JvmStatic
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class, IOException::class)
    fun getRSAPrivateKeyForDER(filePath: String): RSAPrivateKey {
        val key = getFileContent(filePath)
        val keySpec = PKCS8EncodedKeySpec(key)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec) as RSAPrivateKey
    }

    /**
     * 解析PEM编码的公钥证书
     *
     * @param filePath 证书文件绝对路径
     * @return RSA公钥对象
     */
    @JvmStatic
    @Throws(IOException::class)
    fun getRSAPublicKeyForPEM(filePath: String): RSAPublicKey? {
        Security.addProvider(BouncyCastleProvider())
        val rsaPublic = FileReader(filePath)
        val publicPem = PEMParser(rsaPublic)
        val pubObject = publicPem.readObject()
        val pemConverter = JcaPEMKeyConverter()
        pemConverter.setProvider("BC")
        return if (pubObject is SubjectPublicKeyInfo) {
            pemConverter.getPublicKey(pubObject) as RSAPublicKey
        } else {
            null
        }
    }

    /**
     * 解析PEM编码的私钥证书
     *
     * @param filePath 证书文件绝对路径
     * @return RSA私钥对象
     */
    @JvmStatic
    @Throws(IOException::class)
    fun getRSAPrivateKeyForPEM(filePath: String): RSAPrivateKey? {
        Security.addProvider(BouncyCastleProvider())
        val rsaReader = FileReader(filePath)
        val privatePem = PEMParser(rsaReader)
        val obj = privatePem.readObject()
        val pemConverter = JcaPEMKeyConverter()
        pemConverter.setProvider("BC")
        return if (obj is PEMKeyPair) {
            val keyPair = pemConverter.getKeyPair(obj)
            keyPair.private as RSAPrivateKey
        } else {
            if (obj is KeyPair) obj.private as RSAPrivateKey else null
        }
    }

    /**
     * 解析SSH生成的公钥证书
     *
     * @param keyStr 公钥字符串
     * @return RSA公钥对象
     */
    @JvmStatic
    @Throws(Exception::class)
    fun getRSAPublicKeyForSSH(keyStr: String): RSAPublicKey {
        val key = Base64.decode(keyStr)
        val sshRsa = byteArrayOf(0, 0, 0, 7, 's'.toByte(), 's'.toByte(), 'h'.toByte(), '-'.toByte(), 'r'.toByte(), 's'.toByte(), 'a'.toByte())
        var startIndex = sshRsa.size
        /* Decode the public exponent */
        var len = decodeUInt32(key, startIndex)
        startIndex += 4
        val peB = ByteArray(len)
        for (i in 0 until len) {
            peB[i] = key[startIndex++]
        }
        val pe = BigInteger(peB)
        /* Decode the modulus */
        len = decodeUInt32(key, startIndex)
        startIndex += 4
        val mdB = ByteArray(len)
        for (i in 0 until len) {
            mdB[i] = key[startIndex++]
        }
        val md = BigInteger(mdB)
        val keyFactory = KeyFactory.getInstance("RSA")
        val ks = RSAPublicKeySpec(md, pe)
        return keyFactory.generatePublic(ks) as RSAPublicKey
    }

    /**
     * 生成DSA公钥
     *
     * @param publicKey y
     * @param prime     p
     * @param subPrime  q
     * @param base      g
     * @param radix     基数 2，8，10，16
     * @return 公钥
     */
    @JvmStatic
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    @JvmOverloads
    fun getDSAPublicKey(publicKey: String, prime: String, subPrime: String, base: String, radix: Int = 10): DSAPublicKey {
        val y = BigInteger(publicKey, radix)
        val p = BigInteger(prime, radix)
        val q = BigInteger(subPrime, radix)
        val g = BigInteger(base, radix)
        val keyFactory = KeyFactory.getInstance("DSA")
        val keySpec = DSAPublicKeySpec(y, p, q, g)
        return keyFactory.generatePublic(keySpec) as DSAPublicKey
    }

    /**
     * 使用模和指数生成DSA私钥
     *
     * @param privateKey x
     * @param prime      p
     * @param subPrime   q
     * @param base       g
     * @param radix      基数 2，8，10，16
     * @return 私钥
     */
    @JvmStatic
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class)
    @JvmOverloads
    fun getDSAPrivateKey(privateKey: String, prime: String, subPrime: String, base: String, radix: Int = 10): DSAPrivateKey {
        val x = BigInteger(privateKey, radix)
        val p = BigInteger(prime, radix)
        val q = BigInteger(subPrime, radix)
        val g = BigInteger(base, radix)
        val keyFactory = KeyFactory.getInstance("DSA")
        val keySpec = DSAPrivateKeySpec(x, p, q, g)
        return keyFactory.generatePrivate(keySpec) as DSAPrivateKey
    }

    /**
     * 解析DER编码的公钥证书
     *
     * @param filePath 证书文件绝对路径
     * @return DSA公钥对象
     */
    @JvmStatic
    @Throws(InvalidKeySpecException::class, NoSuchAlgorithmException::class, IOException::class)
    fun getDSAPublicKeyForDER(filePath: String): DSAPublicKey {
        val key = getFileContent(filePath)
        val keySpec = X509EncodedKeySpec(key)
        val keyFactory = KeyFactory.getInstance("DSA")
        return keyFactory.generatePublic(keySpec) as DSAPublicKey
    }

    /**
     * 解析DER编码的私钥证书
     *
     * @param filePath 证书文件绝对路径
     * @return DSA私钥对象
     */
    @JvmStatic
    @Throws(NoSuchAlgorithmException::class, InvalidKeySpecException::class, IOException::class)
    fun getDSAPrivateKeyForDER(filePath: String): DSAPrivateKey {
        val key = getFileContent(filePath)
        val keySpec = PKCS8EncodedKeySpec(key)
        val keyFactory = KeyFactory.getInstance("DSA")
        return keyFactory.generatePrivate(keySpec) as DSAPrivateKey
    }

    /**
     * 解析PEM编码的公钥证书
     *
     * @param filePath 证书文件绝对路径
     * @return DSA公钥对象
     */
    @JvmStatic
    @Throws(IOException::class)
    fun getDSAPublicKeyForPEM(filePath: String): DSAPublicKey? {
        Security.addProvider(BouncyCastleProvider())
        val dsaPublic = FileReader(filePath)
        val publicPem = PEMParser(dsaPublic)
        val pubObject = publicPem.readObject()
        val pemConverter = JcaPEMKeyConverter()
        pemConverter.setProvider("BC")
        return if (pubObject is SubjectPublicKeyInfo) {
            pemConverter.getPublicKey(pubObject) as DSAPublicKey
        } else {
            null
        }
    }

    /**
     * 解析PEM编码的私钥证书
     *
     * @param filePath 证书文件绝对路径
     * @return DSA私钥对象
     */
    @JvmStatic
    @Throws(IOException::class)
    fun getDSAPrivateKeyForPEM(filePath: String): DSAPrivateKey? {
        Security.addProvider(BouncyCastleProvider())
        val dsaReader = FileReader(filePath)
        val privatePem = PEMParser(dsaReader)
        val obj = privatePem.readObject()
        val pemConverter = JcaPEMKeyConverter()
        pemConverter.setProvider("BC")
        if (obj is PEMKeyPair) {
            val keyPair = pemConverter.getKeyPair(obj)
            return keyPair.private as DSAPrivateKey
        } else {
            return if (obj is KeyPair) obj.private as DSAPrivateKey else null
        }
    }

    /**
     * 解析SSH生成的公钥证书
     *
     * @param keyStr 公钥字符串
     * @return DSA公钥对象
     */
    @JvmStatic
    @Throws(Exception::class)
    fun getDSAPublicKeyForSSH(keyStr: String): DSAPublicKey {
        val key = Base64.decode(keyStr)
        val sshDsa = byteArrayOf(0, 0, 0, 7, 's'.toByte(), 's'.toByte(), 'h'.toByte(), '-'.toByte(), 'd'.toByte(), 's'.toByte(), 's'.toByte())
        var startIndex = sshDsa.size
        /* Decode the p */
        var len = decodeUInt32(key, startIndex)
        startIndex += 4
        val pB = ByteArray(len)
        for (i in 0 until len) {
            pB[i] = key[startIndex++]
        }
        val p = BigInteger(pB)
        /* Decode the q */
        len = decodeUInt32(key, startIndex)
        startIndex += 4
        val qB = ByteArray(len)
        for (i in 0 until len) {
            qB[i] = key[startIndex++]
        }
        val q = BigInteger(qB)
        /* Decode the g */
        len = decodeUInt32(key, startIndex)
        startIndex += 4
        val gB = ByteArray(len)
        for (i in 0 until len) {
            gB[i] = key[startIndex++]
        }
        val g = BigInteger(gB)
        /* Decode the y */
        len = decodeUInt32(key, startIndex)
        startIndex += 4
        val yB = ByteArray(len)
        for (i in 0 until len) {
            yB[i] = key[startIndex++]
        }
        val y = BigInteger(yB)
        val keyFactory = KeyFactory.getInstance("DSA")
        val ks = DSAPublicKeySpec(y, p, q, g)
        return keyFactory.generatePublic(ks) as DSAPublicKey
    }

    /**
     * 生成随机字符串
     *
     * @param flag   类型：RANDOM_STR | RANDOM_CHAR | RANDOM_NUMBER
     * @param length 长度
     * @return 随机字符串
     */
    @JvmStatic
    fun getRandomString(flag: Int, length: Int): String {
        val builder = RandomStringGenerator.Builder().withinRange(33, 126)
        when (flag) {
            RANDOM_CHAR -> builder.filteredBy(CharacterPredicates.LETTERS)
            RANDOM_NUMBER -> builder.filteredBy(CharacterPredicates.DIGITS)
            else -> builder.filteredBy(CharacterPredicates.LETTERS, CharacterPredicates.DIGITS)
        }
        return builder.build().generate(length)
    }

    /**
     * 获取密钥实体
     *
     * @param keyType   密钥类型
     * @param traitId   申请者身份标识字符串
     * @param length    密钥长度（随机字符串密钥时有效）
     * @return 密钥实体
     */
    @Throws(Exception::class)
    private fun getEntity(keyType: KeyType, cryptType: String = HmacEncrypt.CRYPT_TYPE, traitId: String, length: Int = 0): KeyEntity =
            KeyEntity.generateEntity(keyType, cryptType, traitId, length)

    private fun decodeUInt32(key: ByteArray, start_index: Int): Int {
        val test = key.copyOfRange(start_index, start_index + 4)
        return BigInteger(test).toInt()
    }

    /**
     * 读取文件内容
     *
     * @param filePath 文件绝对路径
     * @return 字节数组
     */
    @Throws(IOException::class)
    private fun getFileContent(filePath: String): ByteArray {
        val file = File(filePath)
        if (!file.exists()) {
            throw FileNotFoundException(filePath)
        }
        val bos = ByteArrayOutputStream(file.length().toInt())
        val inputStream = BufferedInputStream(FileInputStream(file))
        val bufSize = 1024
        val buffer = ByteArray(bufSize)
        var len: Int = inputStream.read(buffer, 0, bufSize)
        while (-1 != len) {
            bos.write(buffer, 0, len)
            len = inputStream.read(buffer, 0, bufSize)
        }
        try {
            inputStream.close()
        } catch (e: IOException) {
            log.error(e.message, e)
        }
        bos.close()
        return bos.toByteArray()
    }

}