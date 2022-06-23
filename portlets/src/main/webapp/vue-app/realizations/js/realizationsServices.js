export function getAllRealizations(fromDate, toDate, itemsFilter, offset, limit, expand) {
  const formData = new FormData();
  if (itemsFilter) {
    Object.keys(itemsFilter).forEach(key => {
      const value = itemsFilter[key];
      if (value) {
        formData.append(key, value);
      }
    });
  }
  if (fromDate) {
    formData.append('fromDate', fromDate);
  }
  if (toDate) {
    formData.append('toDate', toDate);
  }
  if (expand) {
    formData.append('expand', expand);
  }
  if (offset) {
    formData.append('offset', offset);
  }
  if (limit) {
    formData.append('limit', limit);
  }
  const params = new URLSearchParams(formData).toString();
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/realizations/api/allRealizations?${params}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error when getting realizations');
    }
  });
}

export function updateRealization( id, status, actionLabel, domain ,points) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/realizations/api/updateRealizations?realizationId=${id}&status=${status}&actionLabel=${actionLabel || ''}&domain=${domain || ''}&points=${points || 0}`, {
    method: 'PUT',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json'
    },
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error updating realization status');
    }
  });
}

export function getReport(fromDate, toDate) {
  window.open(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/realizations/api/getExport?fromDate=${fromDate}&toDate=${toDate}`, '_blank');
}

export function getAllDomains() {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/domains`, {
    headers: {
      'Content-Type': 'application/json'
    },
    method: 'GET'
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error ('Server indicates an error while sending request');
    }
  });
}
