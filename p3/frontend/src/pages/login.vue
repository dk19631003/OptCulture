<script setup lang="ts">
import { VForm } from 'vuetify/components/VForm'
import type { LoginResponse } from '@/@fake-db/types'
import { useAppAbility } from '@/plugins/casl/useAppAbility'
import axios from '@axios'
import { useGenerateImageVariant } from '@core/composable/useGenerateImageVariant'
import authV2LoginIllustrationBorderedDark from '@images/pages/auth-v2-login-illustration-bordered-dark.png'
import authV2LoginIllustrationBorderedLight from '@images/pages/auth-v2-login-illustration-bordered-light.png'
import authV2LoginIllustrationDark from '@images/pages/auth-v2-login-illustration-dark.png'
import authV2LoginIllustrationLight from '@images/pages/auth-v2-login-illustration-light.png'
import authV2MaskDark from '@images/pages/misc-mask-dark.png'
import authV2MaskLight from '@images/pages/misc-mask-light.png'
import { VNodeRenderer } from '@layouts/components/VNodeRenderer'
import { themeConfig } from '@themeConfig'
import { requiredValidator } from '@validators'

const authThemeImg = useGenerateImageVariant(authV2LoginIllustrationLight, authV2LoginIllustrationDark, authV2LoginIllustrationBorderedLight, authV2LoginIllustrationBorderedDark, true)

const authThemeMask = useGenerateImageVariant(authV2MaskLight, authV2MaskDark)

const isPasswordVisible = ref(false)
const isSessionExpired = ref(false);
const route = useRoute()
const router = useRouter()

const ability = useAppAbility()

const errors = ref<Record<string, string | undefined>>({
  username: undefined,
  password: undefined,
  organisationId: undefined,
})

const refVForm = ref<VForm>()
const username = ref('')
const password = ref('')
const organisationId = ref('')
const rememberMe = ref(false)
const isValidDetails = ref(true);
isSessionExpired.value = JSON.parse(localStorage.getItem('sessionExpired')) ? true : false
const login = () => {

  axios.post<LoginResponse>('/api/login/authenticate', { userName: username.value, password: password.value, organizationId: organisationId.value })
    .then(r => {
      isValidDetails.value = r.data.pwdMatch
      const abilities = r.data.userAbilities;
      abilities.push({
        action: 'read',
        subject: 'Auth',
      })
      const userdata = JSON.stringify(r.data.userData);
      const token = JSON.stringify(r.data.token);
      console.log("abilitires " + abilities + " " + userdata + " " + token);
      localStorage.setItem('userAbilities', JSON.stringify(r.data.userAbilities))
      ability.update(abilities)

      localStorage.setItem('userData', JSON.stringify(r.data.userData))
      localStorage.setItem('accessToken', JSON.stringify(r.data.token))


      localStorage.removeItem('sessionExpired')
      localStorage.setItem('expirationTimeMilli', JSON.stringify(r.data.expirationTime))
      // Redirect to `to` query if exist or redirect to index route
      if (ability.can('View', 'OCAdmin')) {  //if oc admin redirect dashboard
        router.replace(route.query.to ? String(route.query.to) : '/')
      }
      else router.replace('customers') //else redirect customers 
    })
    .catch(e => {
      // const { errors: formErrors } = e.response.data

      isValidDetails.value = false;
      // errors.value = formErrors
      // console.error(e.response.data)
    })
  removeSegmentLocalStorage();
}

// REMOVING SEGMENTS FROM LOCAL-STORAGE ON LOGIN
function removeSegmentLocalStorage() {
  for (let i = 0; i < localStorage.length; i++) {
    const key = localStorage.key(i);
    if (key.startsWith('EditedSegment')) {
      localStorage.removeItem(key);
    }
  }
}

const onSubmit = () => {
  refVForm.value?.validate()
    .then(({ valid: isValid }) => {
      if (isValid)
        login()
    })
}
</script>

<template>
  <VRow no-gutters class="auth-wrapper bg-surface">
    <VCol lg="8" class="d-none d-lg-flex">
      <div class="position-relative bg-background rounded-lg w-100 ma-8 me-0">
        <div class="d-flex align-center justify-center w-100 h-100">
          <VImg max-width="505" :src="authThemeImg" class="auth-illustration mt-16 mb-2" />
        </div>

        <VImg :src="authThemeMask" class="auth-footer-mask" />
      </div>
    </VCol>

    <VCol cols="12" lg="4" class="auth-card-v2 d-flex align-center justify-center">
      <VCard flat :max-width="500" class="mt-12 mt-sm-0 pa-4">
        <VCardText>
          <VNodeRenderer :nodes="themeConfig.app.logo" class="mb-6" />

          <h5 class="text-h5 mb-1">
            Welcome to <span class="text-capitalize"> {{ themeConfig.app.title }} </span>! 👋🏻
          </h5>
          <p class="mb-4">
            Account Login
          </p>
          <v-alert v-model="isSessionExpired" variant="tonal" color="error" icon="$warning" prominent> Your session
            expired, please login. </v-alert>
        </VCardText>
        <VCardText>
          <p class="text-body-2 text-red mt-n2" v-if="!isValidDetails">Please provide valid credentials</p>

          <VForm ref="refVForm" @submit.prevent="onSubmit">
            <VRow>
              <!-- email -->
              <VCol cols="12">
                <AppTextField v-model="username" label="User Name" type="text" autofocus :rules="[requiredValidator]" />
              </VCol>

              <VCol cols="12">
                <AppTextField v-model="organisationId" label="Organization" type="text" :rules="[requiredValidator]" />
              </VCol>

              <!-- password -->
              <VCol cols="12">
                <AppTextField v-model="password" label="Password" :rules="[requiredValidator]"
                  :type="isPasswordVisible ? 'text' : 'password'" :error-messages="errors.password"
                  :append-inner-icon="isPasswordVisible ? 'tabler-eye-off' : 'tabler-eye'"
                  @click:append-inner="isPasswordVisible = !isPasswordVisible" class="mb-3" />
                <VBtn block type="submit">
                  Login
                </VBtn>
              </VCol>
            </VRow>
          </VForm>
        </VCardText>
      </VCard>
    </VCol>
  </VRow>
</template>

<style lang="scss">
@use "@core/scss/template/pages/page-auth.scss";
</style>

<route lang="yaml">
meta:
  layout: blank
  action: read
  subject: Auth
  redirectIfLoggedIn: true
</route>
