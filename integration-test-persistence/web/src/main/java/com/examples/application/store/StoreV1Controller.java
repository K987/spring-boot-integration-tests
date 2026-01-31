package com.examples.application.store;

import java.util.Map;

import com.examples.application.api.v1.OrderDto;
import com.examples.application.api.v1.StoreApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
class StoreV1Controller implements StoreApi {

	@Override
	public ResponseEntity<Void> deleteOrder(Long orderId) {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResponseEntity<Map<String, Integer>> getInventory() {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResponseEntity<OrderDto> getOrderById(Long orderId) {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResponseEntity<OrderDto> placeOrder(OrderDto orderDto) {
        throw new UnsupportedOperationException("Not supported yet.");
	}
}
