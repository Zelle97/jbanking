package fr.marcwrobel.jbanking;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CreditCardTest {

  private Set<String> validVisas = Sets.newHashSet(
    "4111111111111111",
    "4007000000027",
    "4222222222222",
    "4012888888881881"
  );

  private Set<String> validMasterCards = Sets.newHashSet(
    "5105105105105100",
    "5111111111111118",
    "5454545454545454",
    "5555555555551111"
  );

  @Test
  public void isCardValid_validCard(){

    CreditCard card = new CreditCard("4111111111111111");

    assertTrue(card.isCardValid());

  }

  @Test
  public void isCardValid_invalidCard(){

    CreditCard card = new CreditCard("4007000200027");

    assertFalse(card.isCardValid());

  }

  @Test
  public void getCardIssuer_validCard(){

    CreditCard card = new CreditCard("5105105105105100");

    assertEquals("mastercard", card.getCardIssuer() );

  }

  @Test
  public void getCardIssuer_invalidCard(){

    CreditCard card = new CreditCard("5105105125105100");

    assertEquals("", card.getCardIssuer() );

  }

}
