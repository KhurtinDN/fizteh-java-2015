/**
 * Created by Владимир on 19.12.2015.
 */

class RightNameResolver
{
    static final String REGEX = "[A-Za-z0-9_-]*";
    public static Boolean isGood(String name) {
        return name.matches(REGEX);
    }
}
