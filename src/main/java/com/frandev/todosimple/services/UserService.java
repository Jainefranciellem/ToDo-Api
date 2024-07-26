package com.frandev.todosimple.services;

import java.util.Optional;

import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.frandev.todosimple.models.Task;
import com.frandev.todosimple.models.User;
import com.frandev.todosimple.repositories.TaskRepository;
import com.frandev.todosimple.repositories.UserRepository;

@Service
public class UserService {
    // O Autowired é uma anotação que informa ao Spring que ele deve injetar uma instância de uma classe em um atributo.
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    public User findById(Long id) {
        Optional<User> user = this.userRepository.findById(id);
        return user.orElseThrow(() -> new RuntimeException(
            "User not found with id: " + id + ", Tipo:" + User.class.getName()
        ));
    }

    @Transactional
    public User createUser(User user) {
        user.setId(null);
        user = this.userRepository.save(user);
        this.taskRepository.saveAll(user.getTasks());
        return user;
    }

    @Transactional
    public User updateUser(User user) {
        User newUser = findById(user.getId());
        newUser.setPassword(user.getPassword());
        return this.userRepository.save(newUser);
    }

    // public void delete(Long id) {
    //     User findUser = findById(id);
    //     this.userRepository.delete(findUser);
    // }

    public void delete(Long id) {
        findById(id);
        try {
            this.userRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Não é possível excluir pois há entidades relacionadas!");
        }
    }

    // public Task findByTasks(Long id) {
    //     List<Task> taks = this.taskRepository.findByUser_Id(id);
    //     return taks;
    // }
}
