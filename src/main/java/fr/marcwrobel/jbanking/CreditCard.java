package fr.marcwrobel.jbanking;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreditCard {

  private ArrayList<String> cardIssuers = new ArrayList<>(Arrays.asList(
    "visa",
    "mastercard",
    "discover",
    "amex",
    "diners",
    "jcb"
  ));

  private String cardNumber;
  private String cardIssuer;
  private Boolean valid;

  public CreditCard(final String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public boolean isCardValid() {
    String regex = "^(?:(?<visa>4[0-9]{12}(?:[0-9]{3})?)|" +
      "(?<mastercard>5[1-5][0-9]{14})|" +
      "(?<discover>6(?:011|5[0-9]{2})[0-9]{12})|" +
      "(?<amex>3[47][0-9]{13})|" +
      "(?<diners>3(?:0[0-5]|[68][0-9])?[0-9]{11})|" +
      "(?<jcb>(?:2131|1800|35[0-9]{3})[0-9]{11}))$";

    Pattern pattern = Pattern.compile(regex);

    //Strip all hyphens
    cardNumber = cardNumber.replaceAll("-", "");

    //Match the card
    Matcher matcher = pattern.matcher(cardNumber);
    if(matcher.matches()) {
      this.valid = verifyChecksum();
      for(int i = 1; i <= matcher.groupCount(); i++) {
        if(Objects.nonNull(matcher.group(cardIssuers.get(i-1)))){
          this.cardIssuer = cardIssuers.get(i-1);
          return this.valid;
        }
      }
    }
    return this.valid;
  }

  private boolean verifyChecksum(){
    int sum = 0;
    boolean alternate = false;
    for (int i = this.cardNumber.length() - 1; i >= 0; i--)
    {
      int n = Integer.parseInt(this.cardNumber.substring(i, i + 1));
      if (alternate)
      {
        n *= 2;
        if (n > 9)
        {
          n = (n % 10) + 1;
        }
      }
      sum += n;
      alternate = !alternate;
    }
    return (sum % 10 == 0);
  }

  public String getCardIssuer(){
    if(Objects.nonNull(this.valid)) {
      return this.cardIssuer;
    }
    isCardValid();
    if(this.valid) {
      return this.cardIssuer;
    }
    return "";
  }

}
