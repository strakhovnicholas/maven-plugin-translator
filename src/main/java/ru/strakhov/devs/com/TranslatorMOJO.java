package ru.strakhov.devs.com;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.comments.LineComment;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.utils.io.DirectoryScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.strakhov.devs.com.translator.Translator;
import ru.strakhov.devs.com.translator.implementation.LibreTranslateTranslator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
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
    private List<String> excludes;

    @Component
    private MavenProject mavenProject;

    private static final Logger log = LoggerFactory.getLogger(TranslatorMOJO.class);

    private final Translator translatorAPI = new LibreTranslateTranslator();

    @Override
    public void execute() throws MojoExecutionException {
        log.info("Starting translation from {} to {}", sourceLang, targetLang);
        log.debug("Configuration: javadoc={}, blockComments={}, lineComments={}, strings={}",
                translateJavadoc, translateBlockComments, translateLineComments, translateStrings);

        List<Path> files = scanSourceFiles();
        log.info("Found {} Java files to process", files.size());

        for (Path file : files) {
            processFile(file);
        }
    }

    private List<Path> scanSourceFiles() {
        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(mavenProject.getBasedir());
        scanner.setIncludes(includes.toArray(new String[0]));
        if (excludes != null) {
            scanner.setExcludes(excludes.toArray(new String[0]));
        }
        scanner.setCaseSensitive(false);
        scanner.scan();

        return Arrays.stream(scanner.getIncludedFiles())
                .map(f -> Paths.get(mavenProject.getBasedir().toString(), f))
                .toList();
    }

    private void processFile(Path filePath) throws MojoExecutionException {
        try (FileInputStream fis = new FileInputStream(filePath.toFile());
             InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8)) {

            CompilationUnit cu = StaticJavaParser.parse(isr);
            List<Comment> comments = collectComments(cu);

            log.info("Processing file {} ({} comments)", filePath, comments.size());
            translateComments(comments, filePath.toString());

            saveCompilationUnit(cu, filePath);

        } catch (IOException e) {
            log.error("Failed to process file: {}", filePath, e);
            throw new MojoExecutionException("Error processing file " + filePath, e);
        }
    }

    private List<Comment> collectComments(CompilationUnit cu) {
        List<Comment> comments = new ArrayList<>(cu.getAllContainedComments());
        comments.addAll(cu.getOrphanComments());
        return comments;
    }


    private void translateComments(List<Comment> comments, String fileName) {
        for (Comment comment : comments) {
            translateLineComment(comment, fileName);
        }
    }

    private void translateLineComment(Comment comment, String fileName) {
        Node parent = comment.getCommentedNode().orElse(null);
        String translated = translatorAPI.translate(comment.getContent(), sourceLang, targetLang);

        LineComment translatedLineComment = new LineComment(translated);
        if (parent != null) {
            parent.setComment(translatedLineComment);
            log.info("Replaced attached line comment in {}", fileName);
        } else {
            comment.setContent(translatedLineComment.getContent());
            log.info("Replaced orphan line comment in {}", fileName);
        }
    }

    private void saveCompilationUnit(CompilationUnit cu, Path filePath) throws IOException {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(filePath.toFile()), StandardCharsets.UTF_8)) {
            writer.write(cu.toString());
        }
        log.info("File saved: {}", filePath);
    }
}

