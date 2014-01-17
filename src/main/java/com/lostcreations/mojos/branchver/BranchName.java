package com.lostcreations.mojos.branchver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.plexus.util.StringUtils;

public class BranchName
{
    private static final Pattern BRANCH_NAME_PATT = Pattern
        .compile("(?i)^(?:refs/heads/)?([^/]+)(?:/([^/]+))?$");

    private final String type;
    private final String name;

    private BranchName(String name)
    {
        this(null, name);
    }

    private BranchName(String type, String name)
    {
        this.type = type;
        this.name = name;
    }

    public static BranchName parse(String name)
    {
        Matcher m = BRANCH_NAME_PATT.matcher(name);
        if (!m.matches()) return null;
        String g1 = m.group(1);
        String g2 = m.group(2);
        String btype = StringUtils.isEmpty(g2) ? null : g1;
        String bname = StringUtils.isEmpty(g2) ? g1 : g2;

        return new BranchName(btype, bname);
    }

    public boolean isTyped()
    {
        return StringUtils.isNotEmpty(this.type);
    }

    public String getName()
    {
        return this.name;
    }

    public String getType()
    {
        return this.type;
    }

    @Override
    public String toString()
    {
        if (!isTyped()) return this.name;
        else return String.format("%s/%s", this.type, this.name);
    }
}
