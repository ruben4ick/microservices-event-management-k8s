package ua.edu.ukma.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GatewayApiKeyFilter implements GlobalFilter, Ordered {
	@Value("${internal.api.key}")
	private String apiKey;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		exchange.getRequest().mutate()
				.header("x-api-key", apiKey)
				.build();
		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		return -1;
	}
}
