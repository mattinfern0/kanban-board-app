package io.mattinfern0.kanbanboardapi.core.config;

import com.google.firebase.FirebaseApp;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;


@TestConfiguration
public class FirebaseTestConfig {
    @MockBean
    FirebaseApp firebaseApp;
}
