package com.microsoft.xbox.idp.model;

import com.google.gson.GsonBuilder;
import com.microsoft.xbox.idp.model.serialization.UTCDateConverter.UTCDateConverterJSONDeserializer;

import java.util.Date;

public class UserAccount {
    public String administeredConsoles;
    public Date dateOfBirth;
    public String email;
    public String firstName;
    public String gamerTag;
    public String gamerTagChangeReason;
    public Address homeAddressInfo;
    public String homeConsole;
    public String imageUrl;
    public boolean isAdult;
    public String lastName;
    public String legalCountry;
    public String locale;
    public String midasConsole;
    public boolean msftOptin;
    public String ownerHash;
    public String ownerXuid;
    public boolean partnerOptin;
    public Date touAcceptanceDate;
    public String userHash;
    public String userKey;
    public String userXuid;

    public static class Address {
        public String city;
        public String country;
        public String postalCode;
        public String state;
        public String street1;
        public String street2;
    }

    public static GsonBuilder registerAdapters(GsonBuilder gson) {
        return gson.registerTypeAdapter(Date.class, new UTCDateConverterJSONDeserializer());
    }
}
