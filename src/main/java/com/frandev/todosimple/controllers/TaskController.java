package com.frandev.todosimple.controllers;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.frandev.todosimple.models.Task;
import com.frandev.todosimple.services.TaskService;


@RestController
@RequestMapping("/task")
@Validated
public class TaskController {
    
    @Autowired
    private TaskService tasksService;

    @GetMapping("/{id}")
    public ResponseEntity<Task> findById(@PathVariable Long id) {
        Task task = this.tasksService.findById(id);
        return ResponseEntity.ok().body(task);
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<List<Task>> findByAllUserId(@PathVariable Long user_id) {
        List<Task> tasks = this.tasksService.findAllByUserId(user_id);
        return ResponseEntity.ok().body(tasks);
    }
    

    @PostMapping
    @Validated
    public ResponseEntity<Void> createTask(@Valid @RequestBody Task task) {
        this.tasksService.create(task);
         URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(task.getId()).toUri();
        return ResponseEntity.created(uri).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateTask(@RequestBody Task task, @PathVariable Long id) {
        task.setId(id);
        this.tasksService.update(task);
        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        this.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

}
