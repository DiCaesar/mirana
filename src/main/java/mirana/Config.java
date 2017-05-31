package mirana;

public class Config {

    /**
     * 实体和枚举文件路径
     */
    public static String[] classPackagePath = {
    };

    /**
     * 接口文件路径
     */
    public static String[] interfacePath = {
    };

    /**
     * 限制服务列表，不会直接生成全部方法
     */
    public static String[] serviceList = {
    };

    /**
     * 限制服务内，允许生成的方法列表
     */
    public static String[] methodWhiteList = {
    };

    /**
     * 系统配置，请勿修改
     */
    public static String classPath = "../../../../build/classes";

    /**
     * 系统配置，请勿修改
     */
    public static String mdOutputPath = "build/mdFiles";
}
