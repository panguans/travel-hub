package com.travel.hub.security;

/**
 * Constants for Spring Security authorities.
 * <p>
 * This class contains constants representing different roles or authorities that can be assigned to users.
 * These constants are used to define the roles that are used in the application for authorization purposes.
 * This is the spec of all Authorities in the app: <a href="https://github.com/panguans/travel-hub/wiki/%235-Define-All-Application-Roles-&-Permissions">#5 Define All Application Roles & Permissions</a>
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";
    public static final String APP_ADMIN = "ROLE_APP_ADMIN";
    public static final String AGENCY = "ROLE_AGENCY";
    public static final String GUIDE = "ROLE_GUIDE";
    public static final String SUPPORT = "ROLE_SUPPORT";
    public static final String MODERATOR = "ROLE_MODERATOR";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    private AuthoritiesConstants() {}
}
