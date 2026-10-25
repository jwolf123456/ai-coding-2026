package com.hiking.app.generator;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CodeGeneratorUtil {
    // PROJECT_BASE_DIR 为 动态获取CodeGeneratorUtil所在项目根目录
    private static final String PROJECT_BASE_DIR = "D:/ai-coding-2026/hiking-app";
    private static final String DB_URL_PREFIX = "jdbc:mysql://localhost:3306/";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "123456";
    private static final String PARENT_PACKAGE = "com.hiking.app";

    public static void generate(String subProject, String dbName, String tableNames) {
        subProject = promptIfBlank(subProject, "请输入子项目文件夹名(如 user-center): ");
        dbName = promptIfBlank(dbName, "请输入数据库名(如 hiking_user): ");
        tableNames = promptIfBlank(tableNames, "请输入表名(逗号分割, 如 t_user,t_user_role): ");

        if (subProject.isBlank() || dbName.isBlank() || tableNames.isBlank()) {
            System.out.println("子项目文件夹名、数据库名和表名不能为空");
            return;
        }
        String moduleName = subProject.endsWith("-center")
                ? subProject.substring(0, subProject.length() - "-center".length())
                : subProject;

        String javaOutputDir = PROJECT_BASE_DIR + "/" + subProject + "/src/main/java";
        String mapperXmlDir = PROJECT_BASE_DIR + "/" + subProject + "/src/main/resources/mapper";
        String dbUrl = DB_URL_PREFIX + dbName
                + "?useSSL=false&serverTimezone=UTC&characterEncoding=utf8&allowPublicKeyRetrieval=true";
        String superEntityClass = PARENT_PACKAGE + "." + moduleName + ".entity.BaseEntity";

        String[] tables = tableNames.split(",");

        Set<Path> beforeFiles = snapshotFiles(javaOutputDir, mapperXmlDir);
        Map<Path, Long> beforeTimestamps = snapshotTimestamps(javaOutputDir, mapperXmlDir);

        DataSourceConfig.Builder dataSourceConfig = new DataSourceConfig.Builder(dbUrl, DB_USERNAME, DB_PASSWORD);

        GlobalConfig.Builder globalConfig = new GlobalConfig.Builder()
                .outputDir(javaOutputDir)
                .author("hiking-app")
                .dateType(DateType.TIME_PACK)
                .disableOpenDir();

        PackageConfig.Builder packageConfig = new PackageConfig.Builder(PARENT_PACKAGE, moduleName)
                .pathInfo(Collections.singletonMap(OutputFile.xml, mapperXmlDir));

        StrategyConfig.Builder strategyConfig = new StrategyConfig.Builder()
                .addInclude(tables)
                .addTablePrefix("t_");

        strategyConfig.entityBuilder()
                .superClass(superEntityClass)
                .addSuperEntityColumns("id", "create_time", "update_time", "deleted")
                .enableLombok()
                .enableTableFieldAnnotation()
                .enableFileOverride() // 只允许覆盖已存在表实体类
                .logicDeleteColumnName("deleted");

        strategyConfig.mapperBuilder()
                .enableMapperAnnotation();

        strategyConfig.serviceBuilder()
                .formatServiceFileName("%sService")
                .formatServiceImplFileName("%sServiceImpl");

        strategyConfig.controllerBuilder()
                .enableRestStyle();

        AutoGenerator generator = new AutoGenerator(dataSourceConfig.build());
        generator.global(globalConfig.build());
        generator.packageInfo(packageConfig.build());
        generator.strategy(strategyConfig.build());
        generator.execute(new FreemarkerTemplateEngine());

        Set<Path> afterFiles = snapshotFiles(javaOutputDir, mapperXmlDir);
        Set<Path> newFiles = new HashSet<>(afterFiles);
        newFiles.removeAll(beforeFiles);

        Set<Path> overwrittenFiles = new HashSet<>();
        for (Map.Entry<Path, Long> entry : beforeTimestamps.entrySet()) {
            Path file = entry.getKey();
            if (Files.exists(file)) {
                try {
                    long currentModified = Files.getLastModifiedTime(file).toMillis();
                    if (currentModified != entry.getValue()) {
                        overwrittenFiles.add(file);
                    }
                } catch (IOException ignored) {
                }
            }
        }

        if (!overwrittenFiles.isEmpty()) {
            System.out.println("[WARN] 以下文件被覆盖:");
            overwrittenFiles.stream().sorted().forEach(f -> System.out.println("  " + f));
        }

        System.out.println("代码生成完成 -> " + subProject + " | " + dbName + " | " + tableNames);
        if (newFiles.isEmpty() && overwrittenFiles.isEmpty()) {
            System.out.println("无任何生成");
        }
        if (!newFiles.isEmpty()) {
            System.out.println("生成文件列表:");
            newFiles.stream().sorted().forEach(f -> System.out.println("  " + f));
        }
    }

    public static void main(String[] args) {

        String subProject = args.length > 0 ? args[0] : "";
        String dbName = args.length > 1 ? args[1] : "";
        String tableNames = args.length > 2 ? args[2] : "";
        generate(subProject, dbName, tableNames);
    }

    private static String promptIfBlank(String value, String prompt) {
        if (value != null && !value.isBlank()) {
            return value;
        }
        Scanner scanner = new Scanner(System.in);
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static Set<Path> snapshotFiles(String... dirs) {
        Set<Path> files = new HashSet<>();
        for (String dir : dirs) {
            Path path = Paths.get(dir);
            if (Files.exists(path)) {
                try {
                    Files.walk(path).filter(Files::isRegularFile).forEach(files::add);
                } catch (IOException ignored) {
                }
            }
        }
        return files;
    }

    private static Map<Path, Long> snapshotTimestamps(String... dirs) {
        Map<Path, Long> timestamps = new HashMap<>();
        for (String dir : dirs) {
            Path path = Paths.get(dir);
            if (Files.exists(path)) {
                try {
                    Files.walk(path).filter(Files::isRegularFile).forEach(f -> {
                        try {
                            timestamps.put(f, Files.getLastModifiedTime(f).toMillis());
                        } catch (IOException ignored) {
                        }
                    });
                } catch (IOException ignored) {
                }
            }
        }
        return timestamps;
    }
}