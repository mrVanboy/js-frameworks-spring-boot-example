package com.etnetera.hr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.etnetera.hr.data.JavaScriptFramework;
import com.etnetera.hr.repository.JavaScriptFrameworkRepository;

import java.time.Instant;
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
 *
 */
@RestController
public class JavaScriptFrameworkController {

	private final JavaScriptFrameworkRepository repository;

	@Autowired
	public JavaScriptFrameworkController(JavaScriptFrameworkRepository repository) {
		this.repository = repository;
	}

	@GetMapping("/frameworks")
	public Iterable<JavaScriptFramework> getFrameworks() {
		return repository.findAll();
	}

	@GetMapping("/frameworks/{id}")
	public JavaScriptFramework getFramework(@PathVariable Long id) {
		Optional<JavaScriptFramework> item = repository.findById(id);
		return item.orElseThrow(() -> new NotFoundException(id));
	}

	@PostMapping("/frameworks")
	public JavaScriptFramework postFramework(@Validated @RequestBody JavaScriptFramework framework){
		return this.repository.save(framework);
	}
}
