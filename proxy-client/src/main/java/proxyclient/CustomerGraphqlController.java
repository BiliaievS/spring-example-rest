package proxyclient;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
public class CustomerGraphqlController {
    private final CustomerClient cc;

    public CustomerGraphqlController(CustomerClient cc) {
        this.cc = cc;
    }

    @QueryMapping
    Flux<Customer> customers() {
        return this.cc.all();
    }
}
