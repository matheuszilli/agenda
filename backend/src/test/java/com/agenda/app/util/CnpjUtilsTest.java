package com.agenda.app.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class CnpjUtilsTest {

    @ParameterizedTest
    @MethodSource("provideCnpjsForFormatting")
    void shouldFormatCnpjCorrectly(String input, String expected) {
        assertEquals(expected, CnpjUtils.formatCnpj(input));
    }

    @Test
    void shouldThrowExceptionWhenCnpjHasInvalidLength() {
        assertThrows(IllegalArgumentException.class, () -> CnpjUtils.formatCnpj("123456789"));
        assertThrows(IllegalArgumentException.class, () -> CnpjUtils.formatCnpj("123456789012345"));
    }

    @Test
    void shouldReturnNullWhenInputIsNull() {
        assertNull(CnpjUtils.formatCnpj(null));
    }

    @ParameterizedTest
    @MethodSource("provideCnpjsForExtractingRoot")
    void shouldExtractCnpjRootCorrectly(String cnpj, String expectedRoot) {
        assertEquals(expectedRoot, CnpjUtils.extractCnpjRoot(cnpj));
    }

    @ParameterizedTest
    @MethodSource("provideCnpjPairsFromSameCompany")
    void shouldIdentifySameCompany(String cnpj1, String cnpj2, boolean expectedResult) {
        assertEquals(expectedResult, CnpjUtils.isSameCompany(cnpj1, cnpj2));
    }

    // Dados para testes
    static Stream<Arguments> provideCnpjsForFormatting() {
        return Stream.of(
                Arguments.of("12345678000123", "12.345.678/0001-23"),
                Arguments.of("12.345.678/0001-23", "12.345.678/0001-23"),
                Arguments.of("12345678000199", "12.345.678/0001-99"),
                Arguments.of("12345678000245", "12.345.678/0002-45"),
                Arguments.of("12.345.678/0002-45", "12.345.678/0002-45"),
                Arguments.of("12345678000245", "12.345.678/0002-45"),
                Arguments.of("12 345 678 0002 45", "12.345.678/0002-45")
        );
    }

    static Stream<Arguments> provideCnpjsForExtractingRoot() {
        return Stream.of(
                Arguments.of("12345678000123", "12345678"),
                Arguments.of("12.345.678/0001-23", "12345678"),
                Arguments.of("12345678000199", "12345678"),
                Arguments.of("98765432000199", "98765432")
        );
    }

    static Stream<Arguments> provideCnpjPairsFromSameCompany() {
        return Stream.of(
                // Mesmo CNPJ, mesma empresa (trivial)
                Arguments.of("12.345.678/0001-23", "12.345.678/0001-23", true),

                // Mesma empresa, filiais diferentes
                Arguments.of("12.345.678/0001-23", "12.345.678/0002-45", true),
                Arguments.of("12.345.678/0001-23", "12.345.678/0003-67", true),

                // Empresas diferentes
                Arguments.of("12.345.678/0001-23", "98.765.432/0001-99", false),

                // Formatos diferentes, mesma empresa
                Arguments.of("12345678000123", "12.345.678/0002-45", true)
        );
    }
}