package io.github.zhangbinhub.acp.core.file.pdf;

import io.github.zhangbinhub.acp.core.CommonTools;
import io.github.zhangbinhub.acp.core.file.pdf.fonts.FontLoader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.Map;
import java.util.Map.Entry;

class HtmlParse {

    private static final String[] tags = {"area", "base", "br", "col", "command",
            "embed", "hr", "img", "input", "keygen", "link", "meta", "param",
            "source", "track", "wbr"};

    /**
     * 规范字体
     *
     * @param htmlstr  html字符串
     * @param fontName 字体名称
     * @return html字符串
     */
    private static String validateFontFamily(String htmlstr, Map<String, String> fontName) {
        StringBuilder buff = new StringBuilder(htmlstr);
        int begin = buff.indexOf("font-family:", 0);// 获取开始位置
        while (begin > -1) {
            int end = buff.indexOf(";", begin);
            String family = buff.substring(begin + 12, end).trim();
            if (!fontName.containsKey(family)
                    && !fontName.containsValue(family)) {
                String defaultFontFamily = "SimSun";
                buff = buff.replace(begin + 12, end, defaultFontFamily);
                end = end + defaultFontFamily.length() - family.length();
            }
            begin = buff.indexOf("font-family:", end);
        }
        String tmp = buff.toString();
        for (Entry<String, String> entry : fontName.entrySet()) {
            tmp = tmp.replace("'" + entry.getKey() + "'", entry.getValue());
            tmp = tmp.replace(entry.getKey(), entry.getValue());
        }
        return tmp;
    }

    /**
     * 规范图片文件引用路径
     *
     * @param htmlstr html字符串
     * @return html字符串
     */
    private static String parseImgSrc(String htmlstr) {
        return htmlstr.replace("src=\"/", "src=\"");
    }

    /**
     * 转意html字符
     *
     * @param htmlstr html字符串
     * @return html字符串
     */
    private static String buildHtmlStr(String htmlstr) {
        String tmp = htmlstr;
        String webRootAdsPath = CommonTools.getWebRootAbsPath();
        tmp = tmp.replace("&nbsp;", " ").replace("rowSpan", "rowspan")
                .replace("colSpan", "colspan").replace("&ldquo;", "&quot;")
                .replace("&rdquo;", "&quot;")
                .replace("&webrootabspath&", webRootAdsPath);
        return tmp;
    }

    /**
     * 处理自闭和标签
     *
     * @param content html字符串
     * @return html字符串
     */
    private static String processTag(String content) {
        String retContent = content;
        for (String tag : tags) {
            retContent = retContent.replace("</" + tag + ">", "");
        }
        StringBuilder buff = new StringBuilder(retContent);
        for (String tag : tags) {
            int begin = buff.indexOf("<" + tag, 0);// 获取标签开始位置
            while (begin > -1) {
                int end = buff.indexOf(">", begin);
                buff = buff.replace(end, end + 1, "/>");
                end += 1;
                begin = buff.indexOf("<" + tag, end);
            }
        }
        return buff.toString();
    }

    /**
     * 规范HTML字符串
     *
     * @param content html字符串
     * @return html字符串
     */
    static String parseHTML(String content) {
        org.jsoup.nodes.Document doc = Jsoup.parse(content);
        Element firstStyle = doc.getElementsByTag("style").first();
        if (firstStyle != null) {
            firstStyle.prepend("body{font-family:SimSun;}");
        } else {
            doc.getElementsByTag("head").first().prepend("<style type=\"text/css\">body{font-family:SimSun;}</style>");
        }
        String result = doc.html();
        result = processTag(result);
        result = validateFontFamily(result, FontLoader.fontName);
        result = parseImgSrc(result);
        result = buildHtmlStr(result);
        return result;
    }
}
