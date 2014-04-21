package us.kutz.a.mojos.branchver;

import org.junit.Assert;
import org.testng.annotations.Test;
import us.kutz.a.mojos.branchver.BranchName;
import us.kutz.a.mojos.branchver.SetBranchVerMojo;

public class SetBranchVerMojoTest
{
    @Test
    public void skipBranchTest()
    {
        SetBranchVerMojo mojo = new SetBranchVerMojo();
        BranchName bn = BranchName.parse("hotfix/2.1.2");
        String bns = bn.toString();
        boolean skipped = mojo.skipBranch(bns);
        Assert.assertTrue(skipped);
    }
}
