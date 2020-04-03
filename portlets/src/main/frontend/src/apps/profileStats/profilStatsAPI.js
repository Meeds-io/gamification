export function getUsersByGamificationRank() {
  return fetch( `/portal/rest/gamification/leaderboard/rank/all?loadCapacity=false`, {
    method: 'GET',
  }).then((resp) => {
    if(resp && resp.ok) {
      return resp.json();
    } 
    else {
      throw new Error ('Error when getting users by gamification rank')
    }
  })
}
export function getReputationStatus() {
  const url = window.location.pathname
  return fetch( `/rest/gamification/reputation/status?url=${url}`, {
    method: 'GET',
  }).then((resp) => {
    if(resp && resp.ok) {
      return resp.json();
    } 
    else {
      throw new Error ('Error when getting the user reputation status')
    }
  })
}

export function getUserInformations() {
  return fetch(`/portal/rest/v1/social/users/${eXo.env.portal.userName}`, {
    method: 'GET',
  }).then((resp) => {
    if(resp && resp.ok) {
      return resp.json();
    } 
    else {
      throw new Error ('Error when getting user informations');
    }
  })
}

export function getSpaces() {
  return fetch( '/portal/rest/v1/social/spaces?returnSize=true', {
    method: 'GET',
  }).then((resp) => {
    if(resp && resp.ok) {
      return resp.json();
    } 
    else {
      throw new Error ('Error when getting spaces');
    }
  })
}

export function getSpacesRequests() {
  return fetch( '/portal/rest/v1/social/spacesMemberships?status=invited&returnSize=true&limit=3', {
    method: 'GET',
  }).then((resp) => {
    if(resp && resp.ok) {
      return resp.json();
    } 
    else {
      throw new Error ('Error when getting spaces requests');
    }
  })
}

export function replyInvitationToJoinSpace(spaceMembershipId, reply) {
  const data = {status: `${reply}`}; 
  return fetch(`/portal/rest/v1/social/spacesMemberships/${spaceMembershipId}`, {
    method: 'PUT',
    body: JSON.stringify(data),
    headers: {
      'Content-Type': 'application/json'
    }
  }).then((resp) => {
    if(resp && resp.ok) {
      return resp.json();
    } 
    else {
      throw new Error ('Error when replying invitation to join space');
    }
  })
}

export function getConnections() {
  return fetch('/portal/rest/v1/social/relationships?status=confirmed&returnSize=true', {
    method: 'GET',
  }).then((resp) => {
    if(resp && resp.ok) {
      return resp.json();
	} 
	else {
      throw new Error ('Error when getting connections');
	}
  })
}

export function getConnectionsRequests() {
  return fetch('/portal/rest/v1/social/relationships?status=incoming&returnSize=true&limit=3', {
    method: 'GET',
  }).then((resp) => {
    if(resp && resp.ok) {
      return resp.json();
	} 
	else {
      throw new Error ('Error when getting connections requests');
    }
  })
}

export function getConnectionRequestSender(senderUrl) {
  return fetch(`${senderUrl}`, {
    method: 'GET',
  }).then((resp) => {
    if(resp && resp.ok) {
      return resp.json();
    } 
    else {
      throw new Error ('Error when getting connection request sender');
	}
  })
}

export function replyInvitationToConnect(relationId, reply) {
  const data = {status: `${reply}`}; 
  return fetch(`/portal/rest/v1/social/relationships/${relationId}`, {
    method: 'PUT',
	body: JSON.stringify(data),
	headers: {
      'Content-Type': 'application/json'
	}
  }).then((resp) => {
    if(resp && resp.ok) {
      return resp.json();
	} 
	else {
      throw new Error ('Error when replying invitation to connect');
	}
  })
}

export function getGamificationPoints() {
  return fetch(`/portal/rest/gamification/api/v1/points?userId=${eXo.env.portal.userName}`, {
    method: 'GET',
  }).then((resp) => {
    if(resp && resp.ok) {
      return resp.json();
    }
    else {
      throw new Error ('Error when getting gamification points');
    }
  })
}

export function getGamificationPointsStats() {
  return fetch(`/portal/rest/gamification/leaderboard/stats?username=${eXo.env.portal.userName}`, {
    method: 'GET',
  }).then((resp) => {
    if(resp && resp.ok) {
      return resp.json();
    }
    else {
      throw new Error ('Error when getting gamification points stats');
    }
  })
}

export function getCommonConnections(relationId) {
  return fetch(`/portal/rest/v1/social/identities/${relationId}/commonConnections?returnSize=true`, {
    method: 'GET',
  }).then((resp) => {
    if(resp && resp.ok) {
      return resp.json();
    } 
    else {
      throw new Error ('Error when getting common connections');
    }
  })
}
