export function getRules(term, domainId, status, type, offset, limit) {
  const formData = new FormData();
  if (term) {
    formData.append('term', term);
  }
  if (domainId) {
    formData.append('domainId', domainId);
  }
  if (status) {
    formData.append('status', status);
  }
  if (type) {
    formData.append('type', type);
  }
  if (offset) {
    formData.append('offset', offset);
  }
  if (limit) {
    formData.append('limit', limit);
  }
  const params = new URLSearchParams(formData).toString();
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/rules?returnSize=true&${params}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error when getting rules');
    }
  });
}

export function getDomains() {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/domains`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error when getting domains');
    }
  });
}

export function getEvents() {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/api/v1/events`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error when getting events');
    }
  });
}


export function deleteRule(ruleId) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/rules/delete/${ruleId}`, {
    method: 'PUT',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error when removing rule');
    }
  });
}
export function updateRule(rule) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/rules/update`, {
    method: 'PUT',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(rule),
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error when updating rule');
    }
  });
}
export function createRule(rule) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/rules/add`, {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(rule),
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error when updating rule');
    }
  });
}







