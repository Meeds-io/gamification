package org.exoplatform.addons.gamification.utils;

import org.junit.Test;

import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class UtilsTest {

  @Test
  public void testToRFC3339Date() {
    Date date = new Date(System.currentTimeMillis());

    assertNull(Utils.toRFC3339Date(null));
    int dateMonth = date.getMonth() + 1;
    int dateDay = date.getDate();
    int dateYear = date.getYear() + 1900;

    String rfc3339Date = Utils.toRFC3339Date(date);
    assertNotNull(rfc3339Date);
    String fullDate = rfc3339Date.split("T")[0];
    String year = fullDate.split("-")[0];
    String month = fullDate.split("-")[1];
    String day = fullDate.split("-")[2];

    assertEquals(Integer.parseInt(month), dateMonth);
    assertEquals(Integer.parseInt(day), dateDay);
    assertEquals(Integer.parseInt(year), dateYear);
  }

  @Test
  public void testParseRFC3339Date() {
    String rfc3339Date = "2022-04-05T13:45:51.701Z";
    String fullDate = rfc3339Date.split("T")[0];
    String year = fullDate.split("-")[0];
    String month = fullDate.split("-")[1];
    String day = fullDate.split("-")[2];

    assertNull(Utils.parseRFC3339Date(""));

    Date date = Utils.parseRFC3339Date(rfc3339Date);
    assertNotNull(date);
    int dateMonth = date.getMonth() + 1;
    int dateDay = date.getDate();
    int dateYear = date.getYear() + 1900;

    assertEquals(Integer.parseInt(month), dateMonth);
    assertEquals(Integer.parseInt(day), dateDay);
    assertEquals(Integer.parseInt(year), dateYear);
  }

  @Test
  public void testToSimpleDateFormat() {
    Date date = new Date(System.currentTimeMillis());
    assertNull(Utils.toRFC3339Date(null));
    int dateMonth = date.getMonth() + 1;
    int dateDay = date.getDate();
    int dateYear = date.getYear() + 1900;

    String rfc3339Date = Utils.toSimpleDateFormat(date);
    assertNotNull(rfc3339Date);
    String fullDate = rfc3339Date.split("T")[0];
    String year = fullDate.split("-")[0];
    String month = fullDate.split("-")[1];
    String day = fullDate.split("-")[2];

    assertEquals("00:00:00", rfc3339Date.split("T")[1]);
    assertEquals(Integer.parseInt(month), dateMonth);
    assertEquals(Integer.parseInt(day), dateDay);
    assertEquals(Integer.parseInt(year), dateYear);
  }
  @Test
  public void testParseSimpleDate() {
    TimeZone.setDefault(TimeZone.getTimeZone("US/Hawaii"));

    String simpleDate = "2022-04-05T00:00:00";
    String fullDate = simpleDate.split("T")[0];
    String year = fullDate.split("-")[0];
    String month = fullDate.split("-")[1];
    String day = fullDate.split("-")[2];
    Date date = Utils.parseSimpleDate(simpleDate);
    assertNull(Utils.toRFC3339Date(null));
    int dateMonth = date.getMonth() + 1;
    int dateDay = date.getDate();
    int dateYear = date.getYear() + 1900;

    assertEquals("00:00:00", simpleDate.split("T")[1]);
    assertEquals(Integer.parseInt(month), dateMonth);
    assertEquals(Integer.parseInt(day), dateDay);
    assertEquals(Integer.parseInt(year), dateYear);
  }
}
