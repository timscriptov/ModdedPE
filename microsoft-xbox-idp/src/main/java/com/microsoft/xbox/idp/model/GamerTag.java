package com.microsoft.xbox.idp.model;

public class GamerTag {

    public static class Request {
        public String gamertag;
        public boolean preview;
        public String reservationId;
    }

    public static class ReservationRequest {
        public String Gamertag;
        public String ReservationId;

        public ReservationRequest() {
        }

        public ReservationRequest(String gamertag, String reservationId) {
            this.Gamertag = gamertag;
            this.ReservationId = reservationId;
        }
    }

    public static class Response {
        public boolean hasFree;
    }
}
