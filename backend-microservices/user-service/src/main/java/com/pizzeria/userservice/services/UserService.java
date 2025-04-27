package com.pizzeria.userservice.services;

import com.pizzeria.userservice.entities.UserAddress;
import com.pizzeria.userservice.entities.User;
import com.pizzeria.userservice.repositories.UserAddressRepository;
import com.pizzeria.userservice.repositories.UserRepository;
import com.pizzeria.userservice.utils.exceptions.InvalidCredentialsException;
import com.pizzeria.userservice.utils.exceptions.UserAlreadyExistsException;
import com.pizzeria.userservice.utils.exceptions.UserNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pizzeria.userservice.utils.constraints.PasswordConstraint;
import com.pizzeria.userservice.utils.enums.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAddressRepository userAddressRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserAddressRepository userAddressRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userAddressRepository = userAddressRepository;
    }

    @Transactional
    public User registerUser(@Valid User newUser) {
        logger.info("Registering new user: {}", newUser.getUsername());

        if(nonDuplicateData(newUser)){
            // Hash the password
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

            // Save the user
            User savedUser = userRepository.save(newUser);
            logger.info("User registered successfully: {}", savedUser.getUsername());
            return savedUser;
        }else
            return null;
    }

    @Transactional
    public User createUser(String username, String password, String firstName, String lastName, String email, String phone, UserRole userRole) {
        logger.info("Creating user: {}", username);

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setPhone(phone);
        newUser.setUserRole(userRole);

        // Validation will occur due to @Valid in registerUser
        return registerUser(newUser);
    }

    @Transactional
    public User addAddressToUser(Long userId, @Valid UserAddress address) {
        logger.info("Adding address to user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        UserAddress savedAddress = userAddressRepository.save(address); // Save the new address
        user.addAddress(savedAddress);
        userRepository.save(user);

        logger.info("Address added to user successfully. User: {}, Address: {}", user.getUsername(), address.getStreetAddress());
        return user;
    }

    @Transactional
    public User addExistingAddressToUser(Long userId, Long addressId) {
        logger.info("Adding existing address with ID {} to user with ID: {}", addressId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new UserNotFoundException("Address not found with ID: " + addressId));

        user.addAddress(address);
        userRepository.save(user);

        logger.info("Existing address added to user successfully. User: {}, Address: {}", user.getUsername(), address.getId());
        return user;
    }

    @Transactional
    public User removeAddressFromUser(Long userId, Long addressId) {
        logger.info("Removing address with ID {} from user with ID: {}", addressId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new UserNotFoundException("Address not found with ID: " + addressId));

        user.removeAddress(address);
        userRepository.save(user);

        logger.info("Address removed from user successfully. User: {}, Address ID: {}", user.getUsername(), addressId);
        return user;
    }

    public Set<UserAddress> findUserAddresses(Long userId) {
        logger.info("Finding addresses for user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        return user.getAddresses();
    }

    public User authenticateUser(String username, String password) {
        logger.info("Authenticating user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Authentication failed: User not found with username: {}", username);
                    return new UserNotFoundException("Invalid username or password");
                });

        if (passwordEncoder.matches(password, user.getPassword())) {
            logger.info("User authenticated successfully: {}", username);
            return user;
        } else {
            logger.warn("Authentication failed: Invalid password for user: {}", username);
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    public User getUserById(Long userId) {
        logger.info("Getting user by ID: {}", userId);

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    public Optional<User> findUserByUsername(String username) {
        logger.info("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    public List<User> findAllUsers() {
        logger.info("Finding all users");
        return userRepository.findAll();
    }

    @Transactional
    public User updateUser(Long id, @Valid User updatedUser) {
        logger.info("Updating user with ID: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPhone(updatedUser.getPhone());
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());

        User savedUser = userRepository.save(existingUser);
        logger.info("User updated successfully: {}", savedUser.getUsername());
        return savedUser;
    }

    @Transactional
    public void changeUserPassword(Long id, String oldPassword, String newPassword) {
        logger.info("Changing password for user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            logger.warn("Invalid old password for user: {}", user.getUsername());
            throw new InvalidCredentialsException("Invalid old password");
        }

        if(!newPassword.matches(PasswordConstraint.PASSWORD_PATTERN))
            throw new InvalidCredentialsException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Password changed successfully for user: {}", user.getUsername());
    }

    public boolean nonDuplicateData(User tempUser) {
        if(userRepository.findByUsername(tempUser.getUsername()).isPresent()) throw new UserAlreadyExistsException("User with username " + tempUser.getUsername() + " already exists.");
        if(userRepository.findByEmail(tempUser.getEmail()).isPresent()) throw new UserAlreadyExistsException("User with email " + tempUser.getEmail() + " already exists.");
        if(userRepository.findByPhone(tempUser.getPhone()).isPresent()) throw new UserAlreadyExistsException("User with phone number " + tempUser.getPhone() + " already exists.");
        return true;
    }
}