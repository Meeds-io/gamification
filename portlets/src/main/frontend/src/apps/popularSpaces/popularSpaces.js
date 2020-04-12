export function getUsersByGamificationRank() {
  return fetch( `/portal/rest/gamification/leaderboard/rank/all?loadCapacity=false`, {
    method: 'GET',
    credentials: 'include',
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
    credentials: 'include',
  }).then((resp) => {
    if(resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error ('Error when getting the user reputation status')
    }
  })
}
