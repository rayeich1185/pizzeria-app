package com.pizzeria.userservice.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.pizzeria.userservice.services.UserService;
import com.pizzeria.userservice.utils.exceptions.*;
import com.pizzeria.userservice.utils.dto.*;

import java.net.URI;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getAllUsers() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDTO<>(true, userService.findAllUsers(), "Users retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getUserById(@PathVariable Long id) {
        UserResponseDTO userDTO;
        try{
            userDTO = userService.findUserById(id);
        } catch (UserNotFoundException e) {
            logger.warn("User with id {} not found", id);
            throw e;
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDTO<>(true, userDTO, String.format("User id %d retrieved successfully", id)));
    }

    @GetMapping("/{id}/addresses")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Set<AddressDTO>>> getUserAddresses(@PathVariable Long id) {
        Set<AddressDTO> userAddresses;

        try{
            userAddresses = userService.findUserAddresses(id);
        } catch (UserNotFoundException e) {
            logger.warn("User with id {} not found", id);
            throw e;
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDTO<>(true, userAddresses,
                        String.format("User id %d addresses retrieved successfully", id)));
    }

    @GetMapping("/addresses/{addressId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Set<UserResponseDTO>>> getUsersByAddressId(@PathVariable Long addressId) {
        Set<UserResponseDTO> usersDTO;

        try{
            usersDTO = userService.findUsersByAddressId(addressId);
        } catch (UserAddressNotFoundException e) {
            logger.warn("Address with id {} not found", addressId);
            throw e;
        }

        if (usersDTO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(new ApiResponseDTO<>(false, null, "No users found for the specified address"));
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponseDTO<>(true, usersDTO, "Users retrieved successfully"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> registerUser(
            @Valid @RequestBody UserRegistrationRequestDTO userRegistrationRequestDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();

            return ResponseEntity.badRequest()
                    .body(new ApiResponseDTO<>(false, null,
                            "User registration failed: " + String.join(", ", errors)));
        }

        try {
            UserResponseDTO registeredUserDTO = userService.registerUser(userRegistrationRequestDTO);

            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(registeredUserDTO.getId())
                    .toUri();

            return ResponseEntity.created(location)
                    .body(new ApiResponseDTO<>(true, registeredUserDTO, "User registered successfully"));

        } catch (UserAlreadyExistsException e) {
            logger.warn("User with username {} already exists", userRegistrationRequestDTO.getUsername());
            throw e;

        } catch (Exception e) {
            logger.error("Error registering user", e);
            throw e;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> loginUser(@RequestBody LoginRequestDTO loginRequestDTO) {
        try{
            UserResponseDTO authenticatedUserDTO = userService.authenticateUser(loginRequestDTO.getUsername(),
                    loginRequestDTO.getPassword());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponseDTO<>(true, authenticatedUserDTO, "User authenticated successfully"));
        } catch (InvalidCredentialsException e) {
            logger.warn("Authentication failed: Invalid credentials for user");
            throw e;
        } catch (Exception e) {
            logger.error("Error authenticating user", e);
            throw e;
        }
    }

    @PostMapping("/{userId}/addresses")
    public ResponseEntity<?> addAddressToUser(
            @PathVariable Long userId,
            @Valid @RequestBody AddressDTO addressDTO,
            BindingResult result) {

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDTO<>(false, null,
                            "Adding address to users failed: " + String.join(", ", errors)));
        }

        try {
            UserResponseDTO updatedUserDTO = userService.addAddressToUser(userId, addressDTO);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponseDTO<>(true, updatedUserDTO, "Address added successfully"));
        } catch (UserNotFoundException e) {
            logger.warn("User with id {} not found", userId);
            throw e;
        } catch (Exception e) {
            logger.error("Error adding address to user", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> updateUser(@PathVariable Long id,
                                                                   @Valid @RequestBody UserUpdateDTO updatedUserDTO,
                                                                   BindingResult result) {

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDTO<>(false, null,
                            "User update failed: " + String.join(", ", errors)));
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponseDTO<>(true, userService.updateUser(id, updatedUserDTO),
                        "User updated successfully"));
    }

    @PutMapping("/{id}/password")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<ApiResponseDTO<Object>> changeUserPassword(
            @Valid @RequestBody PasswordChangeRequestDTO passwordChangeRequestDTO) {
        try{
            userService.changeUserPassword(passwordChangeRequestDTO.getUserId(),
                    passwordChangeRequestDTO.getOldPassword(),
                    passwordChangeRequestDTO.getNewPassword());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponseDTO<>(true, null, "Password changed successfully"));
        } catch (InvalidCredentialsException e) {
            logger.warn("Invalid credentials: {}", e.getMessage());
            throw e;
        } catch (UserNotFoundException e) {
            logger.warn("User with id {} not found", passwordChangeRequestDTO.getUserId());
            throw e;
        } catch (Exception e) {
            logger.error("Error changing password", e);
            throw e;
        }
    }

    @PutMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<AddressDTO>> updateAddress(
            @PathVariable Long userId,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressDTO addressDTO,
            BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponseDTO<>(false, null,
                            "Update user address failed: " + String.join(", ", errors)));
        }

        try {
            UserResponseDTO updatedUserDTO = userService.updateAddress(userId, addressId, addressDTO);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponseDTO<>(true, addressDTO, "Address updated successfully"));
        } catch (UserNotFoundException e) {
            logger.warn("User with id {} not found", userId);
            throw e;
        } catch (UserAddressNotFoundException e) {
            logger.warn("Address with id {} not found", addressId);
            throw e;
        } catch (Exception e) {
            logger.error("Error updating address", e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Object>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (UserNotFoundException e) {
            logger.warn("User with id {} not found", id);
            throw e;
        } catch (Exception e) {
            logger.error("Error deleting user", e);
            throw e;
        }
    }

    @DeleteMapping("{userId}/addresses/{addressId}")
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Object>> removeAddressFromUser(
            @PathVariable Long userId, @PathVariable Long addressId) {
        try {
            userService.removeAddressFromUser(userId, addressId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (UserNotFoundException e) {
            logger.warn("User with id {} not found", userId);
            throw e;
        } catch (UserAddressNotFoundException e) {
            logger.warn("Address with id {} not found", addressId);
            throw e;
        } catch (Exception e) {
            logger.error("Error removing address from user", e);
            throw e;
        }
    }
}
