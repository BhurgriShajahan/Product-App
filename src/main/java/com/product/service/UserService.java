package com.product.service;
import com.product.dto.UserDto;
import com.product.exceptions.UsernameAlreadyTakenException;
import com.product.model.entities.Role;
import com.product.model.entities.User;
import com.product.repository.RoleRepository;
import com.product.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String signup(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new UsernameAlreadyTakenException("Username already taken!");
        }


        // Create a new User and set its properties
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));


        // Fetch the 'ROLE_USER' from the role repository
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            // If ROLE_USER doesn't exist, return an error
            return "User Role not found!";
        }


        user.getRoles().add(userRole);

        userRepository.save(user);
        return "User registered successfully!";
    }
}
