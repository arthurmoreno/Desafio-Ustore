package repository;

import com.google.common.base.Preconditions;
import models.Session;
import models.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Class that controls the User session.
 */
public class SessionRepository {

    private final Map<String, Session> repo;
    private final long ttl;

    public static final SessionRepository instance = new SessionRepository();

    private SessionRepository() {
        // TODO: i think that is not working properly (need more time to test and deploy)
        // Time the user can stay logged on the system
        this.ttl = 7200000;
        this.repo = new HashMap<>();
    }

    /**
     * Verifies if a specific user is logged on the system.
     * @param email User email that will be verified.
     */
    public boolean isLoggedIn(String email) {
        final Session session = repo.get(email);
        final boolean loggedIn = Optional.ofNullable(session).map(s -> {
            Date now = new Date();
            final long inactivityPeriod = now.getTime() - s.getLastSeen().getTime();
            return inactivityPeriod < ttl;
        }).orElse(false);

        if (!loggedIn) repo.remove(email);
        else repo.put(email, new Session(session.getUser(), session.getLoggedIn(), new Date()));

        return loggedIn;
    }

    /**
     * Login user on session.
     * @param user User that will be logged in.
     */
    public void login(User user) {
        // TODO: treat errors better.
        Preconditions.checkArgument(!isLoggedIn(user.getEmail()), "Usuário já logado no sistema");
        final Date now = new Date();
        repo.put(user.getEmail(), new Session(user, now, now));
    }

    /**
     * Logout user on session.
     * @param email User's email that will be disconnected from the system.
     */
    public void logout(String email) {
        repo.remove(email);
    }

}
