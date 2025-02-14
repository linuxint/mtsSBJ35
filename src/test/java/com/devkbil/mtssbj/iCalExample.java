package com.devkbil.mtssbj;

import lombok.extern.slf4j.Slf4j;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.RandomUidGenerator;
import net.fortuna.ical4j.util.UidGenerator;
import net.fortuna.ical4j.validate.ValidationException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

@Slf4j
public class iCalExample {

    public static void main(String[] args) {
        writeIcalendarFile();
        readIcalendarFile("my_meeting.ics");
    }

    private static void writeIcalendarFile() {

        /* Event start and end time in milliseconds */
        Long startDateTimeInMillis = 1615956275000L;
        Long endDateTimeInMillis = 1615959875000L;

        java.util.Calendar calendarStartTime = new GregorianCalendar();
        calendarStartTime.setTimeInMillis(startDateTimeInMillis);

        // Time zone info
        TimeZone tz = calendarStartTime.getTimeZone();
        ZoneId zid = tz.toZoneId();

        /* Generate unique identifier */
        UidGenerator ug = new RandomUidGenerator();
        Uid uid = ug.generateUid();

        /* Create the event */
        String eventSummary = "New Year";
        LocalDateTime start = LocalDateTime.ofInstant(calendarStartTime.toInstant(), zid);
        LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochMilli(endDateTimeInMillis), zid);
        VEvent event = new VEvent(start, end, eventSummary);
        event.add(uid);

        // Add email addresses as attendees
        Attendee attendee1 = new Attendee("danny@example.com");
        Attendee attendee2 = new Attendee("jenifer@example.com");
        event.add(attendee1);
        event.add(attendee2);

        // Create an Organizer
        Organizer organizer = new Organizer();
        organizer.setValue("MAILTO:sender@example.com");
        event.add(organizer);

        /* Create calendar */
        Calendar icsCalendar = new Calendar();
        icsCalendar.add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));

        // Set the location
        Location location = new Location("Conference Room");

        // Add the Location to the event
        event.add(location);

        /* Add event to calendar */
        icsCalendar.add(event);

        /* Create a file */
        String filePath = "my_meeting.ics";
        FileOutputStream out = null;
        try {

            out = new FileOutputStream(filePath);
            CalendarOutputter outputter = new CalendarOutputter();
            outputter.output(icsCalendar, out);

        } catch (FileNotFoundException e) {
            log.error(e.getMessage());
        } catch (ValidationException e) {
            log.error(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    private static void readIcalendarFile(String path) {
        try {
            FileInputStream fileInputStream =new FileInputStream(path);
            CalendarBuilder builder = new CalendarBuilder();
            Calendar calendar = builder.build(fileInputStream);
            List<CalendarComponent> events = calendar.getComponents(Component.VEVENT);
            for (CalendarComponent event : events) {
                log.info(event.toString());
            }

        } catch (FileNotFoundException e) {
            log.error("readIcalendarFile : input File Not Found", e);
        } catch (ParserException | IOException e) {
            log.error("readIcalendarFile : CalendarBuilder Build Fail", e);
        }
    }

}