package com.etnetera.hr.controller;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException extends RuntimeException {

    NotFoundException(Long id) {
        super("could not find item with id: " + id + "");
    }
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BadRequestException extends RuntimeException {

    BadRequestException(String msg) {
        super(msg);
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

    @PatchMapping("/frameworks/{id}")
    public JavaScriptFramework patchFramework(@PathVariable Long id, @RequestBody Map<Object, Object> fields){
        Optional<JavaScriptFramework> item = repository.findById(id);
        JavaScriptFramework framework = item.orElseThrow(() -> new NotFoundException(id));

        fields.forEach((fieldName,fieldValue) -> {
            String sFieldName = fieldName.toString();
            PropertyAccessor accessor = PropertyAccessorFactory.forBeanPropertyAccess(framework);
            try {
                accessor.setPropertyValue(sFieldName, fieldValue);
            }
            catch (BeansException e){
                throw new BadRequestException(e.getMessage());
            }
        });

        repository.save(framework);
        return framework;
    }

    @PostMapping("/frameworks")
    public JavaScriptFramework postFramework(@Validated @RequestBody JavaScriptFramework framework) {
        return this.repository.save(framework);
    }
}
