//package repository;
//
//import akka.stream.TLSClientAuth;
//import com.google.common.base.Preconditions;
//import models.User;
//import org.mindrot.jbcrypt.BCrypt;
//import play.data.FormFactory;
//import play.db.jpa.JPAApi;
//import play.db.jpa.Transactional;
//
//import javax.inject.Inject;
//import javax.persistence.PersistenceException;
//import javax.persistence.Query;
//import java.util.List;
//
//import java.util.Optional;
//
//public class UserRepository {
//
//    private final SessionRepository sessionRepository;
//    private final String salt;
//    private final JPAApi jpaApi;
//
//    public UserRepository(JPAApi jpaApi) {
//        this.jpaApi = jpaApi;
//        this.salt = BCrypt.gensalt();
//        this.sessionRepository = SessionRepository.instance;
//    }
//
//    @Transactional
//    public User create(String name, String email, String phone, String password) {
//        final User newUser = new User(name, email, hash(password), phone);
//        boolean duplicatedEmail = false;
//        if (this.getUserByEmail(email) != null) duplicatedEmail = true;
//        Preconditions.checkArgument(duplicatedEmail, "E-mail já existente no sistema");
//
//        jpaApi.em().persist(newUser);
//        return newUser;
//    }
//
//    public boolean login(String email, String password) {
//        return Optional.ofNullable(this.getUserByEmail(email))
//                .map(u -> {
//                    boolean validPassword = compare(password, u.getPassword());
//                    if (validPassword) sessionRepository.login(u);
//                    return validPassword;
//                }).orElse(false);
//    }
//
//    @Transactional(readOnly = true)
//    private List<User> getUsers(){
//        return (List<User>) jpaApi.em().createQuery("select u from User u").getResultList();
//    }
//
//    @Transactional(readOnly = true)
//    private User getUserByEmail(String email) {
//        User user = null;
//        try {
//            Query query = jpaApi.em().createQuery("from User where email=:email").setParameter("email", email);
//            if (query != null) user = (User) query.getResultList().get(0);
//        } catch (PersistenceException pe) {
//            System.out.println("Mudar esse erro!!!");
//        }
//        return user;
//    }
//
//    public void logout(String name) {
//        this.sessionRepository.logout(name);
//    }
//
//    public boolean isLoggedIn(String name) { return this.sessionRepository.isLoggedIn(name); }
//
//    private String hash(String value) {
//        return BCrypt.hashpw(value, salt);
//    }
//
//    private boolean compare(String password, String hashed) {
//        return hash(password).equals(hashed);
//    }
//
//}
