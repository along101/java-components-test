package com.yzl.maven.test;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * @author yinzuolong
 */
@Mojo(name = "along101", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class MyPlugin extends AbstractMojo {
    @Parameter(
            property = "sourceDirectory",
            defaultValue = "${project.basedir}/src/main/java")
    private String sourceDirectory;

    @Parameter(
            property = "generatedSourceDirectory",
            defaultValue = "${project.build.directory}/generated-sources/")
    private String generatedSourceDirectory;

    @Parameter(
            defaultValue = "${project}",
            required = true,
            readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (Objects.nonNull(project)) {
            project.addCompileSourceRoot(generatedSourceDirectory);
        }
        String code = "package com.along101;\n" +
                "public class A{\n}";
        File directory = new File(generatedSourceDirectory);
        Path outputDirectory = directory.toPath();
        outputDirectory = outputDirectory.resolve("com/along101");
        try {
            Files.createDirectories(outputDirectory);
            Path outputPath = outputDirectory.resolve("A.java");
            Files.write(outputPath, code.getBytes());
        } catch (IOException e) {
            throw new MojoExecutionException("along101 plugin execute error!", e);
        }
    }
}
