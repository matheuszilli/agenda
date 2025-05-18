package com.agenda.app.service;

import com.agenda.app.mapper.AppointmentMapper;
import com.agenda.app.repository.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private ProfessionalRepository professionalRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private SubsidiaryRepository subsidiaryRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ChairRoomRepository chairRoomRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private AppointmentMapper mapper;

    @Mock
    private SubsidiaryScheduleEntryRepository subsidiaryScheduleRepository;

    @Mock
    private ProfessionalScheduleEntryRepository professionalScheduleRepository;

    @Mock
    private ChairRoomScheduleEntryRepository chairRoomScheduleRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    // Testes específicos virão aqui
}
