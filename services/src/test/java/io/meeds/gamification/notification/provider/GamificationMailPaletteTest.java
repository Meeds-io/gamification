/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2026 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.notification.provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import org.exoplatform.commons.notification.mail.MailPalette;

import groovy.text.GStringTemplateEngine;

/**
 * The gamification mails are written on the recipient's palette: the templates
 * compile with the engine of the mail channel, keep no palette colour as a
 * literal and print the bound palette.
 */
public class GamificationMailPaletteTest {

  private static final Path    TEMPLATES       = Paths.get("src/main/resources/notification/gamification");

  // the light literals the palette replaces; white on a coloured button stays as written
  private static final Pattern PALETTE_LITERAL = Pattern.compile("(?i)(bgcolor=\"|background-color:\\s*)#(ffffff|f9f9f9|f1f1f1|567ab6)"
      + "|(?<![a-z-])(?<!replace\\(\"color: )color:\\s*#(333333|999999)|(?<!replace\\(\"color: )#2f5e92|border[a-z-]*:\\s*\\d+px solid\\s*#(d8d8d8|224886)");

  @Test
  public void everyMailTemplateCompilesAndReadsThePalette() throws Exception {
    List<Path> templates = templates();
    assertEquals(4, templates.size(), "the four gamification mails");
    for (Path template : templates) {
      String text = Files.readString(template, StandardCharsets.UTF_8);
      new GStringTemplateEngine().createTemplate(text);
      assertFalse(PALETTE_LITERAL.matcher(text).find(), template + " keeps a palette colour as a literal");
      assertTrue(text.contains("MailPalette.of(_templateContext)"), template + " reads the palette");
    }
  }

  @Test
  public void theBoundPaletteReachesEveryRenderedMail() throws Exception {
    Map<String, String> palette = new HashMap<>();
    palette.put("surface", "#1E2328");
    palette.put("background", "#14181B");
    palette.put("text", "#E6EAEE");
    palette.put("secondaryText", "#9AA4AD");
    palette.put("link", "#5FA9AC");
    palette.put("border", "#3A4148");
    palette.put("primary", "#3F8487");
    for (Path template : templates()) {
      Map<String, Object> templateContext = new HashMap<>();
      templateContext.put(MailPalette.CONTEXT_KEY, palette);
      StringWriter writer = new StringWriter();
      Map<String, Object> binding = new HashMap<>();
      binding.put("_ctx", new TemplateContextStub());
      binding.put("_templateContext", templateContext);
      binding.put("_writer", writer);
      for (String name : List.of("USER", "RULE_TITLE", "RULE_ID", "FIRSTNAME", "ANNOUNCEMENT_URL", "ANNOUNCEMENT_MESSAGE",
                                 "FOOTER_LINK", "RULE_URL", "RULE_EVENT", "PROGRAM_AVATAR", "USER_AVATAR", "USER_URL", "PROFILE_URL")) {
        binding.put(name, name.toLowerCase());
      }
      new GStringTemplateEngine().createTemplate(Files.readString(template, StandardCharsets.UTF_8)).make(binding).writeTo(writer);
      String html = writer.toString();
      assertTrue(html.contains("bgcolor=\"#1E2328\""), template + ": " + html);
      assertFalse(html.contains("bgcolor=\"#ffffff\""), template + " keeps a white surface");
    }
  }

  private List<Path> templates() throws IOException {
    try (Stream<Path> files = Files.list(TEMPLATES)) {
      return files.filter(path -> path.toString().endsWith("MailTemplate.gtmpl")).toList();
    }
  }

  /** Stands for the template context the engine gives a template. */
  public static class TemplateContextStub {

    public String appRes(String key, Object... args) {
      return "[" + key + "]";
    }

    public String escapeHTML(String text) {
      return text;
    }

    public void include(String path, Object templateContext) {
      // the shared header and footer are pinned by the commons module
    }
  }

}
