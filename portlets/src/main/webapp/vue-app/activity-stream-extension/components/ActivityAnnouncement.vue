<!--
This file is part of the Meeds project (https://meeds.io/).
Copyright (C) 2022 Meeds Association
contact@meeds.io
This program is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
You should have received a copy of the GNU Lesser General Public License
along with this program; if not, write to the Free Software Foundation,
Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
-->

<template>
  <activity-link
    :activity="activity"
    :activity-type-extension="announcementActivityTypeExtensionOptions"
    :is-activity-detail="isActivityDetail" />
</template>
<script>
export default {
  props: {
    activity: {
      type: Object,
      default: null,
    },
    activityTypeExtension: {
      type: String,
      default: null,
    },
    isActivityDetail: {
      type: Boolean,
      default: false,
    },
  },
  data: () => ({
    announcementActivityTypeExtensionOptions: {
      supportsThumbnail: true,
      useSameViewForMobile: true,
      thumbnailProperties: {
        height: '90px',
        width: '90px',
        noBorder: true,
      },
      getSourceLink: () => '#',
      getTitle: activity => {
        const announcementAssigneeUsername = activity?.templateParams?.announcementAssigneeUsername  || '';
        const announcementAssigneeFullName = activity?.templateParams?.announcementAssigneeFullName  || '';
        const title = `<a class="primary--text" href="${eXo.env.portal.context}/${eXo.env.portal.metaPortalName}/profile/${ announcementAssigneeUsername}">${ announcementAssigneeFullName}</a>`;

        return {
          key: 'challenges.succeededChallenge',
          params: { 0: `${ title }` }
        };
      },
      getThumbnail: () => '/gamification-portlets/skin/images/challengesAppIcon.png',
      getSummary: activity => activity?.templateParams?.announcementChallenge  || activity?.templateParams?.announcementDescription  || '',
    }
  }),
};
</script>