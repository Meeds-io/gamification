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

export function getUsersConnectorsSetting() {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/user/connectors`, {
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

export function connect(connectorName, accessToken) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/user/connectors/${connectorName}/connect`, {
    credentials: 'include',
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: `accessToken=${accessToken}`,
  }).then((resp) => {
    if (!resp?.ok) {
      if (resp.status === 409) {
        throw new Error('AccountAlreadyUsed');
      } else {
        throw new Error('Error while validating access token');
      }
    }
  });
}

export function disconnect(connectorName) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/user/connectors/${connectorName}`, {
    method: 'DELETE',
    credentials: 'include',
  }).then(resp => {
    if (!resp?.ok) {
      throw new Error('Response code indicates a server error', resp);
    }
  });
}