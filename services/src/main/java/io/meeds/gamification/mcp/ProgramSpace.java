/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2026 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.meeds.gamification.mcp;

import io.meeds.gamification.model.ProgramDTO;

/**
 * Tells which space a program belongs to, for the space scope decision.
 * Shared by the resolvers of the program, of its quests and of their
 * announcements, so that all three answer alike.
 */
final class ProgramSpace {

  private ProgramSpace() {
    // Static helper
  }

  /**
   * A program belongs to a space only when it is a space program: it has a
   * space and isn't open. An open program is platform-wide, its audience is
   * everyone, so it is nobody's space (fail closed). A deleted program has
   * none either.
   *
   * @param program the program, may be null
   * @return the id of the program's space, or null when the program is
   *         missing, deleted, open or of no space
   */
  static Long of(ProgramDTO program) {
    if (program == null || program.isDeleted() || program.isOpen() || program.getSpaceId() <= 0) {
      return null;
    }
    return program.getSpaceId();
  }

}
