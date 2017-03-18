package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.action.Secured;
import models.User;
import play.data.FormFactory;
import play.db.jpa.JPAApi;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import repository.SessionRepository;
import views.html.alter;
import views.html.login;
import views.html.signup;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Controller of the CRUD operations and authentication
 * process. This Class creates user, delete and alter then.
 * Also controls the user login and logout process.
 */
public class AuthController extends Controller {

    @Inject FormFactory formFactory;
    private final JPAApi jpaApi;
    private final SessionRepository sessionRepository;

    @Inject
    public AuthController(JPAApi jpaApi) {
        this.jpaApi = jpaApi;
        this.sessionRepository = SessionRepository.instance;
    }

    /**
     * Go to the sign up page.
     */
    public Result signUp() {
        return ok(signup.render(formFactory.form(SignUpForm.class)));
    }

    /**
     * Go to login page.
     */
    public Result login() {
        return ok(login.render(formFactory.form(LoginForm.class)));
    }

    /**
     * Go to the update page.
     */
    @Security.Authenticated(Secured.class)
    public Result alter() {
        String email = session("user");
        session().put("user", email);
        return ok(alter.render(formFactory.form(SignUpForm.class)));
    }

    /**
     * Removes the user from session (logout).
     */
    public Result logout() {
        this.logoutSession(session("user"));
        session().clear();
        return redirect(routes.AuthController.login());
    }

    /**
     * Realize the authentication process.
     * @param loginForm form in json format.
     * @return http response with the error message.
     */
    @Transactional
    public Result authenticate() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = (ObjectNode) request().body().asJson();

        if(json == null) {
            ObjectNode result = mapper.createObjectNode();
            result.put("mensagem", "Expecting Json data");
            return badRequest(result);
        } else {
            String email = json.findPath("email").textValue();
            String password = json.findPath("password").textValue();

            // Retrieve the user from the database.
            User user = null;
            List results = jpaApi.em().createQuery("from User where email=:email")
                    .setParameter("email", email).getResultList();
            if (!results.isEmpty()) user = (User) results.get(0);

            // Realizing the password validation and if is valid log user in the session.
            boolean validation = Optional.ofNullable(user)
                    .map(u -> {
                        boolean validPassword = password.equals(u.getPassword());
                        if (validPassword) sessionRepository.login(u);
                        return validPassword;
                    }).orElse(false);

            ObjectNode result = mapper.createObjectNode();
            if (validation) {
                session().put("user", user.email);
                result.put("mensagem", "Usuário logado com sucesso");
                return ok(result);
            } else {
                // TODO: treat errors better.
                result.put("mensagem", "Falha na validação do login, usuário ou senha incorreta");
                return internalServerError(result);
            }
        }
    }

    /**
     * Create a new user from form data from post request.
     * @param signUpForm form in json format.
     * @return http response with the error message.
     */
    @Transactional
    public Result newUser() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = (ObjectNode) request().body().asJson();

        if(json == null) {
            ObjectNode result = mapper.createObjectNode();
            result.put("mensagem", "Expecting Json data");
            return badRequest(result);
        } else {
            String password = json.findPath("password").textValue();
            String passwordConfirmation = json.findPath("passwordConfirmation").textValue();
            String email = json.findPath("email").textValue();

            // Validates if the passwords are equal.
            boolean validPassword = Objects.equals(password, passwordConfirmation);

            // Verifies if the email exists on the system.
            boolean duplicatedEmail = false;
            List results = jpaApi.em().createQuery("from User where email=:email")
                    .setParameter("email", email).getResultList();
            if (!results.isEmpty()) duplicatedEmail = true;

            // Create the new user if there is no validation errors.
            ObjectNode result = mapper.createObjectNode();
            if (validPassword && !duplicatedEmail){
                try {
                    json.remove("passwordConfirmation");
                    User newUser = mapper.readValue(json.toString(), User.class);
                    jpaApi.em().persist(newUser);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                result.put("mensagem", "Cadastro realizado com sucesso");
                return ok(result);
            } else {
                // TODO: treat errors better.
                result.put("mensagem", "Falha na validação do cadastro");
                return internalServerError(result);
            }
        }
    }

    /**
     * Delete the logged user from the system.
     * @return http response with the error message.
     */
    @Transactional
    public Result deleteUser() {
        ObjectMapper mapper = new ObjectMapper();
        String sessionEmail = session("user");

        // Return an error in case of the email is empty.
        if(sessionEmail == null || sessionEmail.equals("")) {
            ObjectNode result = mapper.createObjectNode();
            result.put("mensagem", "E-mail inválido");
            return internalServerError(result);
        }

        // Retrieve the user from the database.
        User user = null;
        List rUser = jpaApi.em().createQuery("from User where email=:email")
                .setParameter("email", sessionEmail).getResultList();
        if (!rUser.isEmpty()) user = (User) rUser.get(0);
        user.setModified(new Date());

        // Creates a json string with the user data.
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(user);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        // Delete the user from the database.
        jpaApi.em().createQuery("delete from User where email=:email")
                .setParameter("email", sessionEmail).executeUpdate();

        // Remove the user from the session (logout).
        this.logoutSession(sessionEmail);

        // TODO: the delete function should return the data of the
        // TODO: ..deleted user. But i had some problems with the javascript (need time to solve)
        // return ok(jsonInString);
        return ok(login.render(formFactory.form(LoginForm.class)));
    }

    /**
     * Delete the logged user from the system.
     * @param alterForm update form in json format.
     * @return http response with the error message.
     */
    @Transactional
    public Result alterUser() {
        String sessionEmail = session("user");
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = (ObjectNode) request().body().asJson();

        // Return an error in case of wrong format (not JSON).
        if (json == null) {
            ObjectNode result = mapper.createObjectNode();
            result.put("mensagem", "Expecting Json data");
            return badRequest(result);
        } else {
            // Retrieve the user from the database.
            User user = null;
            List rUser = jpaApi.em().createQuery("from User where email=:email")
                    .setParameter("email", sessionEmail).getResultList();
            if (!rUser.isEmpty()) user = (User) rUser.get(0);

            // New values to be updated
            String name = json.findPath("name").textValue();
            String email = json.findPath("email").textValue();
            String password = json.findPath("password").textValue();
            String passwordConfirmation = json.findPath("passwordConfirmation").textValue();
            String phone = json.findPath("phone").textValue();

            // Return an error in case of the email is empty.
            if(email == null || email.equals("")) {
                ObjectNode result = mapper.createObjectNode();
                result.put("mensagem", "E-mail inválido");
                return internalServerError(result);
            }

            // Validates email and password.
            boolean validPassword = Objects.equals(password, passwordConfirmation);
            boolean duplicatedEmail = false;
            List results = jpaApi.em().createQuery("from User where email=:email")
                    .setParameter("email", email).getResultList();
            if (!results.isEmpty()) duplicatedEmail = true;

            if (validPassword && !duplicatedEmail) {
                try {
                    // Update user data
                    if (name != null && !name.equals("")) user.setName(name);
                    if (!email.equals("")) user.setEmail(email);
                    if (phone != null && !phone.equals("")) user.setPhone(phone);
                    if (password != null && !password.equals("")) user.setPassword(password);
                    Date date = new Date();
                    user.setModified(date);
                    json.put("modified", date.toString());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                return ok(json);
            } else {
                // TODO: treat errors better.
                ObjectNode result = mapper.createObjectNode();
                result.put("mensagem", "Falha na validação do cadastro");
                return internalServerError(result);
            }
        }
    }

    /**
     * Remove user from session repository.
     * @param email the email of the logged user.
     */
    private void logoutSession(String email) {
        this.sessionRepository.logout(email);
    }

}
