package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类操作工具类
 * Created by liq on 2018/3/25.
 */
public final class ClassUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtil.class);

    /**
     * 获取类加载器
     */
    public static ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 加载类
     */
    public static Class<?> loadClass(String className, boolean isInitialized){
        Class<?> cls;
        try{
            cls = Class.forName(className, isInitialized, getClassLoader());
        } catch (ClassNotFoundException e) {
            LOGGER.error("load class failure", e);
            throw new RuntimeException(e);
        }
        return cls;
    }

    /**
     * 获取指定包名下的所有类
     */
    public static Set<Class<?>> getClassSet(String packageName){
        HashSet<Class<?>> classSet = new HashSet<Class<?>>();
        if (packageName == null){
            return classSet;
        }
        try{
            //获取当前目录下（不递归）的所有文件和目录的url
            Enumeration<URL> urls = getClassLoader().getResources(packageName.replace(".","/"));
            while(urls.hasMoreElements()){
                URL url = urls.nextElement();
                if (url != null){
                    //JAVA默认提供了对file,ftp,http,https,gopher,jar,mailto,netdoc协议的支持
                    String protocol = url.getProtocol();
                    if (protocol.equals("file")){
                        String packagePath = url.getPath().replaceAll("%20", " ");
                        addClass(classSet, packagePath, packageName);
                    }else if (protocol.equals("jar")){
                        //打开连接获得JarURLConnection类对象
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        if (jarURLConnection != null){
                            JarFile jarFile = jarURLConnection.getJarFile();
                            if (jarFile != null){
                                //较大文件中的每一个单独的文件是通过一个JarEntry引用的。每一个entry是一个JarEntry。该类有getName，getSize，getCompressedSize等方法。
                                Enumeration<JarEntry> jarEntries = jarFile.entries();
                                while (jarEntries.hasMoreElements()){
                                    JarEntry jarEntry = jarEntries.nextElement();
                                    String jarEntryName = jarEntry.getName();
                                    if (jarEntryName.endsWith(".class")){
                                        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
                                        doAddClass(classSet, className);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("get class set failure", e);
            throw new RuntimeException(e);
        }
        return classSet;
    }

    private static void addClass(HashSet<Class<?>> classSet, String packagePath, String packageName) {
        //listFiles()返回某个目录下所有文件和目录的绝对路径，返回的是File数组
        File[] files = new File(packagePath).listFiles(new FileFilter() {
            public boolean accept(File file) {
                //要么是.class文件要么是目录
                return (file.isFile() && file.getName().endsWith(".class") ||
                file.isDirectory());
            }
        });

        for (File file : files) {
            String fileName = file.getName();
            //是.class文件
            if (file.isFile()){
                String className = fileName.substring(0, fileName.lastIndexOf("."));
                className = packageName + "." + className;
                doAddClass(classSet, className);
            } else{
              //是目录
               String subPackagePath =  fileName;
               subPackagePath = packagePath+"/"+subPackagePath;

               String subPackageName = fileName;
               subPackageName = packageName+"."+subPackageName;

               addClass(classSet, subPackagePath, subPackageName);
            }
        }
    }

    private static void doAddClass(HashSet<Class<?>> classSet, String className) {
        Class<?> cls = loadClass(className, false);
        classSet.add(cls);
    }


}
