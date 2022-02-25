export function canAddChallenge() {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/challenge/api/canAddChallenge`, {
    headers: {
      'Content-Type': 'text/plain'
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

export function saveChallenge(challenge) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/challenge/api/addChallenge`, {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(challenge),
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error saving challenge');
    }
  });
}

export function updateChallenge(challenge) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/challenge/api/updateChallenge`, {
    method: 'PUT',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(challenge),
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error updating challenge');
    }
  });
}

export function getAllChallengesByUser(offset, limit, announcements) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/challenge/api/allChallenge?offset=${offset || 0}&limit=${limit|| 10}&announcements=${announcements|| 2}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error when getting challenges');
    }
  });
}

export function getAllAnnouncementsByChallenge(challengeId, offset, limit) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/announcement/api/ByChallengeId/${challengeId}?offset=${offset || 0}&limit=${limit|| 10}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error when getting challenges');
    }
  });
}

export function saveAnnouncement(announcement) {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/announcement/api/addAnnouncement`, {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(announcement),
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error saving announcement');
    }
  });
}

export function getChallengeById(id)
{
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/challenge/api/${id}`, {
    method: 'GET',
    credentials: 'include',
  }).then((resp) => {
    if (resp && resp.ok) {
      return resp.json();
    } else {
      throw new Error('Error when getting challenge');
    }
  });
}

export function getAllDomains() {
  return fetch(`${eXo.env.portal.context}/${eXo.env.portal.rest}/gamification/domains`, {
    headers: {
      'Content-Type': 'text/plain'
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

