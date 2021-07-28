/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 Meeds Association
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
export function getUsersByGamificationRank(period) {
  return fetch( `/portal/rest/gamification/leaderboard/rank/all?loadCapacity=false&period=${period || 'WEEK'}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } 
    else {
      throw new Error ('Error when getting users by gamification rank');
    }
  });
}
export function getReputationStatus() {
  return fetch( `/portal/rest/gamification/reputation/status?username=${eXo.env.portal.profileOwner}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } 
    else {
      throw new Error ('Error when getting the user reputation status');
    }
  });
}

export function getUserInformations() {
  return fetch(`/portal/rest/v1/social/users/${eXo.env.portal.profileOwner}?expand=spacesCount,connectionsCount`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } 
    else {
      throw new Error ('Error when getting user informations');
    }
  });
}

export function getSpaces() {
  return fetch( '/portal/rest/v1/social/spaces?returnSize=true', {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } 
    else {
      throw new Error ('Error when getting spaces');
    }
  });
}

export function getSpacesOfUser(offset, limit) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/users/${eXo.env.portal.profileOwner}/spaces?offset=${offset || 0}&limit=${limit|| 10}&returnSize=true`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    }
    else {
      throw new Error ('Error when getting spaces of current user');
    }
  });
}

export function getCommonsSpaces(offset, limit) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/users/${eXo.env.portal.userName}/spaces/${eXo.env.portal.profileOwner}?offset=${offset || 0}&limit=${limit || 10}&returnSize=true`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    }
    else {
      throw new Error ('Error when getting commons spaces');
    }
  });
}

export function getSpacesRequests() {
  return fetch( '/portal/rest/v1/social/spacesMemberships?status=invited&returnSize=true&limit=3', {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } 
    else {
      throw new Error ('Error when getting spaces requests');
    }
  });
}

export function getSuggestionsSpace(){
  return fetch('/portal/rest/homepage/intranet/spaces/suggestions', {
    credentials: 'include'
  }).then(resp => {
    if (!resp || !resp.ok) {
      return resp.text().then((text) => {
        throw new Error(text);
      });
    } else {
      return resp.json();
    }
  });
}

export function getAchievements(providerId, remoteId, limit) {
  return fetch(`/portal/rest/gamification/gameficationinformationsboard/history/all?capacity=${limit}&providerId=${providerId}&remoteId=${remoteId}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error ('Error when getting achievements');
    }
  });
}

export function replyInvitationToJoinSpace(spaceMembershipId, reply) {
  const data = {status: `${reply}`}; 
  return fetch(`/portal/rest/v1/social/spacesMemberships/${spaceMembershipId}`, {
    method: 'PUT',
    credentials: 'include',
    body: JSON.stringify(data),
    headers: {
      'Content-Type': 'application/json'
    }
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } 
    else {
      throw new Error ('Error when replying invitation to join space');
    }
  });
}

export function getUserConnections(query, offset, limit, expand) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/v1/social/users/${eXo.env.portal.profileOwner}/connections?q=${query || ''}&offset=${offset || 0}&limit=${limit|| 0}&expand=${expand || ''}&returnSize=true`, {
    method: 'GET',
    credentials: 'include',
  }).then(resp => {
    if (!resp || !resp.ok) {
      throw new Error('Response code indicates a server error', resp);
    } else {
      return resp.json();
    }
  });
}

export function getConnections() {
  return fetch('/portal/rest/v1/social/relationships?status=confirmed&returnSize=true', {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } 
    else {
      throw new Error ('Error when getting connections');
    }
  });
}

export function getConnectionsRequests() {
  return fetch('/portal/rest/v1/social/relationships?status=incoming&returnSize=true&limit=3', {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } 
    else {
      throw new Error ('Error when getting connections requests');
    }
  });
}

export function getConnectionRequestSender(senderUrl) {
  return fetch(`${senderUrl}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } 
    else {
      throw new Error ('Error when getting connection request sender');
    }
  });
}

export function replyInvitationToConnect(relationId, reply) {
  const data = {status: `${reply}`}; 
  return fetch(`/portal/rest/v1/social/relationships/${relationId}`, {
    method: 'PUT',
    credentials: 'include',
    body: JSON.stringify(data),
    headers: {
      'Content-Type': 'application/json'
    }
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } 
    else {
      throw new Error ('Error when replying invitation to connect');
    }
  });
}

export function getGamificationPoints(period) {
  return fetch(`/portal/rest/gamification/api/v1/points?userId=${eXo.env.portal.profileOwner}&period=${period || ''}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    }
    else {
      throw new Error ('Error when getting gamification points');
    }
  });
}

export function getGamificationPointsStats(period) {
  return fetch(`/portal/rest/gamification/leaderboard/stats?username=${eXo.env.portal.profileOwner}&period=${period || ''}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    }
    else {
      throw new Error ('Error when getting gamification points stats');
    }
  });
}

export function getCommonConnections(relationId) {
  return fetch(`/portal/rest/v1/social/identities/${relationId}/commonConnections?returnSize=true`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } 
    else {
      throw new Error ('Error when getting common connections');
    }
  });
}
