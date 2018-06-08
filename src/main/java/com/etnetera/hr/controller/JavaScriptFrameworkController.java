package com.etnetera.hr.controller;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException extends RuntimeException {

    NotFoundException(Long id) {
        super("could not find item with id: " + id + "");
    }
}

/**
 * Simple REST controller for accessing application logic.
 *
 * @author Etnetera
 */
@RestController
public class JavaScriptFrameworkController {

    private JavaScriptFrameworkRepository repository;

    @Autowired
    public JavaScriptFrameworkController(JavaScriptFrameworkRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/frameworks")
    public Iterable<JavaScriptFramework> getFrameworksByName(
            @QuerydslPredicate(root = JavaScriptFramework.class) Predicate predicate) {
        return repository.findAll(predicate);
    }

    @GetMapping("/frameworks/{id}")
    public JavaScriptFramework getFramework(@PathVariable Long id) {
        Optional<JavaScriptFramework> item = repository.findById(id);
        return item.orElseThrow(() -> new NotFoundException(id));
    }

    @DeleteMapping("/frameworks/{id}")
    public ResponseEntity<Void> deleteFramework(@PathVariable Long id) {
        Optional<JavaScriptFramework> item = repository.findById(id);
        JavaScriptFramework framework = item.orElseThrow(() -> new NotFoundException(id));
        repository.delete(framework);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/frameworks")
    public JavaScriptFramework postFramework(@Validated @RequestBody JavaScriptFramework framework) {
        return this.repository.save(framework);
    }
}
