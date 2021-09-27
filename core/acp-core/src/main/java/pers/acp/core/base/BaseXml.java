package pers.acp.core.base;

import pers.acp.core.exceptions.ConfigException;
import pers.acp.core.log.LogFactory;
import pers.acp.core.tools.CommonUtils;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseXml {

    private static final LogFactory log = LogFactory.getInstance(BaseXml.class);// 日志对象

    private static final Map<String, BaseXml> instanceMap = new ConcurrentHashMap<>();

    private String fileAdbPathName = null;

    private long lastModified = 0;

    /**
     * 序列化xml文件为java对象
     *
     * @param cls 序列化后转换的java类
     * @return 实例对象
     */
    protected static BaseXml Load(Class<? extends BaseXml> cls) throws ConfigException {
        String fileName = null;
        try {
            fileName = CommonUtils.INSTANCE.getProperties(cls.getCanonicalName(), "");
            if (!CommonUtils.INSTANCE.isNullStr(fileName)) {
                File file = new File(CommonUtils.INSTANCE.getAbsPath(fileName));
                InputStreamReader inputStreamReader = null;
                if (!file.exists()) {
                    InputStream in = CommonUtils.INSTANCE.getResourceInputStream(fileName.replace("\\", "/"));
                    if (in != null) {
                        inputStreamReader = new InputStreamReader(in, CommonUtils.defaultCharset);
                    } else {
                        log.error(fileName + " is not found");
                    }
                } else {
                    inputStreamReader = new InputStreamReader(new FileInputStream(file), CommonUtils.defaultCharset);
                }

                BaseXml instance = instanceMap.get(fileName);
                if (!instanceMap.containsKey(fileName) || (file.exists() && file.lastModified() > instance.lastModified)) {
                    if (inputStreamReader != null) {
                        synchronized (instanceMap) {
                            XStream xstream = new XStream(new DomDriver());
                            xstream.addPermission(type -> type.getName().equals(cls.getName()));
                            xstream.processAnnotations(cls);
                            BaseXml obj = (BaseXml) xstream.fromXML(inputStreamReader);
                            if (obj == null) {
                                log.error("load config failed:[" + fileName + "]");
                                instanceMap.remove(fileName);
                            } else {
                                obj.fileAdbPathName = CommonUtils.INSTANCE.getAbsPath(fileName);
                                if (file.exists()) {
                                    obj.lastModified = file.lastModified();
                                }
                                instanceMap.put(fileName, obj);
                                log.info("load [" + fileName + "] success => " + cls.getCanonicalName());
                            }
                            instanceMap.notifyAll();
                            return obj;
                        }
                    } else {
                        return null;
                    }
                } else {
                    return instance;
                }
            } else {
                log.warn("load config failed: need specify XML file for " + cls.getCanonicalName());
                return null;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ConfigException("load config failed:[" + fileName + "]");
        }
    }

    /**
     * 转换成 XML 字符串
     *
     * @return XML 字符串
     */
    public String toXMLString() throws ConfigException {
        StringWriter out = new StringWriter();
        try {
            synchronized (this) {
                XStream xstream = new XStream(new DomDriver());
                xstream.toXML(this, out);
            }
            return out.toString();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ConfigException("convert config failed:[" + this.getClass().getCanonicalName() + "]");
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    /**
     * 将java对象信息写入xml文件
     */
    public void storeToXml() throws ConfigException {
        OutputStreamWriter oFile = null;
        try {
            if (!CommonUtils.INSTANCE.isNullStr(this.fileAdbPathName)) {
                synchronized (this) {
                    File file = new File(fileAdbPathName);
                    XStream xstream = new XStream(new DomDriver());
                    oFile = new OutputStreamWriter(new FileOutputStream(file), CommonUtils.defaultCharset);
                    xstream.toXML(this, oFile);
                    lastModified = file.lastModified();
                }
                log.info("write [" + fileAdbPathName + "] success => " + this.getClass().getCanonicalName());
            } else {
                throw new ConfigException("write config failed: need specify XML file for " + this.getClass().getCanonicalName());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
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

    public String getFileAdbPathName() {
        return fileAdbPathName;
    }

    public void setFileAdbPathName(String fileAdbPathName) {
        this.fileAdbPathName = fileAdbPathName;
    }

}
