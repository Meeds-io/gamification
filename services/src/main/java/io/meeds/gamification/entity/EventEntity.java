/*
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2023 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package io.meeds.gamification.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.persistence.*;

import io.meeds.gamification.utils.StringListConverter;
import org.exoplatform.commons.api.persistence.ExoEntity;
import lombok.Data;

@ExoEntity
@Entity(name = "EventEntity")
@Table(name = "GAMIFICATION_EVENTS")
@NamedQuery(name = "EventEntity.getEventByTitleAndTrigger",
            query = "SELECT event FROM EventEntity event"
                 + " WHERE event.title =: title" + " AND event.trigger =: trigger")
@NamedQuery(name = "EventEntity.getEventByTypeAndTitle",
            query = "SELECT event FROM EventEntity event"
                 + " WHERE event.type =: type" + " AND event.title =: title")
@Data
public class EventEntity implements Serializable {

  private static final long serialVersionUID = 5354086158209637891L;

  @Id
  @SequenceGenerator(name = "SEQ_GAMIFICATION_EVENTS_ID", sequenceName = "SEQ_GAMIFICATION_EVENTS_ID", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_GAMIFICATION_EVENTS_ID")
  @Column(name = "ID")
  private Long            id;

  @Column(name = "TITLE", nullable = false)
  private String            title;

  @Column(name = "TYPE", nullable = false)
  private String            type;

  @Column(name = "EVENT_TRIGGER", nullable = false)
  private String          trigger;

  @Convert(converter = StringListConverter.class)
  @Column(name = "CANCELLER_EVENTS")
  private List<String> cancellerEvents;

  @ElementCollection(fetch = FetchType.EAGER)
  @MapKeyColumn(name = "NAME")
  @Column(name = "VALUE")
  @CollectionTable(name = "GAMIFICATION_EVENT_SETTINGS", joinColumns = { @JoinColumn(name = "ID") })
  private Map<String, String> properties;
}
