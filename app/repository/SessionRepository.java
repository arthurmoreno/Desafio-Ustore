package repository;

import com.google.common.base.Preconditions;
import models.Session;
import models.User;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SessionRepository {

    private final Map<String, Session> repo;
    private final long ttl;

    public static final SessionRepository instance = new SessionRepository();

    private SessionRepository() {
        this.repo = new HashMap<>();
        this.ttl = 7200000;
    }

    public boolean isLoggedIn(String name) {
        final Session session = repo.get(name);
        final boolean loggedIn = Optional.ofNullable(session).map(s -> {
            Date now = new Date();
            final long inactivityPeriod = now.getTime() - s.getLastSeen().getTime();
            return inactivityPeriod < ttl;
        }).orElse(false);

        if (!loggedIn) repo.remove(name);
        else repo.put(name, new Session(session.getUser(), session.getLoggedIn(), new Date()));

        return loggedIn;
    }

    public void login(User user) {
        // TODO: treat errors better.
        Preconditions.checkArgument(!isLoggedIn(user.getEmail()), "Usuário já logado no sistema");
        final Date now = new Date();
        repo.put(user.getEmail(), new Session(user, now, now));
    }

    public void logout(String name) {
        repo.remove(name);
    }

}
