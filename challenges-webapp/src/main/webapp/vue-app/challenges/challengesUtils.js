export function urlVerify(text) {
  return text &&  text.replace(/((?:href|src)=")?((((https?|ftp|file):\/\/)|www\.)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])/ig,
    function (matchedText, hrefOrSrc) {
      // the second group of the regex captures the html attribute 'html' or 'src',
      // so if it exists it means that it is already an html link or an image and it should not be converted
      if (hrefOrSrc) {
        return matchedText;
      }
      let url = matchedText;
      if (url.indexOf('www.') === 0) {
        url = `//${url}`;
      }
      return `<a href="${url}" target="_blank">${matchedText}</a>`;
    });
}


export function pad(n) {
  return n < 10 && `0${n}` || n;
}

export function toRFC3339(date, ignoreTime, useTimeZone) {
  if (!date) {
    return null;
  }
  if (typeof date === 'number') {
    date = new Date(date);
  } else if (typeof date === 'string') {
    if (date.indexOf('T') === 10 && date.length > 19) {
      date = date.substring(0, 19);
    }
    date = new Date(date);
  }
  let formattedDate;
  if (ignoreTime) {
    formattedDate = `${date.getFullYear()  }-${
      pad(date.getMonth() + 1)  }-${
      pad(date.getDate())  }T00:00:00`;
  } else {
    formattedDate = `${date.getFullYear()  }-${
      pad(date.getMonth() + 1)  }-${
      pad(date.getDate())  }T${
      pad(date.getHours())  }:${
      pad(date.getMinutes())  }:${
      pad(date.getSeconds())
    }`;
  }
  if (useTimeZone) {
    return `${formattedDate}${getUserTimezone()}`;
  }
  return formattedDate;
}

export function getUserTimezone() {
  const timeZoneOffset = - (new Date().getTimezoneOffset());
  let timezoneHours = timeZoneOffset / 60;
  let timezoneMinutes = timeZoneOffset % 60;
  timezoneHours = timezoneHours < 10 ? `0${timezoneHours}` : timezoneHours;
  timezoneMinutes = timezoneMinutes < 10 ? `0${timezoneMinutes}` : timezoneMinutes;
  const timezoneSign = timeZoneOffset >= 0 ? '+' : '-';
  return `${timezoneSign}${timezoneHours}:${timezoneMinutes}`;
}

export function getFromDate(date) {
  const lang = eXo.env.portal.language;
  const options = { month: 'long' };
  const day = String(date.getDate());
  const year = String(date.getFullYear());
  return `${date.toLocaleDateString(lang || 'en', options)} ${day}, ${year}` ;
}