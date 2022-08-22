export function getAllRealizations(fromDate, toDate, sortBy, sortDescending, offset, limit) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/realizations/api/allRealizations?fromDate=${fromDate}&toDate=${toDate}&sortBy=${sortBy}&sortDescending=${sortDescending}&offset=${offset || 0}&limit=${limit|| 10}`, {
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

export function exportFile(fromDate, toDate) {
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

export function getAllRealizationsStatus() {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/realizations/api/allRealizations?fromDate=2022-07-31T23:00:00.000Z&toDate=2022-08-22T22:59:00.000Z&sortBy=date&sortDescending=true&offset=0&limit=11`)
    .then(response => {
      return response.status === 200;
    })
    .catch(err => {
      throw new Error ('Server indicates that the response status code is not 200', err);
    });
    
}
