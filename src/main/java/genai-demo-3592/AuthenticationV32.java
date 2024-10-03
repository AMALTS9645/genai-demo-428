 ```java
// code-start
package com.example.loginapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.repository.JpaRepository;

@SpringBootApplication
public class LoginApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginApiApplication.class, args);
    }
}

@Service
class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean authenticateUser(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && BCryptPasswordEncoder.isEncoded(password, user.get().getPassword())) {
            return true;
        }
        return false;
    }

    public void createUser(User user) {
        userRepository.save(user);
    }
}

@RestController
@RequestMapping("/api/v1")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        if (userService.authenticateUser(loginRequest.getUsername(), loginRequest.getPassword())) {
            return ResponseEntity.ok("User authenticated successfully");
        }
        return ResponseEntity.status(401).body("Invalid username or password");
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {
        User newUser = new User();
        newUser.setUsername(registrationRequest.getUsername());
        newUser.setPassword(BCryptPasswordEncoder.encode(registrationRequest.getPassword()));
        userService.createUser(newUser);
        return ResponseEntity.ok("User registered successfully");
    }
}

@Data
@Entity
class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;
}

@Repository
class UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}

@Data
class LoginRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}

@Data
class RegistrationRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
// code-end
```