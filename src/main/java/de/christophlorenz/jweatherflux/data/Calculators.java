package de.christophlorenz.jweatherflux.data;

public class Calculators {

  public static Float farenheitToCelsius(Float farenheit) {
    return (farenheit - 32f) * (5f / 9f);
  }

  public static Float celsiusToFarenheit(Float celsius) {
    return (celsius * 9f/5f) + 32f;
  }

  public static Float inHgToHpa(Float inHg) {
    return inHg * 33.863f;
  }

  public static Float mphToKmh(Float mph) {
    return mph * 1.60934f;
  }

  public static Float imperialToMetric(Float imperial) {
    return imperial * 0.0254f;
  }

  /**
   * According to https://myscope.net/windchill-gefuehlte-temperatur-berechnen/
   * @param tempInCelsius
   * @param windKmh
   * @param humidity
   * @return
   */
  public static Float calcWindchill(Float tempInCelsius, Float windKmh, Float humidity) {
    if (tempInCelsius <= 10 && windKmh >= 4.8 && windKmh <= 177){
      return (float) (13.12 + 0.6215 * tempInCelsius - 11.37 * Math.pow(windKmh, 0.16)
          + 0.3965 * tempInCelsius * Math.pow(windKmh, 0.16));
    }

    if (tempInCelsius >= 26.7 ) {
      return (float)(-8.784695 + 1.61139411*tempInCelsius + 2.338549*humidity - 0.14611605*tempInCelsius*humidity - 0.012308094*tempInCelsius*tempInCelsius - 0.016424828*humidity*humidity + 0.002211732*tempInCelsius*tempInCelsius*humidity + 0.00072546*tempInCelsius*humidity*humidity - 0.000003582*tempInCelsius*tempInCelsius*humidity*humidity);
    }

    return tempInCelsius;
  }

  /**
   * According to https://myscope.net/taupunkttemperatur/
   * @param tempInCelsius
   * @param relHumidity
   * @return
   */
  public static Float calcDewpoint(Float tempInCelsius, Float relHumidity) {
    Float a;
    Float b;

    if (tempInCelsius >= 0) {
      a = 7.5f;
      b = 237.3f;
    } else {
      a = 7.6f;
      b = 240.7f;
    }

    // Sättigungsdampfdruck (hPa)
    double sdd = 6.1078 * Math.pow(10, (a*tempInCelsius)/(b+tempInCelsius));

    // Dampfdruck (hPa)
    double dd = sdd * (relHumidity/100);

    // v-Parameter
    double v = Math.log10(dd/6.1078);

    double dewpoint = (b*v) / (a-v);

    return (float) dewpoint;
  }

}
