export function getSpaceLeaderBord(period, limit) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/leaderboard/rank/all?earnerType=space&limit=${limit}&period=${period}`, {
    credentials: 'include',
    method: 'GET',
  }).then((resp) => {
    if(resp && resp.ok) {
      return resp.json();
    }  else {
      throw new Error ('Error when getting users by gamification leaderboard')
    }
  });
}

export function getSpace(spaceId) {
  return fetch(`/portal/rest/v1/social/spaces/${spaceId}`, {
    method: 'GET',
    credentials: 'include',
  }).then(resp => {
    if (!resp || !resp.ok) {
      throw new Error('Response code indicates a server error', resp);
    } else {
      return resp.json();
    }
  });
}


export function cancel(spaceId) {
  return fetch(`/portal/rest/homepage/intranet/spaces/cancel/${spaceId}`, {
    method: 'DELETE',
    credentials: 'include',
  }).then(resp => {
    if (!resp || !resp.ok) {
      throw new Error('Response code indicates a server error', resp);
    }
  });
}

export function join(spaceId) {
  return fetch(`/portal/rest/homepage/intranet/spaces/join/${spaceId}`, {
    method: 'GET',
    credentials: 'include',
  }).then(resp => {
    if (!resp || !resp.ok) {
      throw new Error('Response code indicates a server error', resp);
    }
  });
}

export function requestJoin(spaceId) {
  return fetch(`/portal/rest/homepage/intranet/spaces/request/${spaceId}`, {
    method: 'GET',
    credentials: 'include',
  }).then(resp => {
    if (!resp || !resp.ok) {
      throw new Error('Response code indicates a server error', resp);
    }
  });
}

export function accept(spaceId) {
  return fetch(`/portal/rest/homepage/intranet/spaces/accept/${spaceId}`, {
    method: 'GET',
    credentials: 'include',
  }).then(resp => {
    if (!resp || !resp.ok) {
      throw new Error('Response code indicates a server error', resp);
    }
  });
}

export function deny(spaceId) {
  return fetch(`/portal/rest/homepage/intranet/spaces/deny/${spaceId}`, {
    method: 'GET',
    credentials: 'include',
  }).then(resp => {
    if (!resp || !resp.ok) {
      throw new Error('Response code indicates a server error', resp);
    }
  });
}
