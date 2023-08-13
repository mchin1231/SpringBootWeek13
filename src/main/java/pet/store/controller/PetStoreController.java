package pet.store.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import pet.store.controller.model.PetStoreCustomer;
import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreEmployee;
import pet.store.service.PetStoreService;

@RestController
@RequestMapping("/pet_store")
@Slf4j
public class PetStoreController {
	private final PetStoreService petStoreService;

	@Autowired
	public PetStoreController(PetStoreService petStoreService) {
		this.petStoreService = petStoreService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public PetStoreData createPetStore(@RequestBody PetStoreData petStoreData) {
		log.info("Received a request to create or modify pet store: {}", petStoreData);
		return petStoreService.savePetStore(petStoreData);
	}

	@PutMapping("/{petStoreId}")
	public PetStoreData updatePetStore(@PathVariable Long petStoreId, @RequestBody PetStoreData petStoreData) {
		log.info("Updating pet store with ID= {}", petStoreId);
		petStoreData.setPetStoreId(petStoreId);
		return petStoreService.savePetStore(petStoreData);
	}

	@PostMapping("/{petStoreId}/employee")
	@ResponseStatus(HttpStatus.CREATED)
	public PetStoreEmployee addEmployeeToPetStore(@PathVariable Long petStoreId,
			@RequestBody PetStoreEmployee employee) {
		log.info("Received a request to add employee to pet store with ID: {}", petStoreId);
		return petStoreService.addEmployeeToPetStore(petStoreId, employee);
	}

	@PostMapping("/{petStoreId}/customer")
	@ResponseStatus(HttpStatus.CREATED)
	public PetStoreCustomer addCustomerToPetStore(@PathVariable Long petStoreId,
			@RequestBody PetStoreCustomer customer) {
		log.info("Received a request to add customer to pet store with ID: {}", petStoreId);
		return petStoreService.addCustomerToPetStore(petStoreId, customer);
	}

	@GetMapping
	public List<PetStoreData> retreiveAllPetStores() {
		log.info("Received a request to retrieve all pet stores.");
		return petStoreService.retrieveAllPetStores();
	}

	@GetMapping("/{petStoreId}")
	public PetStoreData retrievePetStoreById(@PathVariable Long petStoreId) {
		log.info("Received a request to retrieve pet store with ID: {}", petStoreId);
		return petStoreService.retrievePetStoreById(petStoreId);
	}

	@DeleteMapping("/{petStoreId}")
	public Map<String, String> deletePetStoreById(@PathVariable Long petStoreId) {
		log.info("Received a request to delete pet store with ID: {}", petStoreId);
		petStoreService.deletePetStoreById(petStoreId);
		return Collections.singletonMap("message",
				"Pet store with ID " + petStoreId + " has been deleted succesfully.");

	}
}
