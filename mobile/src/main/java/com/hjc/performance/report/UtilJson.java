package com.hjc.performance.report;

import com.alibaba.fastjson.JSONArray;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Created by hujiachun on 16/9/25.
 */

public class UtilJson {




    public static void writeToJson(String filePath, JSONArray object) throws IOException {
        File file = new File(filePath);
        char[] stack = new char[1024];
        int top = -1;

        String string = object.toString();

        StringBuffer sb = new StringBuffer();
        char[] charArray = string.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            if ('{' == c || '[' == c) {
                stack[++top] = c;
                sb.append("\n" + charArray[i] + "\n");
                for (int j = 0; j <= top; j++) {
                    sb.append("\t");
                }
                continue;
            }
            if ((i + 1) <= (charArray.length - 1)) {
                char d = charArray[i + 1];
                if ('}' == d || ']' == d) {
                    top--;
                    sb.append(charArray[i] + "\n");
                    for (int j = 0; j <= top; j++) {
                        sb.append("\t");
                    }
                    continue;
                }
            }
            if (',' == c) {
                sb.append(charArray[i] + "");
                for (int j = 0; j <= top; j++) {
                    sb.append("");
                }
                continue;
            }
            sb.append(c);
        }
        Writer write = new FileWriter(file);
        write.write(sb.toString());
        write.flush();
        write.close();
    }

}
