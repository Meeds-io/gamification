/**
 * This file is part of the Meeds project (https://meeds.io/).
 * 
 * Copyright (C) 2023 Meeds Association contact@meeds.io
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

export function createAnnouncement(announcement) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/announcements`, {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(announcement),
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error(resp.status);
    }
  });
}

export function cancelAnnouncement(announcementId) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/announcements/${announcementId}`, {
    method: 'DELETE',
    credentials: 'include',
  }).then((resp) => {
    if (!resp?.ok) {
      throw new Error('Response code indicates a server error', resp);
    }
  });
}
