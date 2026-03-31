package com.abdulkhadirjallow.spring_auth_system.enums;

public enum Country {

    // North America
    USA(Currency.USD, Continent.NORTH_AMERICA, Currency.USD),
    CANADA(Currency.CAD, Continent.NORTH_AMERICA, Currency.CAD),

    // Europe
    GERMANY(Currency.EUR, Continent.EUROPE, Currency.EUR),
    UK(Currency.GBP, Continent.EUROPE, Currency.GBP),
    SWITZERLAND(Currency.CHF, Continent.EUROPE, Currency.CHF),
    FRANCE(Currency.EUR, Continent.EUROPE, Currency.EUR),
    ITALY(Currency.EUR, Continent.EUROPE, Currency.EUR),
    SPAIN(Currency.EUR, Continent.EUROPE, Currency.EUR),

    // Asia
    JAPAN(Currency.JPY, Continent.ASIA, Currency.JPY),
    INDIA(Currency.INR, Continent.ASIA,Currency.INR),
    CHINA(Currency.CNY, Continent.ASIA, Currency.CNY),
    SOUTH_KOREA(Currency.KRW, Continent.ASIA, Currency.KRW),

    // Africa
    GAMBIA(Currency.GMD, Continent.AFRICA,Currency.GMD),
    NIGERIA(Currency.NGN, Continent.AFRICA,Currency.NGN),
    GHANA(Currency.GHS, Continent.AFRICA,Currency.GHS),
    MOROCCO(Currency.MAD, Continent.AFRICA,Currency.MAD),
    SOUTH_AFRICA(Currency.ZAR, Continent.AFRICA,Currency.ZAR),
    ETHIOPIA(Currency.ETB, Continent.AFRICA,Currency.ETB),
    KENYA(Currency.KES, Continent.AFRICA,Currency.KES),

    // West Africa (XOF)
    SENEGAL(Currency.XOF, Continent.AFRICA,Currency.XOF),
    MALI(Currency.XOF, Continent.AFRICA,Currency.XOF),
    IVORY_COAST(Currency.XOF, Continent.AFRICA,Currency.XOF),
    BURKINA_FASO(Currency.XOF, Continent.AFRICA,Currency.XOF),

    // Central Africa (XAF)
    CAMEROON(Currency.XAF, Continent.AFRICA,Currency.XAF),
    CONGO(Currency.XAF, Continent.AFRICA,Currency.XAF),;

    // Data fields
    private final Currency currency;
    private final Continent continent;
    private final Currency defaultCurrency;
    // Constructor
    Country(Currency currency, Continent continent, Currency defaultCurrency)  {
        this.currency = currency;
        this.continent = continent;
        this.defaultCurrency = defaultCurrency;
    }

    // Getters
    public Currency getCurrency() {return currency;}
    public Continent getContinent() {return continent;}
    public Currency getDefaultCurrency() {return defaultCurrency;}

}
