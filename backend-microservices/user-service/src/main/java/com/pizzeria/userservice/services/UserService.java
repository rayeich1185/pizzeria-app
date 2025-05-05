package com.pizzeria.userservice.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pizzeria.userservice.entities.User;
import com.pizzeria.userservice.entities.UserAddress;
import com.pizzeria.userservice.repositories.UserRepository;
import com.pizzeria.userservice.repositories.UserAddressRepository;
import com.pizzeria.userservice.utils.dto.*;
import com.pizzeria.userservice.utils.exceptions.*;
import com.pizzeria.userservice.utils.constraints.*;
import com.pizzeria.userservice.utils.enums.UserRole;

import java.util.List;
import java.util.stream.Collectors;
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

    public List<UserResponseDTO> findAllUsers() {
        logger.info("Finding all users");

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::userToUserResponseDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO findUserById(Long id) throws UserNotFoundException {
        logger.info("Finding user by id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        return userToUserResponseDTO(user);
    }

    public Set<AddressDTO> findUserAddresses(Long userId) {
        logger.info("Finding addresses for user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        Set<UserAddress> addressSet =  user.getAddresses();

        return addressSet.stream()
                .map(this::addressToAddressDTO)
                .collect(Collectors.toSet());
    }

    public Set<UserResponseDTO> findUsersByAddressId(Long addressId) {
        logger.info("Finding users by address ID: {}", addressId);

        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new UserAddressNotFoundException("Address not found with ID: " + addressId));

        Set<User> users = address.getUsers();

        return users.stream()
                .map(this::userToUserResponseDTO)
                .collect(Collectors.toSet());
    }

    @Transactional
    public UserResponseDTO registerUser(UserRegistrationRequestDTO userRegistrationRequestDTO){
        logger.info("Registering new user: {}", userRegistrationRequestDTO.getUsername());

        User tempUser = userRegistrationRequestDTOToUser(userRegistrationRequestDTO);

        if(nonDuplicateData(tempUser)){
            tempUser.setPassword(passwordEncoder.encode(tempUser.getPassword()));
            User savedUser = userRepository.save(tempUser);

            return userToUserResponseDTO(savedUser);
        }else
            return null;
    }

    public UserResponseDTO authenticateUser(String username, String password) {
        logger.info("Authenticating user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Authentication failed: User not found with username: {}", username);
                    return new InvalidCredentialsException("Invalid username or password");
                });

        if (passwordEncoder.matches(password, user.getPassword())) {
            logger.info("User authenticated successfully: {}", username);
            return userToUserResponseDTO(user);
        } else {
            logger.warn("Authentication failed: Invalid password for user: {}", username);
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

    @Transactional
    public UserResponseDTO addAddressToUser(Long userId, AddressDTO addressDTO) {
        return addAddressToUser(userId, addressDTO, false); // Default is false
    }

    @Transactional
    public UserResponseDTO addAddressToUser(Long userId, AddressDTO addressDTO, boolean isDefault) {
        logger.info("Adding address to user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        UserAddress address = addressDTOtoAddress(addressDTO); // Convert DTO to Entity
        UserAddress savedAddress = userAddressRepository.save(address);
        user.addAddress(savedAddress);

        if (isDefault) {
            user.setDefaultAddress(savedAddress);
        }

        userRepository.save(user);
        logger.info("Address added to user. User: {}, Address: {}", user.getUsername(), address.getStreetAddress());
        return userToUserResponseDTO(user);
    }

    @Transactional
    public UserResponseDTO updateUser(Long userId, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        user.setFirstName(userUpdateDTO.getFirstName());
        user.setLastName(userUpdateDTO.getLastName());
        user.setEmail(userUpdateDTO.getEmail());
        user.setPhone(userUpdateDTO.getPhone());

        User savedUser = userRepository.save(user);
        logger.info("User updated. User: {}", user.getUsername());

        return userToUserResponseDTO(savedUser);
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
            throw new InvalidCredentialsException("Password must be at least 8 characters long" +
                    " and contain at least one uppercase letter, one lowercase letter, one digit, and one special character.");

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        logger.info("Password changed successfully for user: {}", user.getUsername());
    }

    @Transactional
    public UserResponseDTO updateAddress(Long userId, Long addressId, AddressDTO addressDTO) {
        logger.info("Updating address with ID {} for user with ID: {}", addressId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new UserAddressNotFoundException("Address not found with ID: " + addressId));

        // Only update fields that are allowed to be updated
        address.setStreetAddress(addressDTO.getStreetAddress());
        address.setUnitNumber(addressDTO.getUnitNumber());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setZipCode(addressDTO.getZipCode());

        userAddressRepository.save(address);
        logger.info("Address with ID {} updated successfully", addressId);

        return userToUserResponseDTO(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        logger.info("Deleting user with ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        userRepository.delete(user);

        logger.info("User with ID {} deleted successfully", userId);
    }

    @Transactional
    public void removeAddressFromUser(Long userId, Long addressId) {
        logger.info("Removing address with ID {} from user with ID: {}", addressId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new UserNotFoundException("Address not found with ID: " + addressId));

        user.removeAddress(address);
        userRepository.save(user);

        logger.info("Address removed from user successfully. User: {}, Address ID: {}", user.getUsername(), addressId);
    }

    private UserResponseDTO userToUserResponseDTO(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setUsername(user.getUsername());
        userResponseDTO.setFirstName(user.getFirstName());
        userResponseDTO.setLastName(user.getLastName());
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setPhone(user.getPhone());

        return userResponseDTO;
    }

    private User userRegistrationRequestDTOToUser(UserRegistrationRequestDTO userRegistrationRequestDTO) {
        User user = new User();
        user.setUsername(userRegistrationRequestDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userRegistrationRequestDTO.getPassword()));
        user.setFirstName(userRegistrationRequestDTO.getFirstName());
        user.setLastName(userRegistrationRequestDTO.getLastName());
        user.setEmail(userRegistrationRequestDTO.getEmail());
        user.setPhone(userRegistrationRequestDTO.getPhone());
        user.setUserRole(UserRole.CUSTOMER);

        return user;
    }


    private UserAddress addressDTOtoAddress(AddressDTO addressDTO) {
        UserAddress address = new UserAddress();
        address.setStreetAddress(addressDTO.getStreetAddress());
        address.setUnitNumber(addressDTO.getUnitNumber());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setZipCode(addressDTO.getZipCode());

        return address;
    }

    private AddressDTO addressToAddressDTO(UserAddress address) {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(address.getId());
        addressDTO.setStreetAddress(address.getStreetAddress());
        addressDTO.setUnitNumber(address.getUnitNumber());
        addressDTO.setCity(address.getCity());
        addressDTO.setState(address.getState());
        addressDTO.setZipCode(address.getZipCode());

        return addressDTO;
    }

    private boolean nonDuplicateData(User tempUser) {
        if(userRepository.findByUsername(tempUser.getUsername()).isPresent())
            throw new UserAlreadyExistsException("User with username " + tempUser.getUsername() + " already exists.");
        if(userRepository.findByEmail(tempUser.getEmail()).isPresent())
            throw new UserAlreadyExistsException("User with email " + tempUser.getEmail() + " already exists.");
        if(userRepository.findByPhone(tempUser.getPhone()).isPresent())
            throw new UserAlreadyExistsException("User with phone number " + tempUser.getPhone() + " already exists.");

        return true;
    }
}
