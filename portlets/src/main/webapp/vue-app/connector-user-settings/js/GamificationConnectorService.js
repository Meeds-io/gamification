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

export function getConnectors(username, expand) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/connectors?username=${username}&expand=${expand}`, {
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

export function connect(connectorName, accessToken, remoteId) {
  const formData = new FormData();
  formData.append('accessToken', accessToken);
  if (remoteId) {
    formData.append('remoteId', remoteId);
  }

  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/connectors/connect/${connectorName}`, {
    credentials: 'include',
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: new URLSearchParams(formData).toString(),
  }).then((resp) => {
    if (resp?.ok) {
      return resp.text();
    } else {
      if (resp.status === 409) {
        throw new Error('AccountAlreadyUsed');
      } else {
        throw new Error('Error while validating access token');
      }
    }
  });
}

export function disconnect(connectorName, remoteId) {
  const formData = new FormData();
  formData.append('remoteId', remoteId);

  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/connectors/disconnect/${connectorName}`, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    method: 'DELETE',
    credentials: 'include',
    body: new URLSearchParams(formData).toString(),
  }).then(resp => {
    if (!resp?.ok) {
      throw new Error('Response code indicates a server error', resp);
    }
  });
}

export function saveConnectorSettings(connectorName, apiKey, secretKey, redirectUrl, enabled) {
  const formData = new FormData();
  formData.append('connectorName', connectorName);
  formData.append('apiKey', apiKey);
  formData.append('secretKey', secretKey);
  formData.append('redirectUrl', redirectUrl);
  formData.append('enabled', enabled);

  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/connectors/settings`, {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: new URLSearchParams(formData).toString(),
  }).then(resp => {
    if (!resp?.ok) {
      throw new Error('Response code indicates a server error', resp);
    }
  });
}

export function deleteConnectorSetting(connectorName) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/connectors/settings/${connectorName}`, {
    method: 'DELETE',
    credentials: 'include',
  }).then(resp => {
    if (!resp?.ok) {
      throw new Error('Response code indicates a server error', resp);
    }
  });
}

export function saveConnectorAccessToken(connectorName, accessToken) {
  const formData = new FormData();
  formData.append('connectorName', connectorName);
  formData.append('accessToken', accessToken);

  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/connectors/accessToken`, {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: new URLSearchParams(formData).toString(),
  }).then(resp => {
    if (!resp?.ok) {
      throw new Error('Response code indicates a server error', resp);
    }
  });
}

export function getConnectorHooks(connectorName) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/connectors/hooks/${connectorName}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Error when getting connector hooks');
    }
  });
}

export function saveConnectorHook(connectorName, hookName, hookSecret) {
  const formData = new FormData();
  formData.append('hookName', hookName);
  formData.append('hookSecret', hookSecret);

  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/connectors/${connectorName}/hooks`, {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: new URLSearchParams(formData).toString(),
  }).then(resp => {
    if (!resp?.ok) {
      throw new Error('Response code indicates a server error', resp);
    }
  });
}

export function deleteConnectorHook(connectorName, hookName) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/connectors/${connectorName}/hooks/${hookName}`, {
    method: 'DELETE',
    credentials: 'include',
  }).then(resp => {
    if (!resp?.ok) {
      throw new Error('Response code indicates a server error', resp);
    }
  });
}