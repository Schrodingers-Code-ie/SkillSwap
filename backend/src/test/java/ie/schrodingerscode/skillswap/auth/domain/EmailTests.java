package ie.schrodingerscode.skillswap.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import ie.schrodingerscode.skillswap.auth.domain.exception.InvalidEmailException;

//plain unit tests, no Spring, so they run instantly
class EmailTests {

    @Test
    void trimsAndLowercases() {
        assertThat(new Email("  Ann@Mail.COM ").value()).isEqualTo("ann@mail.com");
    }

    @Test
    void emailsDifferingOnlyInCaseAreEqual() {
        //this is what makes the duplicate check ignore case
        assertThat(new Email("Ann@Mail.com")).isEqualTo(new Email("ann@mail.com"));
    }

    @Test
    void acceptsPlusAddressesAndLongDomainEndings() {
        assertThat(new Email("ann+tag@gmail.com").value()).isEqualTo("ann+tag@gmail.com");
        assertThat(new Email("ann@studio.photography").value()).isEqualTo("ann@studio.photography");
    }

    @Test
    void rejectsNullAndBlank() {
        assertThatThrownBy(() -> new Email(null)).isInstanceOf(InvalidEmailException.class);
        assertThatThrownBy(() -> new Email("   ")).isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void rejectsBadFormat() {
        for (String bad : new String[] { "ann", "ann@mail", "ann mail@x.ie", "a@b@c.ie", "@mail.com" }) {
            assertThatThrownBy(() -> new Email(bad))
                    .as(bad)
                    .isInstanceOf(InvalidEmailException.class);
        }
    }

    @Test
    void acceptsExactly255CharactersButNot256() {
        String suffix = "@x.ie";
        assertThat(new Email("a".repeat(255 - suffix.length()) + suffix).value()).hasSize(255);
        assertThatThrownBy(() -> new Email("a".repeat(256 - suffix.length()) + suffix))
                .isInstanceOf(InvalidEmailException.class);
    }
}
