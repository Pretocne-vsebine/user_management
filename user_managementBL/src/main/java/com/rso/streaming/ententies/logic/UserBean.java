package com.rso.streaming.ententies.logic;

import com.kumuluz.ee.logs.*;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.runtime.EeRuntime;
import com.rso.streaming.ententies.User;
import org.apache.logging.log4j.ThreadContext;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RequestScoped
public class UserBean {
    @PersistenceContext(unitName = "userPersistanceUnit")
    private EntityManager em;

    private static final Logger LOG = LogManager.getLogger(UserBean.class.getName());

    @PostConstruct
    private void init() {
        HashMap settings = new HashMap();
        settings.put("environment", EeConfig.getInstance().getEnv().getName());
        settings.put("serviceName", EeConfig.getInstance().getName());
        settings.put("applicationVersion", EeConfig.getInstance().getVersion());
        settings.put("uniqueInstanceId", EeRuntime.getInstance().getInstanceId());
        settings.put("uniqueRequestId", UUID.randomUUID().toString());

        ThreadContext.putAll(settings);
    }

    public User getUser(long userId) {
        User u = em.find(User.class, userId);
        LOG.info("Returning ser with ID: {}: {}", userId, u);
        return u;
    }

    public User getUser(String name, String pass) {
        List<User> results = em.createNamedQuery("User.findOne", User.class)
                .setParameter("name", name)
                .setParameter("pass", pass)
                .getResultList();

        if(results != null && !results.isEmpty())
            return results.get(0);
        else
            return null;
    }

    @Transactional
    public User createUser(User user) {
        LOG.info("Creating user: {}", user);

        try {
            beginTx();
            em.persist(user);
            commitTx();
            LOG.info("User created.");
        } catch (Exception e) {
            LOG.error("User creation not succesfull. {}", e);
            rollbackTx();
        }
        return user;
    }

    public boolean deleteUser(long userId) {
        LOG.info("Removing album with ID: {}", userId);

        User u = em.find(User.class, userId);

        if (u != null) {
            try {
                beginTx();
                em.remove(u);
                commitTx();
                LOG.info("Removed user.");
            } catch (Exception e) {
                rollbackTx();
                LOG.error("Failed to remove user: {}. An error occured: {}.", u, e);
            }

            return true;
        }
        LOG.info("User with ID: {} not found.", userId);
        return false;
    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }
}
