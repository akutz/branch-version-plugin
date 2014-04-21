package us.kutz.a.mojos.branchver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.codehaus.plexus.util.StringUtils;

public class BranchName
{
    private static final Pattern BRANCH_NAME_PATT = Pattern
        .compile("(?i)^(?:refs/heads/)?([^/-]+)(?:[/-](.+))?$");

    private static final Pattern VERSION_PATT =
        Pattern
            .compile("^(\\d+\\.\\d+(?:\\.\\d+){0,2}(?:-(?:SNAPSHOT|RELEASE))?)-(.+)$");

    private static final Pattern VERSION_SUFFIX_PATT = Pattern
        .compile("(.+)-(?:SNAPSHOT|RELEASE)");

    /**
     * capture 1 - type capture 2 - version capture 3 - ticket capture 4 - text
     */
    /*
     * private static final Pattern BRANCH_NAME_PATT2 = Pattern
     * .compile("^(?:refs/heads/)?(?:(\\w+)[/-]?)" +
     * "(\\d+\\.\\d+(?:\\.\\d+){1,2})?(?:-?(\\w+-\\d+))?" +
     * "(?:-?(.+?))?(-SNAPSHOT)?$");
     */

    private final String type;
    private final String name;

    // private final String version;
    // private final String ticket;
    // private final String text;

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

        Matcher version_match = VERSION_PATT.matcher(bname);
        if (version_match.matches())
        {
            bname = version_match.group(2);
        }

        Matcher version_suffix_match = VERSION_SUFFIX_PATT.matcher(bname);
        if (version_suffix_match.matches())
        {
            bname = version_suffix_match.group(1);
        }

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
