package com.coopfinance.account_api.infraestructure.adapters.out.sequence;

import com.coopfinance.account_api.infraestructure.BaseIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SequenceNumeroContaGeneratorIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private SequenceNumeroContaGenerator sequenceGenerator;

    @Test
    @DisplayName("Deve gerar números sequenciais e gerenciar os blocos de allocation size")
    void deveGerarNumerosSequenciaisCorretamente() {
        // O valor padrão de allocationSize configurado no componente é 50.
        // Se a sequence iniciar (por exemplo) no 1 e pedirmos 55 valores,
        // o gerador buscará o primeiro bloco e o segundo quando passar de 50, sem quebrar a sequência.
        
        List<Long> numerosGerados = new ArrayList<>();
        
        // Geramos mais valores do que o tamanho do bloco (50) para forçar uma nova chamada ao banco de dados
        for (int i = 0; i < 55; i++)
            numerosGerados.add(sequenceGenerator.getNextNumeroConta());
        
        assertEquals(55, numerosGerados.size());

        // Verifica se os números gerados estão estritamente em sequência
        for (int i = 0; i < numerosGerados.size() - 1; i++)
           // valida se a lista original foi preenchida na ordem certa (sem precisar de Collections.sort)
            assertEquals(numerosGerados.get(i) + 1, numerosGerados.get(i + 1), 
                    "Os números não foram retornados em sequência pelo generator");
    }
}
