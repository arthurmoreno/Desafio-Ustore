package controllers;

import controllers.action.Secured;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;

/**
 * Controller of the user application services.
 */
public class Application extends Controller {
    @Security.Authenticated(Secured.class)
    public Result index() {
        return ok(index.render(session("user")));
    }
}