package com.example.robocontrol.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Duy M. Nguyen on 6/13/2020.
 */
public class CommandUtils {

    public final static String F = "F";
    public final static String B = "B";
    public final static String L = "L";
    public final static String R = "R";
    public final static String C = "C";
    public final static String M = "M";
    public final static String P = "P";
    public final static String E = "E";
    public final static String S = "S";
    public final static String n = "n";
    public final static String N = "N";
    public final static String y = "y";
    public final static String Y = "Y";
    public final static String t = "t";
    public final static String T = "T";
    public final static String u = "u";
    public final static String U = "U";
    public final static String o = "o";
    public final static String O = "O";

    public static Map<String, String> command = new HashMap<String, String>() {
        {
            // Tất cả các câu lệnh viết bằng chữ thường

            // Tiếng Việt
            put("tiến", F);
            put("tiến lên", F);
            put("lùi", B);
            put("lùi lại", B);
            put("quay trái", L);
            put("quay phải", R);
            put("bật đèn", Y);
            put("tắt đèn", y);
            put("bật còi", T);
            put("tắt còi", t);
            put("dừng", S);

            // Tiếng Anh
        }
    };

    public static String toSingleCommand(String string) {
        String singleCommand = command.get(string.trim());
        return singleCommand == null ? "" : singleCommand;
    }

    public static String toCommand(String string) {
        StringBuilder finalCmd = new StringBuilder();
        String[] commandWords = string.toLowerCase().split(" ");
        for (String commandWord : commandWords) {
            String cmd = command.get(commandWord.trim());
            if (cmd != null)
                finalCmd.append(command.get(commandWord.trim()));
        }
        return String.valueOf(finalCmd);
    }
}
