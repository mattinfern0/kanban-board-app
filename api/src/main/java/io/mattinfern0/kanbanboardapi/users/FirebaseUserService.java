package io.mattinfern0.kanbanboardapi.users;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class FirebaseUserService {
    // This class handles Firebase user management

    final FirebaseApp firebaseApp;

    @Autowired
    public FirebaseUserService(FirebaseApp firebaseApp) {
        this.firebaseApp = firebaseApp;
    }

    public UserRecord getUserDetails(String firebaseId) {
        try {
            return getFirebaseAuth().getUser(firebaseId);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Error fetching user details from Firebase", e);
        }
    }

    public UserRecord getUserDetails(Principal principal) {
        return getUserDetails(principal.getName());
    }

    private FirebaseAuth getFirebaseAuth() {
        return FirebaseAuth.getInstance(firebaseApp);
    }
}
