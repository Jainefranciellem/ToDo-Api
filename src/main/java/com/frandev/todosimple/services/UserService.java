package com.frandev.todosimple.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frandev.todosimple.models.User;
import com.frandev.todosimple.repositories.TaskRepository;
import com.frandev.todosimple.repositories.UserRepository;
import com.frandev.todosimple.services.exceptions.ObjectBindingViolationException;
import com.frandev.todosimple.services.exceptions.ObjectNotFoundException;

@Service
public class UserService {
    // O Autowired é uma anotação que informa ao Spring que ele deve injetar uma instância de uma classe em um atributo.
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    public User findById(Long id) {
        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(() -> new ObjectNotFoundException(
            "User not found with id: " + id + ", Tipo:" + User.class.getName()
        ));
    }

    @Transactional
    public User createUser(User user) {
        user.setId(null);
        user.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));
        user = this.userRepository.save(user);
        this.taskRepository.saveAll(user.getTasks());
        return user;
    }

    @Transactional
    public User updateUser(User user) {
        User newUser = findById(user.getId());
        newUser.setPassword(user.getPassword());
        newUser.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));
        
        return this.userRepository.save(newUser);
    }

    public void delete(Long id) {
        findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new ObjectBindingViolationException("Não é possível excluir pois há entidades relacionadas!");
        }
    }
}
