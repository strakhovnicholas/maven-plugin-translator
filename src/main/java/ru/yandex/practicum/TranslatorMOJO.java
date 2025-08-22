package ru.yandex.practicum;

import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.LineComment;
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

        for (String file : files) {
            try {
                FileInputStream in = new FileInputStream(Paths.get(file).toFile());
                CompilationUnit compilationUnit = StaticJavaParser.parse(in);
                List<Comment> comments = compilationUnit.getAllContainedComments();
                comments.addAll(compilationUnit.getOrphanComments());
                for (Comment comment : comments) {
                    if (comment.isLineComment()) {
                        Node parent = comment.getCommentedNode().orElse(null);
                        if (parent != null) {
                            parent.setComment(new LineComment("Replaced line"));
                        } else {
                            comment.setContent("Replaced line");
                        }
                    }

                }

                FileOutputStream out = new FileOutputStream(file);
                out.write(compilationUnit.toString().getBytes());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
