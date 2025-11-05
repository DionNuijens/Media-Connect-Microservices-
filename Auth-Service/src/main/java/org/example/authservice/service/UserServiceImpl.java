//package org.example.authservice.service;
//
//import org.example.authservice.model.User;
//import org.example.authservice.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class UserServiceImpl implements UserService {
//
//    private final UserRepository userRepository;
//
//    @Autowired
//    public UserServiceImpl(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public User saveUser(User user) {
//        return userRepository.save(user);
//    }
//
//    @Override
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    @Override
//    public User getUserById(Long id) {
//        return userRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
//    }
//
//    @Override
//    public User updateUser(Long id, User user) {
//        User existingUser = getUserById(id);
//        existingUser.setUsername(user.getUsername());
//        existingUser.setEmail(user.getEmail());
//        existingUser.setPassword(user.getPassword());
//        return userRepository.save(existingUser);
//    }
//
//    @Override
//    public void deleteUser(Long id) {
//        User existingUser = getUserById(id);
//        userRepository.delete(existingUser);
//    }
//}
