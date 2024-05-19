<script setup lang="ts">
import { VNodeRenderer } from '@layouts/components/VNodeRenderer'
import { themeConfig } from '@themeConfig'
import { EreceiptType } from '@/@fake-db/types'
import { useTheme } from 'vuetify'

// import type { ThemeSwitcherTheme } from '@layouts/types'


interface Props {
  data?: EreceiptType
}

const props = withDefaults(defineProps<Props>(), {
  data: {
    organization: {
      "companyName": "Brand Name"
    },
    branding: {
      "companyLogo": "",
      "fontName": "Arial",
      "fontURL": "",
      "coverImage": "",
      "logoImage": "",
      "homePageColorCode": "",
      "balanceCardThemeColor": "",
      "balanceCardTextColor": "",
      "bannerName": "",
      "currencySymbol": ""
    },
    storeDetails: {
      "storeName": ""
    }
  }
});


onMounted(() => {
  if (props.data?.branding.homePageColorCode) {
    useTheme().themes.value.light.colors.primary = props.data?.branding.homePageColorCode;
    useTheme().themes.value.dark.colors.primary = props.data?.branding.homePageColorCode;
  }
})

//Disabled Theme option

// const themes: ThemeSwitcherTheme[] = [
//   {
//     name: 'light',
//     icon: 'tabler-sun-high',
//   },
//   {
//     name: 'dark',
//     icon: 'tabler-moon',
//   },
// ]
</script>

<template>
  <VCard class="my-2" elevation="0">
    <div class="d-flex ma-3">
      <span class="ml-0"></span>
      <VSpacer />
      <img :src="data.branding.companyLogo" v-if="data.branding.companyLogo" style="max-width: 50%;max-height:100px;" />
      <span class="text-h6 text-center wr-text-h1" v-else>{{ data.organization.companyName }}</span>
      <VSpacer />

      <!-- Disabled Theme option -->
      <!-- <ThemeSwitcher :themes="themes" class="mr-2" /> -->
    </div>
    <!-- <div class="text-center" v-if="data.storeDetails">{{ data.storeDetails.storeName }}</div> -->
  </VCard>
</template>
