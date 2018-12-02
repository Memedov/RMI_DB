package server.model;

import common.Client;
import common.Credentials;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import java.util.Random;


/**
 *
 * @author yuchen
 */
public class UserManager {
    private final Random idGenerator = new Random();
    private final Map<Long, User> users = Collections.synchronizedMap(new HashMap<>());
    
    public long createUser(Client remoteNode, Credentials credentials) {
        long uid = idGenerator.nextLong();
        User newUser = new User(credentials.getUsername(),
                                                     remoteNode, this);
        users.put(uid, newUser);
        return uid;
    }
    
    public User getUser(long id) {
        return users.get(id);
    }
    
    public void removeUser(long id) {
        users.remove(id);
    } 
}
