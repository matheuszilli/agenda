package com.agenda.app;

import com.agenda.app.model.*;
import com.agenda.app.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;


@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

	@Bean
	@Transactional
	CommandLineRunner run(
			SubsidiaryRepository subsidiaryRepository,
			CustomerRepository customerRepository,
			ProfessionalRepository professionalRepository,
			AppointmentRepository appointmentRepository,
			ServiceRepository serviceRepository,
			CompanyRepository companyRepository
	) {
		return args -> {
			// Criar e salvar Company
			Company company = new Company();
			company.setName("Clínica Central");
			company.setAddress("Rua Exemplo, 123");
			company.setPhone("11999999999");
			companyRepository.save(company);

			// Criar e salvar Subsidiary
			Subsidiary subsidiary = new Subsidiary();
			subsidiary.setName("Unidade Centro");
			subsidiary.setAddress("Av. Teste, 456");
			subsidiary.setOpenTime(LocalTime.of(8, 0));
			subsidiary.setCloseTime(LocalTime.of(18, 0));
			subsidiary.setDaysOpen(SubsidiaryDaysOpen.MONDAY);
			subsidiary.setCompany(company);
			subsidiaryRepository.save(subsidiary);

			// Criar e salvar Customer
			Customer customer = new Customer();
			customer.setFirstName("Ana");
			customer.setLastName("Silva");
			customer.setEmail("ana@example.com");
			customer.setPhone("11988887777");
			customer.setAddress("Rua Cliente, 789");
			customer.setDocumentNumber("12345678900");
			customer.setDateOfBirth(LocalDate.of(1990, 1, 1));
			customerRepository.save(customer);

			// Criar e salvar Professional
			Professional professional = new Professional();
			professional.setFirstName("Dr.");
			professional.setLastName("Carlos");
			professional.setEmail("carlos@example.com");
			professional.setAddress("Rua Médico, 101");
			professional.setDocumentNumber("98765432100");
			professional.setPhone("11977776666");
			professional.setAvailableStart(LocalTime.of(8, 0));
			professional.setAvailableEnd(LocalTime.of(17, 0));
			professional.setSubsidiary(subsidiary);
			professionalRepository.save(professional);

			// Criar e salvar Service
			Service service = new Service();
			service.setName("Consulta Geral");
			service.setPrice(new BigDecimal("150.00"));
			service.setDurationInMinutes(30);
			service.setCompany(company);
			serviceRepository.save(service);

			// Criar e salvar Appointment
			Appointment appointment = new Appointment();
			appointment.setCustomer(customer);
			appointment.setProfessional(professional);
			appointment.setService(service);
			appointment.setSubsidiary(subsidiary);
			appointment.setCompany(company);
			appointment.setStartTime(LocalDateTime.now().plusDays(1));
			appointment.setEndTime(LocalDateTime.now().plusDays(1).plusMinutes(30));
			appointment.setStatus(AppointmentStatus.CONFIRMED);
			appointmentRepository.save(appointment);

			// Agora rodar a consulta
			List<Appointment> result = appointmentRepository.findByStatusAndSubsidiaryId(
					AppointmentStatus.CONFIRMED,
					subsidiary.getId()
			);

			System.out.println("Encontrados: " + result.size() + " agendamentos CONFIRMED");
			result.forEach(a -> {
				System.out.println("ID: " + a.getId());
				System.out.println("Início: " + a.getStartTime());
				System.out.println("Cliente: " + a.getCustomer().getFirstName());
			});
		};
	}



}
