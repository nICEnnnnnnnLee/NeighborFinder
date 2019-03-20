package top.nicelee.ipscanner.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import top.nicelee.ipscanner.MainActivity;

public class Config {
    static Pattern notePattern = Pattern.compile("^([^ ]+)(.*)$");

    public static void load(HashMap<String, String> macNoteMap) {
        BufferedReader buReader = null;
        try{
            File folder = new File(MainActivity.path);
            File file = new File(folder, "notes");
            buReader = new BufferedReader(new FileReader(file));
            String line = null;
            while ( (line = buReader.readLine()) != null){
                Matcher matcher = notePattern.matcher(line);
                matcher.find();
                macNoteMap.put(matcher.group(1), matcher.group(2).trim());
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                buReader.close();
            } catch (Exception e) {
            }
        }
    }

    public static void save(HashMap<String, String> macNoteMap) {
        BufferedWriter buWriter = null;
        try {
            File folder = new File(MainActivity.path);
            File file = new File(folder, "notes");
            //System.out.println(MainActivity.path);
            buWriter = new BufferedWriter(new FileWriter(file, false));
            for(Map.Entry entry: macNoteMap.entrySet()){
                buWriter.write(entry.getKey().toString());
                buWriter.write(" ");
                buWriter.write(entry.getValue().toString());
                buWriter.newLine();
            }
            buWriter.flush();
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            try {
                buWriter.close();
            } catch (Exception e) {
            }
        }
    }
}
