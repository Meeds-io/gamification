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

export function getRules(filter) {
  const formData = new FormData();
  if (filter?.term) {
    formData.append('term', filter.term);
  }
  if (filter?.programId) {
    formData.append('programId', filter.programId);
  }
  if (filter?.status) {
    formData.append('status', filter.status.toUpperCase());
  }
  if (filter?.type) {
    formData.append('type', filter.type.toUpperCase());
  }
  if (filter?.dateFilter) {
    formData.append('dateFilter', filter.dateFilter.toUpperCase());
  }
  if (filter?.period) {
    formData.append('period', filter.period.toUpperCase());
  }
  if (filter?.groupByProgram) {
    formData.append('groupByProgram', 'true');
  }
  if (filter?.orderByRealizations) {
    formData.append('orderByRealizations', 'true');
  }
  if (filter?.excludedRuleIds?.length) {
    filter.excludedRuleIds.forEach(id => formData.append('excludedRuleIds', id));
  }
  if (filter?.returnSize) {
    formData.append('returnSize', filter.returnSize);
  }
  if (filter?.offset) {
    formData.append('offset', filter.offset);
  }
  if (filter?.limit) {
    formData.append('limit', filter.limit);
  }
  if (filter?.announcementsLimit) {
    formData.append('announcementsLimit', filter.announcementsLimit);
  }
  if (filter?.expand) {
    formData.append('expand', filter.expand);
  }
  const params = new URLSearchParams(formData).toString();
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/rules?returnSize=true&${params}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Error when getting rules');
    }
  });
}

export function getRuleById(id, expand) {
  const extraParams = expand && `?expand=${expand}` || '';
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/rules/${id}${extraParams}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error(`Error retrieving rule by id ${id}`);
    }
  });
}

export function getEvents() {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/api/v1/events`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Error when getting events');
    }
  });
}


export function deleteRule(ruleId) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/rules/${ruleId}`, {
    method: 'DELETE',
    credentials: 'include',
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error('Response code indicates a server error', resp);
    }
  });
}

export function updateRule(rule) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/rules`, {
    method: 'PUT',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(rule),
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error(resp.status);
    }
  });
}

export function createRule(rule) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/rules`, {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(rule),
  }).then((resp) => {
    if (resp?.ok) {
      return resp.json();
    } else {
      throw new Error(resp.status);
    }
  });
}