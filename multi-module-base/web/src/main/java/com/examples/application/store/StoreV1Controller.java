package com.examples.application.store;

import java.util.Map;

import com.examples.application.api.v1.OrderDto;
import com.examples.application.api.v1.StoreApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
class StoreV1Controller implements StoreApi {

	@Override
	public ResponseEntity<Void> deleteOrder(Long orderId) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResponseEntity<Map<String, Integer>> getInventory() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResponseEntity<OrderDto> getOrderById(Long orderId) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public ResponseEntity<OrderDto> placeOrder(OrderDto orderDto) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
	}
}
