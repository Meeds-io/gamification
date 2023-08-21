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

  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/programs?returnSize=true&${params}`, {
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

export function createProgram(program) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/programs/`, {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(program),
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      return resp.text().then(e => new Error(e));
    }
  });
}

export function updateProgram(program) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/programs/${program.id}`, {
    method: 'PUT',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(program),
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      return resp.text().then(e => new Error(e));
    }
  });
}

export function getProgramById(id, filter) {
  const formData = new FormData();
  if (filter?.lang) {
    formData.append('lang', filter.lang);
  }
  if (filter?.expand) {
    formData.append('expand', filter.expand);
  }
  const params = new URLSearchParams(formData).toString();
  const extraParams = params && `?${params}` || '';
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/programs/${id}${extraParams}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      return null;
    }
  });
}

export function deleteProgram(programId) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/programs/${programId}`, {
    method: 'DELETE',
    credentials: 'include',
  }).then((resp) => {
    if (!resp?.ok) {
      throw new Error('Response code indicates a server error', resp);
    }
  });
}

export function deleteProgramCover(programId) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/programs/${programId}/cover`, {
    method: 'DELETE',
    credentials: 'include',
  }).then((resp) => {
    if (!resp?.ok) {
      throw new Error('Response code indicates a server error', resp);
    }
  });
}

export function deleteProgramAvatar(programId) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/programs/${programId}/avatar`, {
    method: 'DELETE',
    credentials: 'include',
  }).then((resp) => {
    if (!resp?.ok) {
      throw new Error('Response code indicates a server error', resp);
    }
  });
}

export function checkColorValidity(programId, color) {
  const formData = new FormData();
  formData.append('color', color);
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/programs/${programId || 0}/color`, {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded'
    },
    body: new URLSearchParams(formData).toString(),
  }).then((resp) => {
    if (resp?.ok) {
      return resp.text();
    } else {
      throw new Error('Error saving program');
    }
  });
}
