<script setup lang="ts">
import { EreceiptType } from '@/@fake-db/types'
interface props {
  data?: EreceiptType
}
const Props = withDefaults(defineProps<props>(), {
  data: {
    offersArray: {
      "offerBannerImages": "",
      "offerBannerRedirectUrls": ""
    },
    organization: {
      "userName": "NA"
    }

  }
});
const bannerImage = [];
const redirectUrls = [];
const offers = Props.data?.offersArray;
if (offers) {
  for (let i = 0; i < offers.length; i++) {
    bannerImage.push(offers[i].offerBannerImages);
    const redirectUrl = offers[i].offerBannerRedirectUrls;
    redirectUrls.push(redirectUrl)
  }
}
console.log("BannnerImages :" + bannerImage)
console.log("redirectUrls :" + redirectUrls)

function getUrl(url: string) {
    const utmParams = '?utm_source=optculture';
    return `${url}${utmParams}`;
}
</script>

<template>
  <VCard class="my-2" elevation="0">
    <VCardText class="pb-1">
      <div class="wr-text-h1 pb-3 text-center">Current Offers</div>
    </VCardText>
    <VCardText class="p-2" v-if="offers">
      <v-carousel cycle :show-arrows="false" hide-delimiter-background height="250px" :continuous="true">
        <v-carousel-item v-for="(item, i) in redirectUrls" :key="i" :src="item">
          <template v-if="item">
            <a :href="getUrl(item)" target="_blank">
              <v-img :src="bannerImage[i]" width="100%" contain max-height="200"></v-img>
            </a>
          </template>
          <template v-else>
            <v-img :src="bannerImage[i]" width="100%" contain max-height="200"></v-img>
          </template>
        </v-carousel-item>
      </v-carousel>
    </VCardText>
  </VCard>
</template>
