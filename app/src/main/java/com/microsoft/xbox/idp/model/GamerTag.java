package com.microsoft.xbox.idp.model;

/**
 * 05.10.2020
 *
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */

public class GamerTag {
    public static class Request {
        public String gamertag;
        public boolean preview;
        public String reservationId;
    }

    public static class Response {
        public boolean hasFree;
    }

    public static class ReservationRequest {
        public String Gamertag;
        public String ReservationId;

        public ReservationRequest() {
        }

        public ReservationRequest(String gamertag, String reservationId) {
            Gamertag = gamertag;
            ReservationId = reservationId;
        }
    }
}