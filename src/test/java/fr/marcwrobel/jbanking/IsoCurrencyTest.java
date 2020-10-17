package fr.marcwrobel.jbanking;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.neovisionaries.i18n.CountryCode;
import com.neovisionaries.i18n.CurrencyCode;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link IsoCountry} class.
 *
 * @author Marc Wrobel
 */
class IsoCurrencyTest {

  @Test
  void fromAlphaCodeAllowsNull() {
    assertNull(IsoCurrency.fromAlphabeticCode(null));
  }

  @Test
  void fromAlphaCodeAllowsUnknownOrInvalidCodes() {
    assertNull(IsoCurrency.fromAlphabeticCode("AA"));
  }

  @Test
  void fromAlphaCodeIsNotCaseSensitive() {
    assertEquals(
        IsoCurrency.EUR,
        IsoCurrency.fromAlphabeticCode(IsoCurrency.EUR.getAlphabeticCode().toLowerCase()));
  }

  @Test
  void fromAlphaCodeWorksWithExistingValues() {
    for (IsoCurrency currency : IsoCurrency.values()) {
      assertEquals(currency, IsoCurrency.fromAlphabeticCode(currency.getAlphabeticCode()));
    }
  }

  @Test
  void fromNumericCodeAllowsUnknownOrInvalidCodes() {
    assertNull(IsoCurrency.fromNumericCode(-1));
    assertNull(IsoCurrency.fromNumericCode(1));
    assertNull(IsoCurrency.fromNumericCode(1000));
  }

  // using nv-i18n helps us keeping the enum up-to-date
  @Test
  void ensureCompleteness() {
    Set<CurrencyCode> exclusion =
        EnumSet.of(
            CurrencyCode.UNDEFINED,
            CurrencyCode.BYR, // https://wikipedia.org/wiki/Belarusian_ruble
            CurrencyCode.MRO, // https://wikipedia.org/wiki/Mauritanian_ouguiya
            CurrencyCode.RUR, // https://wikipedia.org/wiki/Russian_ruble
            CurrencyCode
                .STD, // https://wikipedia.org/wiki/S%C3%A3o_Tom%C3%A9_and_Pr%C3%ADncipe_dobra
            CurrencyCode.VEF, // https://wikipedia.org/wiki/Venezuelan_bol%C3%ADvar
            CurrencyCode.LTL // https://wikipedia.org/wiki/Lithuanian_litas
            );

    List<CurrencyCode> allCurrencies =
        EnumSet.allOf(CurrencyCode.class).stream()
            .filter(c -> !exclusion.contains(c))
            .collect(Collectors.toList());

    List<CurrencyCode> undefinedCurrencies =
        allCurrencies.stream()
            .filter(c -> IsoCurrency.fromAlphabeticCode(c.name()) == null)
            .collect(Collectors.toList());

    assertTrue(undefinedCurrencies.isEmpty(), "Missing currencies : " + undefinedCurrencies);
  }

  // using nv-i18n helps us keeping the enum up-to-date
  @Test
  void ensureCountriesCompleteness() {
    Multimap<IsoCurrency, IsoCountry> missingCountries = HashMultimap.create();
    Set<CountryCode> unknownCountryCode = new HashSet<>();

    for (IsoCurrency currency : IsoCurrency.values()) {
      CurrencyCode currencyCode = CurrencyCode.getByCode(currency.getAlphabeticCode());

      for (CountryCode countryCode : currencyCode.getCountryList()) {
        if (countryCode != CountryCode.EU) {
          Optional<IsoCountry> country = IsoCountry.fromAlpha2Code(countryCode.getAlpha2());

          if (country.isPresent()) {
            if (!currency.getCountries().contains(country.get())) {
              missingCountries.put(currency, country.get());
            }
          } else {
            unknownCountryCode.add(countryCode);
          }
        }
      }
    }

    assertTrue(missingCountries.isEmpty(), "Missing countries : " + missingCountries);
    assertTrue(unknownCountryCode.isEmpty(), "Unknown countries : " + unknownCountryCode);
  }

  // using nv-i18n helps us keeping the enum up-to-date
  @Test
  void ensureNoDeprecated() {
    List<IsoCurrency> deprecated =
        Arrays.stream(IsoCurrency.values())
            .filter(
                currency -> {
                  CurrencyCode code = CurrencyCode.getByCode(currency.getAlphabeticCode());
                  if (code != null) {
                    if (currency.getNumericCode() == code.getNumeric()) {
                      if (currency.getMinorUnit() != null) {
                        return currency.getMinorUnit() != code.getMinorUnit();
                      } else {
                        return code.getMinorUnit() != -1;
                      }
                    } else {
                      return true;
                    }
                  }
                  return true;
                })
            .collect(Collectors.toList());

    assertTrue(deprecated.isEmpty(), "Deprecated currencies : " + deprecated);
  }

  @Test
  void ensureIsoCodeIsUsedForEnumEntries() {
    for (IsoCurrency currency : IsoCurrency.values()) {
      assertEquals(3, currency.name().length());
    }
  }
}
