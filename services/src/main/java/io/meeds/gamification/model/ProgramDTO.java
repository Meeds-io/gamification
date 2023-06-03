/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
 * contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.gamification.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProgramDTO implements Serializable, Cloneable {

  private static final long serialVersionUID = -8857818632949907592L;

  protected long            id;

  protected String          title;

  protected String          description;

  protected long            spaceId;

  protected int             priority;

  protected String          createdBy;

  protected String          createdDate;

  protected String          lastModifiedBy;

  protected String          lastModifiedDate;

  protected boolean         deleted;

  protected boolean         enabled;

  protected long            budget;

  /**
   * @deprecated There is no difference anymore between Automatic or Manual
   *             Domain/Program. This is preserved for future usages, but not
   *             used
   */
  @Deprecated(forRemoval = false, since = "1.5.0")
  protected String          type;

  protected String          coverUploadId;

  protected long            coverFileId;

  protected String          coverUrl;

  protected Set<Long>       ownerIds;                                // NOSONAR

  protected long            rulesTotalScore;

  protected boolean         open;

  /**
   * Deprecated should be renamed to spaceId knowing that audienceId
   * should reference an identity id instead
   * @return space technical identifier
   * @deprecated user spaceId instead
   */
  @Deprecated(forRemoval = true, since = "1.5.0")
  public long getAudienceId() {
    return spaceId;
  }

  /**
   * Deprecated should be renamed to spaceId knowing that audienceId
   * should reference an identity id instead
   * @param spaceId space technical identifier
   * @deprecated user spaceId instead
   */
  @Deprecated(forRemoval = true, since = "1.5.0")
  public void setAudienceId(long spaceId) {
    this.spaceId = spaceId;
  }

  @Override
  public ProgramDTO clone() { // NOSONAR
    return new ProgramDTO(id,
                          title,
                          description,
                          spaceId,
                          priority,
                          createdBy,
                          createdDate,
                          lastModifiedBy,
                          lastModifiedDate,
                          deleted,
                          enabled,
                          budget,
                          type,
                          coverUploadId,
                          coverFileId,
                          coverUrl,
                          ownerIds == null ? null : new HashSet<>(ownerIds),
                          rulesTotalScore,
                          open);
  }

}
