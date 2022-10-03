export function getAllRealizations(searchingKey, fromDate, toDate, earnerId, sortBy, sortDescending, offset, limit) {
  const formData = new FormData();
  if (searchingKey?.length > 0) {
    for (let index = 0; index < searchingKey.length; index++) {
      formData.append('searchingKey', searchingKey[index]);
    }

  }
  if (fromDate) {
    formData.append('fromDate', fromDate);
  }

  if (toDate) {
    formData.append('toDate', toDate);
  }
  if (earnerId) {
    formData.append('earnerId', earnerId);
  }
  if (sortBy) {
    formData.append('sortBy', sortBy);
  }
  if (sortDescending) {
    formData.append('sortDescending', sortDescending);
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

export function exportFile(fromDate, toDate, earnerId) {
  window.open(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/realizations/api/allRealizations?fromDate=${fromDate}&toDate=${toDate}&earnerId=${earnerId}&returnType=xlsx`, '_blank');
}

export function updateRealization( id, status, actionLabel, domain, points) {
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

export function getAllDomains() {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/domains?type=ALL`, {
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
