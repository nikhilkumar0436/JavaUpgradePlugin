package com.custom;

//import org.apache.tools.ant.Project;
 import org.gradle.api.Plugin;
 import org.gradle.api.Project;


public class CustomPlugin implements Plugin<Project>{
 @Override
 public void apply(Project target) {
  target.getTasks().create("upGradeDependencies",JavaTask.class);
 }
}
