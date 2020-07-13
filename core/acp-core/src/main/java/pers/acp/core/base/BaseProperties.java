package pers.acp.core.base;

import pers.acp.core.exceptions.ConfigException;
import pers.acp.core.log.LogFactory;
import pers.acp.core.tools.CommonUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by zhangbin on 2017-8-7 17:49
 */
public abstract class BaseProperties extends Properties {

    private static final long serialVersionUID = -8039022312482420408L;

    private static final LogFactory log = LogFactory.getInstance(BaseProperties.class);// 日志对象

    private static final Map<String, BaseProperties> instanceMap = new ConcurrentHashMap<>();

    private String fileAdbPathName = null;

    private String comments = null;

    private long lastModified = 0;

    public static BaseProperties getInstance(Class<? extends BaseProperties> cls, String propertiesFileName) throws ConfigException, IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        return getInstance(cls, propertiesFileName, null);
    }

    public static BaseProperties getInstance(Class<? extends BaseProperties> cls, String propertiesFileName, String fileAdbPathName) throws ConfigException, IOException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        if (!CommonUtils.INSTANCE.isNullStr(propertiesFileName)) {
            if (CommonUtils.INSTANCE.isNullStr(fileAdbPathName)) {
                fileAdbPathName = CommonUtils.INSTANCE.getAbsPath(propertiesFileName);
            } else {
                fileAdbPathName = fileAdbPathName.replace("/", File.separator).replace("\\", File.separator);
            }
            File file = new File(fileAdbPathName);
            InputStreamReader inputStreamReader = null;
            if (!file.exists()) {
                InputStream in = CommonUtils.INSTANCE.getResourceInputStream(propertiesFileName.replace("\\", "/"));
                if (in != null) {
                    inputStreamReader = new InputStreamReader(in, CommonUtils.defaultCharset);
                } else {
                    log.warn(propertiesFileName + " is not found");
                }
            } else {
                inputStreamReader = new InputStreamReader(new FileInputStream(file), CommonUtils.defaultCharset);
            }
            BaseProperties instance = instanceMap.get(propertiesFileName);
            if (!instanceMap.containsKey(propertiesFileName) || (file.exists() && file.lastModified() > instance.lastModified)) {
                if (inputStreamReader != null) {
                    synchronized (instanceMap) {
                        instance = cls.getDeclaredConstructor().newInstance();
                        instance.load(inputStreamReader);
                        if (file.exists()) {
                            instance.lastModified = file.lastModified();
                        }
                        instance.fileAdbPathName = fileAdbPathName;
                        instanceMap.put(propertiesFileName, instance);
                        log.info("load [" + propertiesFileName + "] success => " + cls.getCanonicalName());
                        instanceMap.notifyAll();
                        return instance;
                    }
                } else {
                    return null;
                }
            } else {
                return instance;
            }
        } else {
            throw new ConfigException("load config failed : need specify properties file for [" + cls.getCanonicalName() + "]");
        }
    }

    public void storeToProperties() throws ConfigException {
        OutputStreamWriter oFile = null;
        try {
            if (!CommonUtils.INSTANCE.isNullStr(fileAdbPathName)) {
                synchronized (this) {
                    File file = new File(fileAdbPathName);
                    if (!file.getParentFile().exists()) {
                        if (!file.getParentFile().mkdirs()) {
                            log.error("mkdirs failed : " + file.getParentFile().getCanonicalPath());
                        }
                    }
                    if (!file.exists() && !file.createNewFile()) {
                        throw new ConfigException("create file failed : " + fileAdbPathName);
                    }
                    oFile = new OutputStreamWriter(new FileOutputStream(file), CommonUtils.defaultCharset);
                    this.store(oFile, comments);
                }
                log.info("write [" + fileAdbPathName + "] success => " + this.getClass().getCanonicalName());
            } else {
                throw new ConfigException("write config failed: need specify properties file for " + this.getClass().getCanonicalName());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ConfigException("write config failed:[" + fileAdbPathName + "]");
        } finally {
            if (oFile != null) {
                try {
                    oFile.close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getFileAdbPathName() {
        return fileAdbPathName;
    }

    public void setFileAdbPathName(String fileAdbPathName) {
        this.fileAdbPathName = fileAdbPathName;
    }

}
