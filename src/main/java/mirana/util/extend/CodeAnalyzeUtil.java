package mirana.util.extend;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import mirana.Config;
import mirana.Data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeAnalyzeUtil {

    private Map<String, String> entityTypeMap = new HashMap<>();

    private Map<String, Map<String, String>> methodParamTypeMap = new HashMap<>();

    private Map<String, String> methodReturnTypeMap = new HashMap<>();

    public void initEntity(String className) {
        try {
            entityTypeMap.clear();
            BufferedReader reader = new BufferedReader(new FileReader(getSubFile(className, Config.classPackagePath)));
            String line;
            while ((line = reader.readLine()) != null) {
                String entityRegex = "(private)? +(?<type>\\w+|\\w+<[<>\\w ,]+>) +(?<name>\\w+);";
                Pattern pattern = Pattern.compile(entityRegex);
                Matcher matcher = pattern.matcher(line);
                boolean result = matcher.find();
                if (result) {
                    //添加类型到扩展队列
                    Data.putAllClass(matcher.group("type"));
                    String type = matcher.group("type").replace("<", "\\<").replace(">", "\\>");
                    if (type.equals("return")) {
                        continue;
                    }
                    entityTypeMap.put(matcher.group("name"), type);
                }
            }
        } catch (IOException e) {
        }
    }

    public void initMethod(String className) {
        try {
            methodParamTypeMap.clear();
            methodReturnTypeMap.clear();
            BufferedReader reader = new BufferedReader(new FileReader(getFile(className, Config.interfacePath)));
            String line;
            while ((line = reader.readLine()) != null) {
                //去除Annotation
                String annotation;
                while ((annotation = getAnnotation(line)) != null) {
                    line = line.replace(annotation, "");
                }
                //抓取returnType,methodName,params
                String methodRegex = "(?<returnType>\\w+|\\w+<[<>\\w ,]+>) (?<methodName>\\w+)\\((?<params>.*)\\)( +throws +\\w+)?;";
                Pattern pattern = Pattern.compile(methodRegex);
                Matcher matcher = pattern.matcher(line);
                boolean result = matcher.find();
                if (result) {
                    String returnType = matcher.group("returnType");
                    String methodName = matcher.group("methodName");
                    if (methodNeed(className, methodName)) {
                        methodReturnTypeMap.put(methodName, returnType);
                        Map<String, String> paramTypes = new HashMap<>();
                        //添加类型到扩展队列
                        Data.putAllClass(returnType);

                        String params = matcher.group("params");
                        String param;
                        //逐步抓取单个paramType
                        while ((param = getParam(params)) != null) {
                            String regexParam = "(?<type>\\w+|\\w+<[<>\\w ,]+>) +(?<name>\\w+)";
                            pattern = Pattern.compile(regexParam);
                            matcher = pattern.matcher(params);
                            result = matcher.find();
                            if (result) {
                                //添加类型到扩展队列
                                Data.putAllClass(matcher.group("type"));
                                String type = matcher.group("type").replace("<", "\\<").replace(">", "\\>");
                                String name = matcher.group("name");
                                paramTypes.put(name, type);
                            }
                            params = params.replace(param, "");
                        }
                        methodParamTypeMap.put(methodName, paramTypes);
                    }
                }
            }
        } catch (IOException e) {
        }
    }

    public String getMethodParamType(String method, String param) {
        if (methodParamTypeMap.containsKey(method)) {
            if (methodParamTypeMap.get(method).containsKey(param)) {
                return methodParamTypeMap.get(method).get(param);
            }
        }
        return "";
    }

    public String getMethodReturnType(String method) {
        if (methodReturnTypeMap.containsKey(method)) {
            return methodReturnTypeMap.get(method);
        }
        return "";
    }

    public String getEntityPropertyType(String name) {
        if (entityTypeMap.containsKey(name)) {
            return entityTypeMap.get(name);
        }
        return "";
    }

    public static boolean methodNeed(String className, String methodName) {
        for (String service : Config.serviceList) {
            if (service.equals(className)) {
                boolean contains = false;
                for (String method : Config.methodWhiteList) {
                    if (method.equals(methodName)) {
                        contains = true;
                    }
                }
                return contains;
            }
        }
        return true;
    }

    private File getFile(String className, String[] paths) {
        String fileName = className + ".java";
        for (String path : paths) {
            Pattern pattern = Pattern.compile(fileName);
            Matcher matcher = pattern.matcher(path);
            boolean result = matcher.find();
            if (result) {
                return new File(path);
            }
        }
        return null;
    }

    private File getSubFile(String className, String[] paths) {
        for (String path : paths) {
            File result = searchFile(new File(path), className);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private File searchFile(File file, String className) {
        if (file == null) {
            return null;
        }
        if (file.list() == null) {
            if (file.getName().equals(className + ".java"))
                return file;
            return null;
        }
        for (String fileName : file.list()) {
            File result = searchFile(new File(file.getPath() + "/" + fileName), className);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private static String getAnnotation(String line) {
        String annotationRegex = "(?<annotation>@\\w+(\\([,\\w =\"\\u4e00-\\u9fa5]+\\))?)";
        Pattern pattern = Pattern.compile(annotationRegex);
        Matcher matcher = pattern.matcher(line);
        boolean result = matcher.find();
        if (result) {
            return matcher.group("annotation");
        }
        return null;
    }

    private static String getParam(String params) {
        String methodParamRegex = "(?<param>(\\w+|\\w+<[<>\\w ]+>) +\\w+)";
        Pattern pattern = Pattern.compile(methodParamRegex);
        Matcher matcher = pattern.matcher(params);
        boolean result = matcher.find();
        if (result) {
            return matcher.group("param");
        }
        return null;
    }
}
