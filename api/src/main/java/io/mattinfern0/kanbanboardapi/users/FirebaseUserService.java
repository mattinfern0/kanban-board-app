package io.mattinfern0.kanbanboardapi.users;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import io.mattinfern0.kanbanboardapi.core.entities.User;
import io.mattinfern0.kanbanboardapi.core.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class FirebaseUserService {
    // This class handles Firebase user management

    final FirebaseApp firebaseApp;
    final UserRepository userRepository;

    @Autowired
    public FirebaseUserService(FirebaseApp firebaseApp, UserRepository userRepository) {
        this.firebaseApp = firebaseApp;
        this.userRepository = userRepository;
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

    public Optional<UserRecord> getFirebaseUserDetailsByEmail(String email) {
        try {
            return Optional.of(getFirebaseAuth().getUserByEmail(email));
        } catch (FirebaseAuthException e) {
            if (e.getAuthErrorCode().equals(AuthErrorCode.USER_NOT_FOUND)) {
                return Optional.empty();
            }

            throw new RuntimeException("Error fetching user details from Firebase", e);
        }
    }

    public Optional<User> getUserEntityByEmail(String email) {

        Optional<UserRecord> firebaseUserDeatils = getFirebaseUserDetailsByEmail(email);

        if (firebaseUserDeatils.isEmpty()) {
            return Optional.empty();
        }

        UserRecord userRecord = firebaseUserDeatils.get();
        return userRepository.findByFirebaseId(userRecord.getUid());
    }

    private FirebaseAuth getFirebaseAuth() {
        return FirebaseAuth.getInstance(firebaseApp);
    }
}
