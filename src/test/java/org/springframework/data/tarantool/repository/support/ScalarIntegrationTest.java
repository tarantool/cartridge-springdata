package org.springframework.data.tarantool.repository.support;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.tarantool.BaseIntegrationTest;
import org.springframework.data.tarantool.repository.ScalarRepository;
import org.springframework.data.tarantool.repository.ScalarRepositoryWithoutTuple;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Artyom Dubinin
 */
@Tag("integration")
class ScalarIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private ScalarRepository repositoryWithTuple;

    @Autowired
    private ScalarRepositoryWithoutTuple repositoryWithoutTuple;

    @Test
    public void test_nil_shouldCalledSuccessfully_withoutTuple() {
        assertDoesNotThrow(() -> repositoryWithoutTuple.getNil());
    }

    @Test
    @Disabled
    public void test_nil_shouldCalledSuccessfully_withTuple() {
        assertDoesNotThrow(() -> repositoryWithTuple.getNil());
    }

    @Test
    public void test_boolean_shouldCalledSuccessfully_withoutTuple() {
        //given
        Boolean expected = true;

        //when
        Boolean actual = repositoryWithoutTuple.getBoolean();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_boolean_shouldCalledSuccessfully_withTuple() {
        //given
        Boolean expected = true;

        //when
        Boolean actual = repositoryWithTuple.getBoolean();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void test_string_shouldCalledSuccessfully_withoutTuple() {
        //given
        String expected = "test string";

        //when
        String actual = repositoryWithoutTuple.getString();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_string_shouldCalledSuccessfully_withTuple() {
        //given
        String expected = "test string";

        //when
        String actual = repositoryWithTuple.getString();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_char_shouldCalledSuccessfully_withoutTuple() {
        //given
        Character expected = 't';

        //when
        Character actual = repositoryWithoutTuple.getChar();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_char_shouldCalledSuccessfully_withTuple() {
        //given
        Character expected = 't';

        //when
        Character actual = repositoryWithTuple.getChar();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_byte_shouldCalledSuccessfully_withoutTuple() {
        //given
        Byte expected = 't';

        //when
        Byte actual = repositoryWithoutTuple.getByte();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_byte_shouldCalledSuccessfully_withTuple() {
        //given
        Byte expected = 't';

        //when
        Byte actual = repositoryWithTuple.getByte();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void test_integer_shouldCalledSuccessfully_withoutTuple() {
        //given
        Integer expected = 12345;

        //when
        Integer actual = repositoryWithoutTuple.getInteger();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_integer_shouldCalledSuccessfully_withTuple() {
        //given
        Integer expected = 12345;

        //when
        Integer actual = repositoryWithTuple.getInteger();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_long_shouldCalledSuccessfully_withoutTuple() {
        //given
        Long expected = 12345L;

        //when
        Long actual = repositoryWithoutTuple.getLong();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_long_shouldCalledSuccessfully_withTuple() {
        //given
        Long expected = 12345L;

        //when
        Long actual = repositoryWithTuple.getLong();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_short_shouldCalledSuccessfully_withoutTuple() {
        //given
        Short expected = 12345;

        //when
        Short actual = repositoryWithoutTuple.getShort();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_short_shouldCalledSuccessfully_withTuple() {
        //given
        Short expected = 12345;

        //when
        Short actual = repositoryWithTuple.getShort();

        //then
        assertEquals(expected, actual);
    }

    @Test
    public void test_double_shouldCalledSuccessfully_withoutTuple() {
        //given
        Double expected = 1.2345;

        //when
        Double actual = repositoryWithoutTuple.getDouble();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_double_shouldCalledSuccessfully_withTuple() {
        //given
        Double expected = 1.2345;

        //when
        Double actual = repositoryWithTuple.getDouble();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_float_shouldCalledSuccessfully_withoutTuple() {
        //given
        Float expected = 1.2345f;

        //when
        Float actual = repositoryWithoutTuple.getFloat();

        //then
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    public void test_float_shouldCalledSuccessfully_withTuple() {
        //given
        Float expected = 1.2345f;

        //when
        Float actual = repositoryWithTuple.getFloat();

        //then
        assertEquals(expected, actual);
    }
}
