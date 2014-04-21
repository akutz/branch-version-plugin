package us.kutz.a.mojos.branchver;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;
import java.io.File;
import java.util.regex.Pattern;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

/**
 * Sets the version of a POM file based on the current branch.
 */
@Mojo(name = "set",
    aggregator = true,
    requiresDirectInvocation = true,
    requiresProject = true,
    threadSafe = true,
    requiresOnline = false)
@Execute(goal = "set", phase = LifecyclePhase.VALIDATE)
public class SetBranchVerMojo extends AbstractBaseMojo
{
    private final static Pattern DEFAULT_SKIP_BRANCHES_PATT = Pattern
        .compile("(?i)master|develop|(?:(?:release|hotfix)/.*)");

    private final static String TC_BUILD_NUM_FMT =
        "##teamcity[buildNumber '%s']";

    @Parameter(alias = "branch", property = "branch")
    private String branch;

    @Parameter(alias = "buildNumber", property = "buildNumber")
    private String buildNumber;

    @Parameter(alias = "skipBranchesRegex", property = "skipBranchesRegex")
    private String skipBranchesRegex;

    @Override
    public void execute() throws MojoExecutionException
    {
        BranchName branchName = getBranchName();

        if (skipBranch(branchName.toString()))
        {
            reportVersion(super.project.getVersion());
            return;
        }

        String newVersion = getNewVersion(branchName.getName());

        if (super.dryRun)
        {
            getLog().info(String.format("dryRun=true"));
        }
        else
        {
            setVersion(newVersion);
        }

        reportVersion(newVersion);
    }

    private String getNewVersion(String branchName)
        throws MojoExecutionException
    {
        String v = this.project.getVersion();

        if (StringUtils.isEmpty(v))
        {
            throw new MojoExecutionException("version is null or empty");
        }

        // Parse the old version.
        Version ov = Version.parse(v);

        // Print the old version as a string using just its supplied numeric
        // components.
        String ovn = ov.toString(ov.getNumberOfComponents());
        String ovp = ov.getPrefix();
        String ovs = ov.getSuffix();

        // Build the new version.
        String nv = String.format("%s%s-%s%s", ovp, ovn, branchName, ovs);

        getLog().info(String.format("new version='%s'", nv));

        return nv;
    }

    private void reportVersion(String version)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(version);

        if (StringUtils.isNotEmpty(this.buildNumber))
        {
            sb.append(" Build ");
            sb.append(this.buildNumber);
        }

        getLog().info(String.format(TC_BUILD_NUM_FMT, sb.toString()));
    }

    private BranchName getBranchName() throws MojoExecutionException
    {
        if (StringUtils.isNotEmpty(this.branch))
        {
            return BranchName.parse(this.branch);
        }

        // Get the project's base directory.
        File baseDir = super.project.getBasedir();
        File gitDir = new File(baseDir, ".git");
        if (!gitDir.exists())
        {
            throw new MojoExecutionException(String.format(
                "'%s' is not a valid git repository",
                baseDir));
        }

        String branchName = getCurrentBranchName(gitDir);

        if (StringUtils.isEmpty(branchName))
        {
            throw new MojoExecutionException("branch name unavailable");
        }

        return BranchName.parse(branchName);
    }

    public boolean skipBranch(String branchName)
    {
        Pattern skipPatt;

        if (StringUtils.isEmpty(this.skipBranchesRegex))
        {
            skipPatt = DEFAULT_SKIP_BRANCHES_PATT;
        }
        else
        {
            skipPatt =
                Pattern.compile(
                    this.skipBranchesRegex,
                    Pattern.CASE_INSENSITIVE);
        }

        if (skipPatt.matcher(branchName).matches())
        {
            getLog().info(
                String.format(
                    "current branch='%s' matches no-op branch='%s'",
                    branchName,
                    skipPatt.pattern()));

            return true;
        }

        return false;
    }

    private void setVersion(String newVersion) throws MojoExecutionException
    {
        executeMojo(
            getVersionsPlugin(),
            "set",
            getConfig(newVersion),
            getExeEnv());

        getLog().info(String.format("executed set version='%s'", newVersion));
    }

    private Xpp3Dom getConfig(String newVersion)
    {
        return configuration(element(name("newVersion"), newVersion));
    }

    public String getCurrentBranchName(File gitDir)
    {
        Repository r = null;

        try
        {
            r = new FileRepository(gitDir);
            return r.getBranch();
        }
        catch (Exception e)
        {
            getLog().error(
                String.format("error getting branch name w/ '%'", gitDir),
                e);

            return null;
        }
        finally
        {
            if (r != null)
            {
                try
                {
                    r.close();
                }
                catch (Exception e)
                {
                    // Do nothing
                }
            }
        }
    }
}
