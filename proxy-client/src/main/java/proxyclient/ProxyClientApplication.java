package proxyclient;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ProxyClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProxyClientApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(CustomerClient cc) {
        return args -> cc.all().subscribe(System.out::println);
    }

    @Bean
    CustomerClient customerClient(WebClient.Builder builder) {
        var wc = builder.baseUrl("http://localhost:8080").build();
        var wca = WebClientAdapter.forClient(wc);
        var hsp = HttpServiceProxyFactory.builder()
                .clientAdapter(wca)
                .build()
                .createClient(CustomerClient.class);

        return hsp;
    }

    @Bean
    RouteLocator gateway(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(rs -> rs
                        .path("/proxy")
                        .filters(fs -> fs
                                .setPath("/customers")
                                .retry(10)
                                .addResponseHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"))
                        .uri("http://localhost:8080"))
                .build();
    }
}

interface CustomerClient {

    @GetExchange("/customers/{name}")
    Flux<Customer> byName(@PathVariable String name);

    @GetExchange("/customers")
    Flux<Customer> all();

}

record Customer(Integer id, String name) {
}

record Profile(Integer id) {
}