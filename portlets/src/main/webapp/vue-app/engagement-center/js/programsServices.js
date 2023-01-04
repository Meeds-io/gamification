/**
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2022 Meeds Association
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

export function retrievePrograms(offset, limit, type, status, query, includeDeleted, sortByBudget) {
  const formData = new FormData();
  if (offset) {
    formData.append('offset', offset);
  }
  if (limit) {
    formData.append('limit', limit);
  }

  if (type) {
    formData.append('type', type);
  }
  if (status) {
    formData.append('status', status);
  }
  if (query) {
    formData.append('query', query);
  }
  if (includeDeleted != null) {
    formData.append('includeDeleted', includeDeleted);
  }
  if (sortByBudget != null) {
    formData.append('sortByBudget', sortByBudget);
  }

  const params = new URLSearchParams(formData).toString();

  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/domains?returnSize=true&${params}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Server indicates an error while sending request');
    }
  });
}

export function saveProgram(program) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/domains/`, {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(program),
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error saving program');
    }
  });
}

export function canAddProgram() {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/domains/canAddProgram`, {
    headers: {
      'Content-Type': 'text/plain'
    },
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Server indicates an error while sending request');
    }
  });
}

export function updateProgram(program) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/domains/${program.id}`, {
    method: 'PUT',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(program),
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error updating program');
    }
  });
}

export function getProgramById(id) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/domains/${id}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      return null;
    }
  });
}

export function deleteProgram(programId) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/domains/${programId}`, {
    method: 'DELETE',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Response code indicates a server error', resp);
    }
  });
}