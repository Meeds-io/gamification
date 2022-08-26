export function getAllRealizations(fromDate, toDate, earnerId, sortBy, sortDescending, offset, limit, returnType) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/realizations/api/allRealizations?fromDate=${fromDate}&toDate=${toDate}&earnerId=${earnerId}&sortBy=${sortBy}&sortDescending=${sortDescending}&offset=${offset || 0}&limit=${limit|| 10}&returnType=${returnType}`, {
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

export function exportFile(fromDate, toDate, earnerId, returnType) {
  window.open(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/realizations/api/allRealizations?fromDate=${fromDate}&toDate=${toDate}&earnerId=${earnerId}&returnType=${returnType}`, '_blank');
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
