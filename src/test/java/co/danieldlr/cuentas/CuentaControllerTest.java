package co.danieldlr.cuentas;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Prueba solo la capa web: el servicio es un mock, no hace falta base de datos.
@WebMvcTest(CuentaController.class)
class CuentaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CuentaService service;

    @Test
    void crearCuentaDevuelve201() throws Exception {
        when(service.crear(any(), any())).thenReturn(new Cuenta("Ana", new BigDecimal("100")));

        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titular\":\"Ana\",\"saldoInicial\":100}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titular").value("Ana"));
    }

    @Test
    void cuentaInexistenteDevuelve404() throws Exception {
        when(service.buscar(99L)).thenThrow(new CuentaNoEncontradaException(99L));

        mockMvc.perform(get("/cuentas/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void montoNegativoDevuelve400() throws Exception {
        mockMvc.perform(post("/cuentas/transferencias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"origenId\":1,\"destinoId\":2,\"monto\":-5}"))
                .andExpect(status().isBadRequest());
    }
}
