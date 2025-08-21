package ru.yandex.practicum;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

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

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

    }
}
