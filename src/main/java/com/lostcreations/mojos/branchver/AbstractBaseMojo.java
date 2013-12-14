package com.lostcreations.mojos.branchver;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.twdata.maven.mojoexecutor.MojoExecutor.ExecutionEnvironment;

/**
 * A base class for this project's MOJOs.
 * 
 * @author akutz
 * 
 */
public abstract class AbstractBaseMojo extends AbstractMojo
{
    protected final static String VERSIONS_GID = "org.codehaus.mojo";
    protected final static String VERSIONS_AID = "versions-maven-plugin";

    @Parameter(alias = "versions.version",
        property = "versions.version",
        defaultValue = "2.1")
    protected String versionsVersion;

    @Parameter(alias = "dryRun", property = "dryRun", defaultValue = "false")
    protected boolean dryRun;

    @Component
    protected MavenProject project;

    @Component
    protected MavenSession session;

    @Component
    protected BuildPluginManager pluginManager;

    protected Plugin getVersionsPlugin()
    {
        return plugin(
            groupId(VERSIONS_GID),
            artifactId(VERSIONS_AID),
            version(this.versionsVersion));
    }

    protected ExecutionEnvironment getExeEnv()
    {
        return executionEnvironment(
            this.project,
            this.session,
            this.pluginManager);
    }
}
