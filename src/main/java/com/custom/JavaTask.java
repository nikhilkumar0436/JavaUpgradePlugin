package com.custom;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.ReplaceRegExp;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class JavaTask extends DefaultTask {

    @TaskAction
    public void javaUpgradeTask() throws IOException {

        //spring version to be upgraded to
        String springVersion = "6.1.14";

        Project antProject = new Project();
        antProject.init();
        Map<String, String> dependenciesMap = getUpgradableDependencies(springVersion);

        Files.walkFileTree(Path.of(getProject().getProjectDir().getPath()), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String fileName = file.getFileName().toString();
                if (fileName.endsWith(".gradle")) {
                    // Process text or Java files
                    for (Map.Entry<String, String> dependencies : dependenciesMap.entrySet()) {
                        ReplaceRegExp replaceRegExp = new ReplaceRegExp();
                        replaceRegExp.setByLine(true);
                        replaceRegExp.setFile(new File(file.toUri()));
                        replaceRegExp.setFlags("i");
                        replaceRegExp.setMatch(dependencies.getKey());
                        replaceRegExp.setReplace(dependencies.getValue());
                        replaceRegExp.setProject(antProject);
                        replaceRegExp.execute();
                    }
                    System.out.println("Found a file to process: " + file);
                }
                return FileVisitResult.CONTINUE;
            }
        });


    }

    //add the dependency for what you want, if it is not spring related then simply mention the target version
    @NotNull
    private static Map<String, String> getUpgradableDependencies(String springVersion) {
        Map<String, String> dependenciesMap = new HashMap<>();
        dependenciesMap.put("org\\.springframework:spring-core:\\d+\\.\\d+\\.\\d+(-[a-zA-Z0-9]+)?(\\.[a-zA-Z0-9]+)?", "org.springframework:spring-core:%s".formatted(springVersion));
        dependenciesMap.put("org\\.springframework:spring-jdbc:\\d+\\.\\d+\\.\\d+(-[a-zA-Z0-9]+)?(\\.[a-zA-Z0-9]+)?", "org.springframework:spring-jdbc:%s".formatted(springVersion));
        return dependenciesMap;
    }


}
