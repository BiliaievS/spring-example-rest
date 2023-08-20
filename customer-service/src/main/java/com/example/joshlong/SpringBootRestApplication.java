package com.example.joshlong;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
public class SpringBootRestApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootRestApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(CustomerRepository repository) {
        return args -> repository.findAll().forEach(System.out::println);
    }

}

@Controller
@ResponseBody
class CustomerController {
    private final CustomerRepository repository;
    private final ObservationRegistry registry;

    CustomerController(CustomerRepository repository, ObservationRegistry registry) {
        this.repository = repository;
        this.registry = registry;
    }

    @GetMapping("/customers/{name}")
    Iterable<Customer> byName(@PathVariable String name) {
        Assert.state(Character.isUpperCase(name.charAt(0)), "The name must start with an uppercase letter");
        return Observation.createNotStarted("by-name", this.registry)
                //http://localhost:8080/actuator/metrics/by-name
                .observe(() -> repository.findByName(name));
    }

    @GetMapping("/customers")
    Iterable<Customer> customers() {
        return this.repository.findAll();
    }
}

@ControllerAdvice
class ErrorHandlingControllerAdvice {

    @ExceptionHandler
    ProblemDetail handle(IllegalStateException ise, HttpServletRequest request) {
        request.getHeaderNames().asIterator().forEachRemaining(System.out::println);
        var pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST.value());
        pd.setDetail(ise.getMessage());
        return pd;
    }
}

interface CustomerRepository extends CrudRepository<Customer, Integer> {

    Iterable<Customer> findByName(String name);
}

record Customer(@Id Integer id, String name) {
}
