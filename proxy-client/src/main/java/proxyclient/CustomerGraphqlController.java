package proxyclient;

import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @BatchMapping
    Map<Customer, Profile> profile(List<Customer> customerList) {
        var map = new HashMap<Customer, Profile>();
        for (var c : customerList) {
            map.put(c, new Profile(c.id()));
        }
        return map;
    }
//    @SchemaMapping(typeName = "Customer")
//    Profile profile(Customer customer) {
//        return new Profile(customer.id());
//    }
}
