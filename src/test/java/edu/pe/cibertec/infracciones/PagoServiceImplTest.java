package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.dto.PagoResponseDTO;
import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Pago;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.PagoRepository;
import edu.pe.cibertec.infracciones.service.impl.PagoServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PagoServiceImpl - Unit test with Mockito")
public class PagoServiceImplTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private PagoServiceImpl pagoService;

    @Test
    @DisplayName("Should apply 20% discount when fine is paid on the same day")
    void givenFineIssuedToday_whenProcesarPago_thenApply20PercentDiscount() {
        Multa multa = new Multa();
        multa.setId(1L);
        multa.setMonto(500.0);
        multa.setFechaEmision(LocalDate.now());
        multa.setFechaVencimiento(LocalDate.now().plusDays(30));
        multa.setEstado(EstadoMulta.PENDIENTE);

        when(multaRepository.findById(1L)).thenReturn(Optional.of(multa));
        when(pagoRepository.save(any(Pago.class))).thenAnswer(i -> i.getArgument(0));

        PagoResponseDTO result = pagoService.procesarPago(1L);

        assertEquals(400.0, result.getMontoPagado());
        assertEquals(0.20 * 500, result.getDescuentoAplicado());
        assertEquals(0.0, result.getRecargo());

        assertEquals(EstadoMulta.PAGADA, multa.getEstado());

        verify(multaRepository, times(1)).findById(1L);
        verify(pagoRepository).save(any(Pago.class));
        verify(multaRepository).save(multa);
    }

}
