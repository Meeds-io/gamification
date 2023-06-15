/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 - 2023 Meeds Association
 * contact@meeds.io
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

export function getConnectors() {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/connectors`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Error when getting user connectors');
    }
  });
}

export function saveUserConnector(connectorLoginRequest) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/connectors/connect`, {
    headers: {
      'Content-Type': 'application/json'
    },
    credentials: 'include',
    method: 'POST',
    body: JSON.stringify(connectorLoginRequest),
  }).then((resp) => {
    if (!resp?.ok) {
      return resp.text().then(() => {
        throw new Error('Error while validating access token');
      });
    } else {
      return resp.text();
    }
  });
}

export function removeUserConnector(connector) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/connectors/${connector.name}/${connector.user}`, {
    method: 'DELETE',
    credentials: 'include',
  }).then(resp => {
    if (!resp?.ok) {
      throw new Error('Response code indicates a server error', resp);
    }
  });
}