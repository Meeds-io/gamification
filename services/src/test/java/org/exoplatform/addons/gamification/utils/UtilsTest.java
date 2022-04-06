package org.exoplatform.addons.gamification.utils;

import org.junit.Test;

import java.util.Date;

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
  public void testToRFC3339ChallengeDate() {
    Date date = new Date(System.currentTimeMillis());
    assertNull(Utils.toRFC3339Date(null));
    int dateMonth = date.getMonth() + 1;
    int dateDay = date.getDate();
    int dateYear = date.getYear() + 1900;

    String rfc3339Date = Utils.toRFC3339ChallengeDate(date);
    assertNotNull(rfc3339Date);
    String fullDate = rfc3339Date.split("T")[0];
    String year = fullDate.split("-")[0];
    String month = fullDate.split("-")[1];
    String day = fullDate.split("-")[2];

    assertEquals(rfc3339Date.split("T")[1], "00:00:00");
    assertEquals(Integer.parseInt(month), dateMonth);
    assertEquals(Integer.parseInt(day), dateDay);
    assertEquals(Integer.parseInt(year), dateYear);
  }
}
