package controllers.action;

import org.apache.commons.lang3.StringUtils;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import repository.SessionRepository;

/**
 * Class to protect the access to some pages.
 * Only logged user will see the content of that pages.
 */
public class Secured extends Security.Authenticator {

    private final SessionRepository sessionRepository;

    public Secured() {
        this.sessionRepository = SessionRepository.instance;
    }

    /**
     * Returns the logged user, or null if there is none.
     * @param context Http Context object of the actual page.
     */
    @Override
    public String getUsername(Http.Context context) {
        final String userName = context.session().get("user");
        if (StringUtils.isNotBlank(userName) && this.isLoggedIn(userName)) {
            return userName;
        } else {
            return null;
        }
    }

    /**
     * If none user is logged in the app redirects to the login page.
     * @param context Http Context object of the actual page.
     */
    @Override
    public Result onUnauthorized(Http.Context context) {
        return redirect("/login");
    }

    /**
     * Verifies if some user are logged in session.
     * @param email user email.
     */
    private boolean isLoggedIn(String email) { return this.sessionRepository.isLoggedIn(email); }

}
