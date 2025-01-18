package me.rejomy.randomrush.util.io;

import lombok.experimental.UtilityClass;
import me.rejomy.randomrush.RandomRushAPI;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

@UtilityClass
public class FileUtil {

    public File getOrLoadFromJar(File directory, String name) {
        File file = new File(directory, name);

        if (!file.exists()) {
            RandomRushAPI.INSTANCE.getPlugin().saveResource(name, false);
        }

        return file;
    }

    public boolean deleteFile(File path) {
        if(path.exists()) {
            File[] files = path.listFiles();

            for (File file : files) {
                if (file.isDirectory())
                    deleteFile(file);
                else
                    file.delete();
            }
        }

        return(path.delete());
    }

    public void copyWorld(File source, File target){
        try {
            ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
            if(!ignore.contains(source.getName())) {
                if(source.isDirectory()) {
                    if(!target.exists())
                        target.mkdirs();
                    String files[] = source.list();
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyWorld(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {

        }
    }
}
