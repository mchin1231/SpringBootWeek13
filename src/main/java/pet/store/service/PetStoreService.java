package pet.store.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.store.controller.model.PetStoreCustomer;
import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreEmployee;
import pet.store.dao.CustomerDao;
import pet.store.dao.EmployeeDao;
import pet.store.dao.PetStoreDao;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {
	private final PetStoreDao petStoreDao;
	private final EmployeeDao employeeDao;
	private final CustomerDao customerDao;

	@Autowired
	public PetStoreService(PetStoreDao petStoreDao, EmployeeDao employeeDao, CustomerDao customerDao) {
		this.petStoreDao = petStoreDao;
		this.employeeDao = employeeDao;
		this.customerDao = customerDao;
	}

	@Transactional(readOnly = false)
	public PetStoreCustomer addCustomerToPetStore(Long petStoreId, PetStoreCustomer petStoreCustomer) {
		PetStore petStore = findPetStoreById(petStoreId);
		Customer customer = findOrCreateCustomer(petStoreCustomer.getCustomerId(), petStoreId);

		petStore.getCustomers().add(customer);
		petStoreDao.save(petStore);

		return new PetStoreCustomer(customer);
	}

	private Customer findOrCreateCustomer(Long customerId, Long petStoreId) {
		if (customerId == null) {
			return new Customer();
		} else {
			Customer customer = findCustomerById(customerId, petStoreId);
			return customer;
		}
	}

	private Customer findCustomerById(Long customerId, Long petStoreId) {
		Customer customer = customerDao.findById(customerId)
				.orElseThrow(() -> new NoSuchElementException("Customer with ID=" + customerId + " not found"));

		for (PetStore petStore : customer.getPetStores()) {
			if (petStore.getPetStoreId().equals(petStoreId)) {
				return customer;
			}
		}

		throw new IllegalArgumentException("Customer is not associated with the specified pet store.");
	}

	// switch

//	@Transactional(readOnly = false)
//	public PetStoreCustomer saveCustomer(Long petStoreId, PetStoreCustomer petStoreCustomer) {
//		PetStore petStore = findPetStoreById(petStoreId);
//		Customer customer = findOrCreateCustomer(petStoreCustomer.getCustomerId(), petStoreId);
//		copyCustomerFields(customer, petStoreCustomer);
//
//		customer.setPetStore(petStore);
//		petStore.getCustomers().add(customer);
//
//		Customer savedCustomer = customerDao.save(customer);
//		return new PetStoreCustomer(savedCustomer);
//	}
//
//	private Customer findOrCreateCustomer(Long customerId, Long petStoreId) {
//		if (customerId == null) {
//			return new Customer();
//		} else {
//			Customer customer = findCustomerById(petStoreId, customerId);
//			if (!customer.getPetStore().getPetStoreId().equals(petStoreId)) {
//				throw new IllegalArgumentException("Customer does not belong to the specified pet store.");
//			}
//			return customer;
//		}
//	}
//
//	private Customer findCustomerById(Long petStoreId, Long customerId) {
//		return customerDao.findById(customerId)
//				.filter(customer -> customer.getPetStore().getPetStoreId().equals(petStoreId))
//				.orElseThrow(() -> new NoSuchElementException("Customer with ID=" + customerId + " not found"));
//	}

	// switch

	@Transactional(readOnly = false)
	public PetStoreEmployee saveEmployee(Long petStoreId, PetStoreEmployee petStoreEmployee) {
		PetStore petStore = findPetStoreById(petStoreId);
		Employee employee = findOrCreateEmployee(petStoreEmployee.getEmployeeId(), petStoreId);
		copyEmployeeFields(employee, petStoreEmployee);

		employee.setPetStore(petStore);
		petStore.getEmployees().add(employee);

		Employee savedEmployee = employeeDao.save(employee);
		return new PetStoreEmployee(savedEmployee);
	}

	private Employee findOrCreateEmployee(Long employeeId, Long petStoreId) {
		if (employeeId == null) {
			return new Employee();
		} else {
			Employee employee = findEmployeeById(petStoreId, employeeId);
			if (!employee.getPetStore().getPetStoreId().equals(petStoreId)) {
				throw new IllegalArgumentException("Employee does not belong to the specified pet store.");
			}
			return employee;
		}
	}

	private Employee findEmployeeById(Long petStoreId, Long employeeId) {
		return employeeDao.findById(employeeId)
				.filter(employee -> employee.getPetStore().getPetStoreId().equals(petStoreId))
				.orElseThrow(() -> new NoSuchElementException("Employee with ID=" + employeeId + " not found"));
	}

	private void copyEmployeeFields(Employee employee, PetStoreEmployee petStoreEmployee) {
		employee.setEmployeeFirstName(petStoreEmployee.getEmployeeFirstName());
		employee.setEmployeeLastName(petStoreEmployee.getEmployeeLastName());
		employee.setEmployeePhone(petStoreEmployee.getEmployeePhone());
		employee.setEmployeeJobTitle(petStoreEmployee.getEmployeeJobTitle());
	}

	public PetStoreData savePetStore(PetStoreData petStoreData) {
		Long petStoreId = petStoreData.getPetStoreId();
		PetStore petStore = findOrCreatePetStore(petStoreId);

		copyPetStoreFields(petStore, petStoreData);

		PetStore savedPetStore = petStoreDao.save(petStore);
		return new PetStoreData(savedPetStore);
	}

	private PetStore findOrCreatePetStore(Long petStoreId) {
		if (petStoreId == null) {
			return new PetStore();
		} else {
			return findPetStoreById(petStoreId);
		}
	}

	private PetStore findPetStoreById(Long petStoreId) {
		return petStoreDao.findById(petStoreId)
				.orElseThrow(() -> new NoSuchElementException("Pet store with ID=" + petStoreId + " does not exist"));
	}

	private void copyPetStoreFields(PetStore petStore, PetStoreData petStoreData) {
		petStore.setPetStoreName(petStoreData.getPetStoreName());
		petStore.setPetStoreAddress(petStoreData.getPetStoreAddress());
		petStore.setPetStoreCity(petStoreData.getPetStoreCity());
		petStore.setPetStoreState(petStoreData.getPetStoreState());
		petStore.setPetStoreZip(petStoreData.getPetStoreZip());
		petStore.setPetStorePhone(petStoreData.getPetStorePhone());
	}

	public PetStoreEmployee addEmployeeToPetStore(Long petStoreId, PetStoreEmployee employee) {
		Employee newEmployee = new Employee();
		newEmployee.setEmployeeFirstName(employee.getEmployeeFirstName());
		newEmployee.setEmployeeLastName(employee.getEmployeeLastName());
		newEmployee.setEmployeePhone(employee.getEmployeePhone());
		newEmployee.setEmployeeJobTitle(employee.getEmployeeJobTitle());

		PetStore petStore = petStoreDao.findById(petStoreId)
				.orElseThrow(() -> new NoSuchElementException("Pet store with ID=" + petStoreId + " not found"));

		newEmployee.setPetStore(petStore);
		Employee savedEmployee = employeeDao.save(newEmployee);

		return new PetStoreEmployee(savedEmployee);
	}

	@Transactional
	public List<PetStoreData> retrieveAllPetStores() {
		List<PetStore> petStores = petStoreDao.findAll();
		List<PetStoreData> petStoreDataList = new ArrayList<>();

		for (PetStore petStore : petStores) {
			PetStoreData petStoreData = new PetStoreData(petStore);
			petStoreData.setCustomers(Collections.emptySet());
			petStoreDataList.add(petStoreData);
		}
		return petStoreDataList;
	}

	@Transactional
	public PetStoreData retrievePetStoreById(Long petStoreId) {
		PetStore petStore = findPetStoreById(petStoreId);
		return new PetStoreData(petStore);
	}

	@Transactional
	public void deletePetStoreById(Long petStoreId) {
		PetStore petStore = findPetStoreById(petStoreId);
		petStoreDao.delete(petStore);
	}
}
