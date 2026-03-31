package com.abdulkhadirjallow.spring_auth_system.enums;

public enum Country {

    // North America
    USA(Currency.USD, Continent.NORTH_AMERICA),
    CANADA(Currency.CAD, Continent.NORTH_AMERICA),

    // Europe
    GERMANY(Currency.EUR, Continent.EUROPE),
    UK(Currency.GBP, Continent.EUROPE),
    SWITZERLAND(Currency.CHF, Continent.EUROPE),
    FRANCE(Currency.EUR, Continent.EUROPE),
    ITALY(Currency.EUR, Continent.EUROPE),
    SPAIN(Currency.EUR, Continent.EUROPE),

    // Asia
    JAPAN(Currency.JPY, Continent.ASIA),
    INDIA(Currency.IND, Continent.ASIA),
    CHINA(Currency.CNY, Continent.ASIA),
    SOUTH_KOREA(Currency.KRW, Continent.ASIA),

    // Africa
    GAMBIA(Currency.GMD, Continent.AFRICA),
    NIGERIA(Currency.NGN, Continent.AFRICA),
    GHANA(Currency.GHS, Continent.AFRICA),
    MOROCCO(Currency.MAD, Continent.AFRICA),
    SOUTH_AFRICA(Currency.ZAR, Continent.AFRICA),
    ETHIOPIA(Currency.ETB, Continent.AFRICA),
    KENYA(Currency.KES, Continent.AFRICA),

    // West Africa (XOF)
    SENEGAL(Currency.XOF, Continent.AFRICA),
    MALI(Currency.XOF, Continent.AFRICA),
    IVORY_COAST(Currency.XOF, Continent.AFRICA),
    BURKINA_FASO(Currency.XOF, Continent.AFRICA),

    // Central Africa (XAF)
    CAMEROON(Currency.XAF, Continent.AFRICA),
    CONGO(Currency.XAF, Continent.AFRICA);

    // Data fields
    private final Currency currency;
    private final Continent continent;

    // Constructor
    Country(Currency currency, Continent continent)  {
        this.currency = currency;
        this.continent = continent;
    }

    // Getters
    public Currency getCurrency() {return currency;}
    public Continent getContinent() {return continent;}

}
