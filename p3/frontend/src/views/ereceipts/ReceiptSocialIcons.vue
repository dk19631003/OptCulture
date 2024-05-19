<script setup lang="ts">
import type { EreceiptType } from "@/@fake-db/types"

interface Props {
  data?: EreceiptType
  itemData: {
    "id": 0, "name": "", "configuration": {
      "youtube": "NA",
      "facebook": "NA",
      "twitter": "NA",
      "instagram": "NA",
      "linkedin": "NA"
    }
  },

}

const props = withDefaults(defineProps<Props>(), {
  data: {
    organization: {
      "youtube": "NA",
      "facebook": "NA",
      "twitter": "NA",
      "instagram": "NA",
      "linkedin": "NA"
    }
  },
  itemData: {
    "id": 0, "name": "", "configuration": {
      "youtube": "NA",
      "facebook": "NA",
      "twitter": "NA",
      "instagram": "NA",
      "linkedin": "NA"
    }
  },
});

import { useSharedStore } from '@/store';
const store = useSharedStore();


</script>

<template>
  <VCard class="my-2" elevation="0">
    <span
      v-if="itemData.configuration.facebook || itemData.configuration.twitter || itemData.configuration.linkedin || itemData.configuration.instagram || itemData.configuration.youtube">
      <VCardText>
        <VListItemTitle class="text-center font-weight-bold wr-text-h1">Find us on your favourite social media
        </VListItemTitle>
      </VCardText>

      <VCardText class="d-flex justify-center">

        <div class="d-flex gap-3">
          <a :href="itemData.configuration.facebook" target="_blank" v-if="itemData.configuration.facebook">
            <VBtn icon class="circle" size="38">
              <img src="@/assets/images/media_icons/fb.png" alt="facebook"
                style="width: 100%; height: 100%;  border-radius: 50%;">
            </VBtn>
          </a>


          <a :href="itemData.configuration.instagram" target="_blank" v-if="itemData.configuration.instagram">
            <VBtn icon class="circle" size="38">
              <img src="@/assets/images/media_icons/insta.png" alt="instagram"
                style="width: 100%; height: 100%;  border-radius: 50%;">
            </VBtn>
          </a>

          <a :href="itemData.configuration.twitter" target="_blank" v-if="itemData.configuration.twitter">
            <VBtn icon class="circle" size="38">
              <img src="@/assets/images/media_icons/twitter.png" alt="twitter"
                style="width: 100%; height: 100%;  border-radius: 50%;">
            </VBtn>
          </a>

          <a :href="itemData.configuration.linkedin" target="_blank" v-if="itemData.configuration.linkedin">
            <VBtn icon class="circle" size="38">
              <img src="@/assets/images/media_icons/linkedin.png" alt="linkedin"
                style="width: 100%; height: 100%; border-radius: 50%;">
            </VBtn>
          </a>

          <a :href="itemData.configuration.youtube" target="_blank" v-if="itemData.configuration.youtube">
            <VBtn icon class="circle" size="38">
              <img src="@/assets/images/media_icons/youtube.png" alt="youtube"
                style="width: 100%; height: 100%; border-radius: 50%;">
            </VBtn>
          </a>

        </div>
      </VCardText>
    </span>
  </VCard>
</template>
