package pers.acp.test.application.test;

import java.io.File;

/**
 * @author zhangbin by 2018-2-25 15:23
 * @since JDK 11
 */
public class Test {

    public static void main(String[] args) {
        File fold = new File("D:\\照片\\待分类");
        int count = 0;
        if (fold.exists()) {
            File[] files = fold.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().contains("(1)")) {
                        file.delete();
                        count++;
                    }
                }
            }
        }
        System.out.println("delete " + count + " files");
    }

}
