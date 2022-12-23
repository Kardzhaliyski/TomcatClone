package com.github.kardzhaliyski.tomcatclone;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;

public class Test2 {
    public static void main(String[] args) throws Exception{
        test1 test1 = new test1();
        test1.t();

    }

    public static class test1 {
        public void t() throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
            Path of = Path.of("C:\\Users\\Zdravko\\Desktop\\wars\\MavenTomcatDemo.war\\WEB-INF\\classes");
            File file = of.toFile();
            System.out.println(file.exists());
            URL url = file.toURI().toURL();
            URLClassLoader cl = URLClassLoader.newInstance(new URL[]{url});
//            System.out.println(Arrays.toString(cl.findResources()));

//            System.out.println(cl.getDefinedPackages());
            Class<?> aClass = cl.loadClass("com.github.kardzhaliyski.blog.LoginServlet");
//            Constructor<?> constructor = aClass.getConstructor();
//            Object o = constructor.newInstance();
//            InputStream is = cl.getResourceAsStream("WEB-INF/web.xml");
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }

//            InputStream webxml = cl.getResourceAsStream("WEB-INF\\web.xml");
//            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.openStream()));
//            String line = null;
//            while ((line = bufferedReader.readLine()) != null)
            System.out.println();

        }
    }

}
