package us.kutz.a.mojos.branchver;

import static org.twdata.maven.mojoexecutor.MojoExecutor.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

/**
 * Rolls back the version set during a previous set invocation if the backup
 * POMs are still available.
 */
@Mojo(name = "unset",
    aggregator = true,
    requiresDirectInvocation = true,
    requiresProject = true,
    threadSafe = true,
    requiresOnline = false)
@Execute(goal = "unset", phase = LifecyclePhase.VALIDATE)
public class UnsetBranchVerMojo extends AbstractBaseMojo
{
    @Override
    public void execute() throws MojoExecutionException
    {
        if (super.dryRun)
        {
            getLog().info(String.format("dryRun=true"));
            return;
        }

        executeMojo(getVersionsPlugin(), "revert", configuration(), getExeEnv());
    }
}
