package ru.yandex.practicum;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.io.DirectoryScanner;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Mojo(name = "translate")
public class TranslatorMOJO extends AbstractMojo {

    @Parameter(property = "source.language")
    private String sourceLang;

    @Parameter(property = "target.language")
    private String targetLang;

    @Parameter(defaultValue = "true")
    private boolean translateJavadoc;

    @Parameter(defaultValue = "true")
    private boolean translateBlockComments;

    @Parameter(defaultValue = "true")
    private boolean translateLineComments;

    @Parameter(defaultValue = "false")
    private boolean translateStrings;

    @Parameter(defaultValue = "**/*.java")
    private List<String> includes;

    @Parameter
    private String apiKey;

    @Parameter
    private List<String> excludes;

    @Component
    private MavenProject mavenProject;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        DirectoryScanner scanner  = new DirectoryScanner();
        scanner.setBasedir(mavenProject.getBasedir());
        scanner.setIncludes(includes.toArray(new String[includes.size()]));
        scanner.setCaseSensitive(false);
        scanner.scan();
        String[] files = scanner.getIncludedFiles();
        System.out.println(String.join(" , ",files));

        for (String file : files) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                StringBuilder sb = new StringBuilder();
                while (br.ready()){
                    String line = br.readLine();
                    sb.append(line + "\n");
                }
//                Files.createDirectory(Paths.get(mavenProject.getBuild().getDirectory()));
//                BufferedWriter bf = new BufferedWriter();
                System.out.println(sb.toString());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
