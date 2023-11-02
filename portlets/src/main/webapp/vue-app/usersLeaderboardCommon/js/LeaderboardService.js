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
 *
 */

export function getPrograms(filter) {
  const formData = new FormData();
  if (filter?.offset) {
    formData.append('offset', filter.offset);
  }
  if (filter?.limit) {
    formData.append('limit', filter.limit);
  }
  if (filter?.status) {
    formData.append('status', filter.status.toUpperCase());
  }
  if (filter?.query) {
    formData.append('query', filter.query);
  }
  if (filter?.includeDeleted) {
    formData.append('includeDeleted', 'true');
  }
  if (filter?.sortByBudget) {
    formData.append('sortByBudget', 'true');
  } else if (filter?.sortBy) {
    formData.append('sortBy', filter.sortBy);
    formData.append('sortDescending', !!filter.sortDescending);
  }
  if (filter?.owned) {
    formData.append('owned', 'true');
  }
  if (filter?.lang) {
    formData.append('lang', filter.lang);
  }
  if (filter?.expand) {
    formData.append('expand', filter.expand);
  }
  const params = new URLSearchParams(formData).toString();

  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/programs?${params}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Server indicates an error while sending request');
    }
  });
}

export function getLeaderboard(filter) {
  const formData = new FormData();
  if (filter.programId && filter.programId !== '0') {
    formData.append('programId', filter.programId);
  }
  if (filter.identityId) {
    formData.append('identityId', filter.identityId);
  }
  formData.append('period', filter.period || 'WEEK');
  formData.append('limit', filter.limit || 0);
  const params = decodeURIComponent(new URLSearchParams(formData).toString());
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/leaderboard?${params}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Server indicates an error while sending request');
    }
  });
}

export function getStats(identityId, period) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/leaderboard/stats/${identityId}?period=${period || 'WEEK'}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Server indicates an error while sending request');
    }
  });
}