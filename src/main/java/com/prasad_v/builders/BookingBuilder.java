package com.prasad_v.builders;

import com.github.javafaker.Faker;
import com.prasad_v.pojos.Booking;
import com.prasad_v.pojos.Bookingdates;

public class BookingBuilder {
    private static final Faker faker = new Faker();
    
    private String firstname;
    private String lastname;
    private Integer totalprice;
    private Boolean depositpaid;
    private String checkin;
    private String checkout;
    private String additionalneeds;

    public BookingBuilder() {
        this.firstname = faker.name().firstName();
        this.lastname = faker.name().lastName();
        this.totalprice = faker.number().numberBetween(100, 1000);
        this.depositpaid = true;
        this.checkin = "2024-12-01";
        this.checkout = "2024-12-05";
        this.additionalneeds = "Breakfast";
    }

    public BookingBuilder withFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public BookingBuilder withLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public BookingBuilder withTotalprice(Integer totalprice) {
        this.totalprice = totalprice;
        return this;
    }

    public BookingBuilder withDepositpaid(Boolean depositpaid) {
        this.depositpaid = depositpaid;
        return this;
    }

    public BookingBuilder withCheckin(String checkin) {
        this.checkin = checkin;
        return this;
    }

    public BookingBuilder withCheckout(String checkout) {
        this.checkout = checkout;
        return this;
    }

    public BookingBuilder withAdditionalneeds(String additionalneeds) {
        this.additionalneeds = additionalneeds;
        return this;
    }

    public Booking build() {
        Booking booking = new Booking();
        booking.setFirstname(firstname);
        booking.setLastname(lastname);
        booking.setTotalprice(totalprice);
        booking.setDepositpaid(depositpaid);
        
        Bookingdates dates = new Bookingdates();
        dates.setCheckin(checkin);
        dates.setCheckout(checkout);
        booking.setBookingdates(dates);
        
        booking.setAdditionalneeds(additionalneeds);
        return booking;
    }
}
