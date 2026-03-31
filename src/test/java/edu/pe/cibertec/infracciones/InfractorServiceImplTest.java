package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.VehiculoRepository;
import edu.pe.cibertec.infracciones.service.impl.InfractorServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("InfractorServiceImpl - Unit test with Mockito")
public class InfractorServiceImplTest {

    @Mock
    private InfractorRepository infractorRepository;

    @Mock
    private VehiculoRepository vehiculoRepository;

    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private InfractorServiceImpl service;

    @Test
    @DisplayName("Should NOT block infractor when has less than 3 expired fines")
    void givenLessThan3ExpiredFines_whenVerificarBloqueo_thenNotBlocked() {
        Infractor infractor = new Infractor();
        infractor.setId(1L);
        infractor.setBloqueado(false);

        when(infractorRepository.findById(1L)).thenReturn(Optional.of(infractor));
        when(multaRepository.findByInfractor_IdAndEstado(1L, EstadoMulta.VENCIDA))
                .thenReturn(List.of(new Multa(), new Multa())); // 2 vencidas

        service.verificarBloqueo(1L);

        assertFalse(infractor.isBloqueado());
        verify(infractorRepository, times(1)).findById(1L);
        verify(infractorRepository, never()).save(infractor);
    }

}
