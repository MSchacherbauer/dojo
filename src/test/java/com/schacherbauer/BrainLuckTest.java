package com.schacherbauer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BrainLuckTest {
    @Test
    public void testEchoUntilByte255Encountered() {
        assertThat(new BrainLuck(",+[-.,+]").process("Codewars" + ((char) 255))).isEqualTo("Codewars");
    }

    @Test
    public void testEchoUntilByte0Encountered() {
        assertThat(new BrainLuck(",[.[-],]").process("Codewars" + ((char) 0))).isEqualTo("Codewars");
    }

    @Test
    public void testTwoNumbersMultiplier() {
        final char[] input = {8, 9};

        assertThat(new BrainLuck(",>,<[>[->+>+<<]>>[-<<+>>]<<<-]>>.").process("8,9")).isEqualTo("72");
    }
}