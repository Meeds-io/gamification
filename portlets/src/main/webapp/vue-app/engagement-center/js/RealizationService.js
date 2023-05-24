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

export function getRealizations(filter) {
  const formData = new FormData();
  if (filter?.fromDate) {
    formData.append('fromDate', filter.fromDate);
  }
  if (filter?.toDate) {
    formData.append('toDate', filter.toDate);
  }
  if (filter?.earnerIds?.length > 0) {
    for (const earnerId of filter.earnerIds) {
      formData.append('earnerIds', earnerId);
    }
  }
  if (filter?.sortBy) {
    formData.append('sortBy', filter.sortBy);
  }
  if (filter?.sortDescending) {
    formData.append('sortDescending', 'true');
  }
  if (filter?.offset) {
    formData.append('offset', filter.offset);
  }
  if (filter?.limit) {
    formData.append('limit', filter.limit);
  }
  if (filter?.programIds?.length > 0) {
    for (const element of filter.programIds) {
      formData.append('programIds', element);
    }
  }
  if (filter?.owned) {
    formData.append('owned', 'true');
  }

  const params = new URLSearchParams(formData).toString();
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/realizations?returnSize=true&${params}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Error when getting realizations');
    }
  });
}

export function getRealizationsExportLink(filter) {
  const formData = new FormData();
  if (filter?.fromDate) {
    formData.append('fromDate', filter.fromDate);
  }
  if (filter?.toDate) {
    formData.append('toDate', filter.toDate);
  }
  if (filter?.earnerIds?.length > 0) {
    for (const earnerId of filter.earnerIds) {
      formData.append('earnerIds', earnerId);
    }
  }
  if (filter?.sortBy) {
    formData.append('sortBy', filter.sortBy);
  }
  if (filter?.sortDescending) {
    formData.append('sortDescending', 'true');
  }
  if (filter?.offset) {
    formData.append('offset', filter.offset);
  }
  if (filter?.limit) {
    formData.append('limit', filter.limit);
  }
  if (filter?.programIds?.length > 0) {
    for (const element of filter.programIds) {
      formData.append('programIds', element);
    }
  }
  if (filter?.owned) {
    formData.append('owned', 'true');
  }
  if (filter?.returnSize) {
    formData.append('returnSize', 'true');
  }
  formData.append('returnType', 'xlsx');

  const params = new URLSearchParams(formData).toString();
  return `${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/realizations?${params}`;
}

export function isRealizationManager() {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/realizations/manager`, {
    headers: {
      'Content-Type': 'text/plain'
    },
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp?.ok) {
      return resp.text()
        .then(manager => manager === 'true');
    } else {
      throw new Error('Server indicates an error while sending request');
    }
  });
}

export function updateRealizationStatus(id, status) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/realizations`, {
    method: 'PATCH',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: `id=${id}&status=${status}`,
  }).then((resp) => {
    if (resp?.ok) {
      return;
    } else {
      throw new Error('Error updating realization status');
    }
  });
}
