<script setup lang="ts">
import { PerfectScrollbar } from 'vue3-perfect-scrollbar'
import existingAbility from '@/plugins/casl/ability'
import { useAppAbility } from '@/plugins/casl/useAppAbility'
import { initialAbility } from '@/plugins/casl/ability'
import UsersList from './UsersList.vue'
const router = useRouter()
const ability = useAppAbility()
const userData = JSON.parse(localStorage.getItem('userData') || 'null')
const extractedName = ref('')
const isUserListVisible = ref(false)
if (userData)
  extractedName.value = userData.userName.split('__')[0];
const adminName = JSON.parse(localStorage.getItem('adminName') || 'null');
if (adminName) extractedName.value = adminName;
const logout = () => {
  // Remove "userData" from localStorage
  localStorage.removeItem('userData')
  localStorage.removeItem('adminName')
  // Remove "accessToken" from localStorage
  localStorage.removeItem('accessToken')
  localStorage.removeItem('userList')
  // Redirect to login page
  router.push('/login')
    .then(() => {
      // ℹ️ We had to remove abilities in then block because if we don't nav menu items mutation is visible while redirecting user to login page
      // Remove "userAbilities" from localStorage
      localStorage.removeItem('userAbilities')

      // Reset ability to initial ability
      ability.update(initialAbility)
    })
}
function switchUser() {
  isUserListVisible.value = true;
}
existingAbility.cannot('all', 'manage')
const userProfileList = [
  { type: 'divider' },
  // { type: 'navItem', icon: 'tabler-user', title: 'Profile', to: { name: 'apps-user-view-id', params: { id: 21 } } },
  // { type: 'navItem', icon: 'tabler-settings', title: 'Settings', to: { name: 'pages-account-settings-tab', params: { tab: 'account' } } },
  // { type: 'navItem', icon: 'tabler-credit-card', title: 'Billing', to: { name: 'pages-account-settings-tab', params: { tab: 'billing-plans' } }, badgeProps: { color: 'error', content: '3' } },
  // { type: 'divider' },
  // { type: 'navItem', icon: 'tabler-lifebuoy', title: 'Help', to: { name: 'pages-help-center' } },
  // { type: 'navItem', icon: 'tabler-currency-dollar', title: 'Pricing', to: { name: 'pages-pricing' } },
  // { type: 'navItem', icon: 'tabler-help', title: 'FAQ', to: { name: 'pages-faq' } },
  { type: 'navItem', icon: 'tabler-logout', title: 'Logout', onClick: logout },
]
if (ability.can('View', 'OCAdmin')) { //switch user visible only for admin
  userProfileList.push({ type: 'divider' })
  userProfileList.push({ type: 'navItem', icon: 'tabler-user', title: 'Switch Company', onClick: switchUser })

}
</script>

<template>
  <VBadge dot bordered location="bottom right" offset-x="3" offset-y="3" color="success">
    <VAvatar class="cursor-pointer" :color="!(userData && userData.avatar) ? 'primary' : undefined"
      :variant="!(userData && userData.avatar) ? 'tonal' : undefined">
      <VImg v-if="userData && userData.avatar" :src="userData.avatar" />
      <VIcon v-else icon="tabler-user" />

      <!-- SECTION Menu -->
      <VMenu activator="parent" width="230" location="bottom end" offset="14px">
        <VList>
          <VListItem>
            <template #prepend>
              <VListItemAction start>
                <VBadge dot location="bottom right" offset-x="3" offset-y="3" color="success" bordered>
                  <VAvatar :color="!(userData && userData.avatar) ? 'primary' : undefined"
                    :variant="!(userData && userData.avatar) ? 'tonal' : undefined">
                    <VImg v-if="userData && userData.avatar" :src="userData.avatar" />
                    <VIcon v-else icon="tabler-user" />
                  </VAvatar>
                </VBadge>
              </VListItemAction>
            </template>

            <VListItemTitle class="font-weight-medium">
              <!-- {{ userData ? userData.fullName || userData.username : '' }} -->
            </VListItemTitle>
            <VListItemSubtitle>{{ extractedName }}</VListItemSubtitle>
          </VListItem>

          <PerfectScrollbar :options="{ wheelPropagation: false }">
            <template v-for="item in userProfileList" :key="item.title">
              <VListItem v-if="item.type === 'navItem'" :to="item.to" @click="item.onClick && item.onClick()">
                <template #prepend>
                  <VIcon class="me-2" :icon="item.icon" size="22" />
                </template>

                <VListItemTitle>{{ item.title }}</VListItemTitle>

                <template v-if="item.badgeProps" #append>
                  <VBadge v-bind="item.badgeProps" />
                </template>
              </VListItem>

              <VDivider v-else class="my-2" />
            </template>
          </PerfectScrollbar>
        </VList>
      </VMenu>
      <!-- !SECTION -->
    </VAvatar>
  </VBadge>
  <UsersList v-model:isDialogVisible="isUserListVisible" />
</template>
